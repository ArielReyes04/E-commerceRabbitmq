package ec.edu.espe.e_comerce_core.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StockResponseDto {
    private String eventType; // StockReserved | StockRejected
    private String orderId;
    private String correlationId;

    private List<ReservedItemResponseDto> reservedItems;
    private LocalDateTime reservedAt;

    private String reason;
    private LocalDateTime rejectedAt;

    @Data
    @Builder
    public static class ReservedItemResponseDto {
        private String productId;
        private int quantity;
    }
}