package ec.edu.espe.OrderService.services;

import ec.edu.espe.OrderService.dto.request.OrderRequestDto;
import ec.edu.espe.OrderService.dto.response.OrderResponseDto;

import java.util.UUID;

public interface OrderService {
    // Requerimiento: Endpoint 1 - Crear pedido (POST)
    OrderResponseDto createOrder(OrderRequestDto createOrderDto);

    // Requerimiento: Endpoint 2 - Consultar pedido (GET)
    OrderResponseDto getOrderById(UUID orderId);
}