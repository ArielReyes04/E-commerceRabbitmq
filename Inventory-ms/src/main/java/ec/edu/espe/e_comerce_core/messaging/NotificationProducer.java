package ec.edu.espe.e_comerce_core.messaging;

import ec.edu.espe.e_comerce_core.dto.response.StockResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducer {

    private final RabbitTemplate rabbitTemplate;

    // Leemos del YAML
    @Value("${custom.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${custom.rabbitmq.routing-key.stock-reserved}")
    private String routingKeyReserved;

    @Value("${custom.rabbitmq.routing-key.stock-rejected}")
    private String routingKeyRejected;

    public void sendStockResponse(StockResponseDto response, boolean isSuccess) {
        String routingKey = isSuccess ? routingKeyReserved : routingKeyRejected;

        try {
            log.info("Enviando evento {} a exchange {} con key {}",
                    response.getEventType(), exchangeName, routingKey);

            rabbitTemplate.convertAndSend(exchangeName, routingKey, response);

        } catch (Exception e) {
            log.error("Error enviando notificación de stock: {}", e.getMessage(), e);
        }
    }
}