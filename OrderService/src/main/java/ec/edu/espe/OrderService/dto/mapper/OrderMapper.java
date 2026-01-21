package ec.edu.espe.OrderService.dto.mapper;

import ec.edu.espe.OrderService.dto.request.OrderRequestDto;
import ec.edu.espe.OrderService.dto.response.OrderItemResponseDto;
import ec.edu.espe.OrderService.dto.response.OrderResponseDto;
import ec.edu.espe.OrderService.models.Order;
import ec.edu.espe.OrderService.models.OrderItem;
import ec.edu.espe.OrderService.models.OrderStatus;
import ec.edu.espe.OrderService.models.ShippingAddress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    // Convierte de RequestDTO a Entidad Order
    public Order toEntity(OrderRequestDto dto) {
        Order order = Order.builder()
                .customerId(java.util.UUID.fromString(dto.getCustomerId()))
                .paymentReference(dto.getPaymentReference())
                .status(OrderStatus.PENDING) // Estado inicial obligatorio [cite: 77-78]
                .shippingAddress(ShippingAddress.builder()
                        .country(dto.getShippingAddress().getCountry())
                        .city(dto.getShippingAddress().getCity())
                        .street(dto.getShippingAddress().getStreet())
                        .postalCode(dto.getShippingAddress().getPostalCode())
                        .build())
                .build();

        // Mapear los items y asignar la relación bidireccional
        List<OrderItem> items = dto.getItems().stream().map(itemDto ->
                OrderItem.builder()
                        .productId(java.util.UUID.fromString(itemDto.getProductId()))
                        .quantity(itemDto.getQuantity())
                        .order(order) // Importante: Vincular con el padre
                        .build()
        ).collect(Collectors.toList());

        order.setItems(items);
        return order;
    }

    // Convierte de Entidad Order a ResponseDTO
    public OrderResponseDto toDto(Order entity) {
        List<OrderItemResponseDto> itemsDto = entity.getItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        // Mensaje dinámico según el estado
        String message = (entity.getStatus() == OrderStatus.PENDING)
                ? "Order received. Inventory check in progress."
                : null;

        return OrderResponseDto.builder()
                .orderId(entity.getOrderId())
                .customerId(entity.getCustomerId())
                .status(entity.getStatus())
                .items(itemsDto)
                .updatedAt(entity.getUpdatedAt())
                .message(message)
                // Si tuvieras un campo 'reason' en la entidad para rechazos, lo mapearías aquí
                .build();
    }
}
