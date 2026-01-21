package ec.edu.espe.e_comerce_core.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductStockResponseDto {
    private String productId;
    private Integer availableStock;
    private Integer reservedStock;
    private LocalDateTime updatedAt;
}