package ec.edu.espe.OrderService.controllers;

import ec.edu.espe.OrderService.dto.request.OrderRequestDto;
import ec.edu.espe.OrderService.dto.response.OrderResponseDto;
import ec.edu.espe.OrderService.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Endpoint 1: Crear pedido (POST)
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderRequestDto request) {
        OrderResponseDto response = orderService.createOrder(request);
        // Retornamos 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint 2: Consultar pedido (GET)
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable UUID orderId) {
        try {
            OrderResponseDto response = orderService.getOrderById(orderId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Manejo básico de excepción si no encuentra la orden
            return ResponseEntity.notFound().build();
        }
    }
}