package ec.edu.espe.OrderService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ShippingAddressRequestDto {

    @NotBlank(message = "El país es obligatorio")
    @Size(max = 2, message = "El código de país debe tener 2 letras (ej: EC)")
    private String country;
    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "La calle principal es obligatoria")
    private String street;

    @NotBlank(message = "El código postal es obligatorio")
    @Pattern(regexp = "\\d{6}", message = "El código postal debe tener 6 dígitos")
    private String postalCode;
}
