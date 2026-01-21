package ec.edu.espe.OrderService.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequestDto {

    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID customerId;

    @NotEmpty(message = "El pedido debe contener al menos un ítem")
    @Valid // Importante: Valida cada objeto dentro de la lista
    private List<OrderItemRequestDto> items;

    @NotNull(message = "La dirección de envío es obligatoria")
    @Valid // Importante: Valida el objeto anidado
    private ShippingAddressRequestDto shippingAddress;

    @NotBlank(message = "La referencia de pago es obligatoria")
    private String paymentReference;
}