package ec.edu.espe.OrderService.dto.messaging;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderCreatedEvent {
    private String eventType; // "OrderCreated"
    private UUID orderId;
    private UUID correlationId;
    private LocalDateTime createdAt;
    private List<OrderItemEvent> items;

    @Data
    @Builder
    public static class OrderItemEvent {
        private UUID productId;
        private int quantity;
    }
}