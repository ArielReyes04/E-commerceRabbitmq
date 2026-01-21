package ec.edu.espe.OrderService.controllers;

import ec.edu.espe.OrderService.dto.request.OrderRequestDto;
import ec.edu.espe.OrderService.dto.response.OrderResponseDto;
import ec.edu.espe.OrderService.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Validated
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // Endpoint 1: Crear pedido
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @Valid @RequestBody OrderRequestDto dto) {
        log.info("Solicitud para crear un nuevo pedido. Cliente: {}, Ref: {}",
                dto.getCustomerId(), dto.getPaymentReference());

        OrderResponseDto response = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint 2: Consultar pedido [cite: 84]
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID orderId) {
        log.info("Solicitud para consultar el estado del pedido: {}", orderId);

        OrderResponseDto response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }
}