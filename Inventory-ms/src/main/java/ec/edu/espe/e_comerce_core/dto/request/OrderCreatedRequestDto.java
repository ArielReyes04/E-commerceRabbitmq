package ec.edu.espe.e_comerce_core.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderCreatedRequestDto {

    @NotBlank(message = "El tipo de evento es requerido")
    private String eventType;

    @NotBlank(message = "El Order ID es requerido")
    private String orderId;

    @NotBlank(message = "El Correlation ID es requerido")
    private String correlationId;

    @Valid // Valida los objetos dentro de la lista
    @NotNull(message = "La lista de items no puede ser nula")
    private List<OrderItemRequestDto> items;

    @Data
    public static class OrderItemRequestDto {
        @NotBlank(message = "El ID del producto es requerido")
        private String productId;

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private int quantity;
    }
}