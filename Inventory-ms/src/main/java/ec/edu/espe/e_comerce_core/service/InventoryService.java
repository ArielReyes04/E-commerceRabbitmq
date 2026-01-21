package ec.edu.espe.e_comerce_core.service;

import ec.edu.espe.e_comerce_core.dto.request.OrderCreatedRequestDto;
import ec.edu.espe.e_comerce_core.dto.response.ProductStockResponseDto;

public interface InventoryService {
    void processOrderEvent(OrderCreatedRequestDto orderEvent);
    ProductStockResponseDto getProductStock(String productId);
}
