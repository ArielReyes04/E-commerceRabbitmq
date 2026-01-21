package ec.edu.espe.e_comerce_core.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StockResponseDto {
    private String eventType; // StockReserved | StockRejected
    private UUID orderId;
    private UUID correlationId;

    private List<ReservedItemResponseDto> reservedItems;
    private LocalDateTime reservedAt;

    private String reason;
    private LocalDateTime rejectedAt;

    @Data
    @Builder
    public static class ReservedItemResponseDto {
        private UUID productId;
        private int quantity;
    }
}