package ec.edu.espe.e_comerce_core.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ProductStockResponseDto {
    private UUID productId;
    private Integer availableStock;
    private Integer reservedStock;
    private LocalDateTime updatedAt;
}