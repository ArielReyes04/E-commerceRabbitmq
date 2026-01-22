package ec.edu.espe.OrderService.dto.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockResponseEvent {
    private String eventType; // "StockReserved" o "StockRejected"
    private UUID orderId;
    private UUID correlationId;
    private String reason;    // Solo si es Rejected
}