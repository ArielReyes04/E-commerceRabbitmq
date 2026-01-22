package ec.edu.espe.OrderService.services.imp;

import ec.edu.espe.OrderService.dto.mapper.OrderMapper;
import ec.edu.espe.OrderService.dto.messaging.OrderCreatedEvent;
import ec.edu.espe.OrderService.dto.messaging.StockResponseEvent;
import ec.edu.espe.OrderService.dto.request.OrderRequestDto;
import ec.edu.espe.OrderService.dto.response.OrderResponseDto;
import ec.edu.espe.OrderService.messaging.OrderProducer;
import ec.edu.espe.OrderService.models.Order;
import ec.edu.espe.OrderService.models.OrderStatus;
import ec.edu.espe.OrderService.repositories.OrderRepository;
import ec.edu.espe.OrderService.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderProducer orderProducer;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) {
        log.info("Creando nueva orden para cliente: {}", request.getCustomerId());

        // 1. Convertir DTO a Entidad y guardar (Estado inicial: PENDING)
        Order order = orderMapper.toEntity(request);
        order = orderRepository.save(order);

        // 2. Construir el evento para RabbitMQ
        UUID correlationId = UUID.randomUUID();

        List<OrderCreatedEvent.OrderItemEvent> itemsEvent = order.getItems().stream()
                .map(item -> OrderCreatedEvent.OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventType("OrderCreated")
                .orderId(order.getOrderId())     // Ya es UUID en la entidad
                .correlationId(correlationId)    // CAMBIO: Pasamos UUID puro
                .createdAt(LocalDateTime.now())
                .items(itemsEvent)
                .build();

        // 3. Publicar el evento asíncronamente
        orderProducer.publishOrderCreated(event);

        // 4. Retornar respuesta inmediata (PENDING)
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        OrderResponseDto response = orderMapper.toDto(order);

        // Mapear la razón si fue cancelada
        if (order.getStatus() == OrderStatus.CANCELLED) {
            response.setReason(order.getRejectionReason());
        }

        return response;
    }

    @Override
    @Transactional
    public void processInventoryResponse(StockResponseEvent event) {
        // CAMBIO: Usamos getOrderId() directamente porque ya es un UUID.
        // Eliminamos la conversión UUID.fromString()
        log.info("Procesando respuesta de inventario para orden: {}", event.getOrderId());

        UUID orderId = event.getOrderId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada al procesar evento: " + orderId));

        if ("StockReserved".equals(event.getEventType())) {
            order.setStatus(OrderStatus.CONFIRMED);
            log.info("Orden {} confirmada.", orderId);
        } else if ("StockRejected".equals(event.getEventType())) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setRejectionReason(event.getReason());
            log.warn("Orden {} rechazada. Razón: {}", orderId, event.getReason());
        }

        orderRepository.save(order);
    }
}