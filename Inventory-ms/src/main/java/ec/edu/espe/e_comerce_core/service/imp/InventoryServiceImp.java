package ec.edu.espe.e_comerce_core.service.imp;

import ec.edu.espe.e_comerce_core.dto.mapper.InventoryMapper;
import ec.edu.espe.e_comerce_core.dto.request.OrderCreatedRequestDto;
import ec.edu.espe.e_comerce_core.dto.response.ProductStockResponseDto;
import ec.edu.espe.e_comerce_core.dto.response.StockResponseDto;
import ec.edu.espe.e_comerce_core.messaging.NotificationProducer;
import ec.edu.espe.e_comerce_core.model.ProductStock;
import ec.edu.espe.e_comerce_core.repository.ProductStockRepository;
import ec.edu.espe.e_comerce_core.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImp implements InventoryService {

    private final ProductStockRepository stockRepository;
    private final NotificationProducer notificationProducer;
    private final InventoryMapper inventoryMapper;

    @RabbitListener(queues = "${custom.rabbitmq.queue.order-created}")
    @Transactional
    public void processOrderEvent(OrderCreatedRequestDto orderEvent) {
        log.info("Procesando orden recibida: {}", orderEvent.getOrderId());

        boolean isStockAvailable = true;
        String reason = "";

        // 1. Validar Stock
        for (OrderCreatedRequestDto.OrderItemRequestDto item : orderEvent.getItems()) {
            Optional<ProductStock> productOpt = stockRepository.findById(item.getProductId());

            if (productOpt.isEmpty()) {
                isStockAvailable = false;
                reason = "Producto no encontrado: " + item.getProductId();
                break;
            }

            if (productOpt.get().getAvailableStock() < item.getQuantity()) {
                isStockAvailable = false;
                reason = "Stock insuficiente para producto: " + item.getProductId(); // [cite: 297]
                break;
            }
        }

        // 2. Acciones y Notificación
        if (isStockAvailable) {
            // Decrementar stock
            orderEvent.getItems().forEach(item -> {
                ProductStock product = stockRepository.findById(item.getProductId()).get();
                product.setAvailableStock(product.getAvailableStock() - item.getQuantity());
                product.setReservedStock(product.getReservedStock() + item.getQuantity());
                stockRepository.save(product);
            });

            StockResponseDto response = inventoryMapper.toStockReserved(orderEvent);
            notificationProducer.sendStockResponse(response, true);
        } else {
            StockResponseDto response = inventoryMapper.toStockRejected(orderEvent, reason);
            notificationProducer.sendStockResponse(response, false);
        }
    }

    @Transactional(readOnly = true)
    public ProductStockResponseDto getProductStock(String productId) {
        return stockRepository.findById(productId)
                .map(inventoryMapper::toProductStockResponse)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productId));
    }
}