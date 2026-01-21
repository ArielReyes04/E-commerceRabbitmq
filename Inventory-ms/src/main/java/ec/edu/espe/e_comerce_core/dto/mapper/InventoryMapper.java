package ec.edu.espe.e_comerce_core.dto.mapper;

import ec.edu.espe.e_comerce_core.dto.request.OrderCreatedRequestDto;
import ec.edu.espe.e_comerce_core.dto.response.ProductStockResponseDto;
import ec.edu.espe.e_comerce_core.dto.response.StockResponseDto;
import ec.edu.espe.e_comerce_core.model.ProductStock;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryMapper {


    public StockResponseDto toStockReserved(OrderCreatedRequestDto request) {
        List<StockResponseDto.ReservedItemResponseDto> items = request.getItems().stream()
                .map(item -> StockResponseDto.ReservedItemResponseDto.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return StockResponseDto.builder()
                .eventType("StockReserved")
                .orderId(request.getOrderId())
                .correlationId(request.getCorrelationId())
                .reservedItems(items)
                .reservedAt(LocalDateTime.now())
                .build();
    }

    // Convierte Request -> Response (FALLO)
    public StockResponseDto toStockRejected(OrderCreatedRequestDto request, String reason) {
        return StockResponseDto.builder()
                .eventType("StockRejected")
                .orderId(request.getOrderId())
                .correlationId(request.getCorrelationId())
                .reason(reason)
                .rejectedAt(LocalDateTime.now())
                .build();
    }

    public ProductStockResponseDto toProductStockResponse(ProductStock entity) {
        return ProductStockResponseDto.builder()
                .productId(entity.getProductId())
                .availableStock(entity.getAvailableStock())
                .reservedStock(entity.getReservedStock())
                // Como no guardamos fecha en BD, devolvemos la fecha/hora actual de la consulta
                .updatedAt(LocalDateTime.now())
                .build();
    }
}