package ec.edu.espe.OrderService.messaging;

import ec.edu.espe.OrderService.dto.messaging.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    // Asegúrate de tener estas variables en tu application.yml del OrderService
    @Value("${custom.rabbitmq.exchange}")
    private String exchange;

    @Value("${custom.rabbitmq.routing-key.order-created}")
    private String routingKey;

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Publicando evento OrderCreated para ID: {}", event.getOrderId());
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (Exception e) {
            log.error("Error al publicar evento en RabbitMQ: {}", e.getMessage());
            // Aquí podrías manejar lógica de compensación o reintento
        }
    }
}
