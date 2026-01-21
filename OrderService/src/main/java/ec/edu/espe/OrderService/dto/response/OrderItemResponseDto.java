package ec.edu.espe.OrderService.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderItemResponseDto {
    private UUID productId;
    private Integer quantity;
}
