package ec.edu.espe.OrderService.dto.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseEvent {
    private String eventType; // "StockReserved" o "StockRejected"
    private UUID orderId;
    private UUID correlationId;
    private String reason;    // Solo si es Rejected
}