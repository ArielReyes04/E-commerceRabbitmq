package ec.edu.espe.OrderService.messaging;

import ec.edu.espe.OrderService.dto.messaging.StockResponseEvent;
import ec.edu.espe.OrderService.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderListener {

    private final OrderService orderService;

    // Escucha la cola definida en el yaml
    @RabbitListener(queues = "${custom.rabbitmq.queue.order-replies}")
    public void handleStockUpdate(StockResponseEvent event) {
        log.info("Evento de inventario recibido: {} para OrderID: {}",
                event.getEventType(), event.getOrderId());

        try {
            // Delegamos la lógica al servicio que ya modificamos
            orderService.processInventoryResponse(event);
        } catch (Exception e) {
            log.error("Error al procesar la respuesta de inventario: {}", e.getMessage(), e);
            // Aquí podrías manejar una Dead Letter Queue (DLQ) si fuera necesario
        }
    }
}
