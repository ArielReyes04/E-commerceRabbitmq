package ec.edu.espe.e_comerce_core.controller;


import ec.edu.espe.e_comerce_core.dto.response.ProductStockResponseDto;
import ec.edu.espe.e_comerce_core.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final InventoryService inventoryService;

    // Endpoint 1: Consultar stock de un producto
    @GetMapping("/{productId}/stock")
    public ResponseEntity<ProductStockResponseDto> getProductStock(@PathVariable UUID productId) {
        try {
            ProductStockResponseDto response = inventoryService.getProductStock(productId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // devolver 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }
}