package ec.edu.espe.OrderService.dto.response;

import ec.edu.espe.OrderService.models.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponseDto {

    // Campos obligatorios / comunes
    private UUID orderId;
    private OrderStatus status;

    // Campos informativos (usados en PENDING o creación)
    private String message;

    // Campos de detalle (usados en GET /orders/{id})
    private UUID customerId;
    private List<OrderItemResponseDto> items;
    private LocalDateTime updatedAt;

    // Campo específico para errores o rechazos (StockRejected)
    private String reason;
}
