package ec.edu.espe.OrderService.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class OrderItemRequestDto {

    @NotNull(message = "El productId es obligatorio")
    private UUID productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    private Integer quantity;
}
