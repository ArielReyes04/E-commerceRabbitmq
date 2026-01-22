package ec.edu.espe.OrderService.services;

import ec.edu.espe.OrderService.dto.messaging.StockResponseEvent;
import ec.edu.espe.OrderService.dto.request.OrderRequestDto;
import ec.edu.espe.OrderService.dto.response.OrderResponseDto;

import java.util.UUID;

public interface OrderService {
    // Para el endpoint POST /orders
    OrderResponseDto createOrder(OrderRequestDto request);

    // Para el endpoint GET /orders/{id}
    OrderResponseDto getOrderById(UUID orderId);

    // Para procesar la respuesta asíncrona (lo llamará el Listener más adelante)
    void processInventoryResponse(StockResponseEvent event);
}