package ec.edu.espe.OrderService.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${custom.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${custom.rabbitmq.queue.order-replies}")
    private String repliesQueueName;

    @Value("${custom.rabbitmq.routing-key.stock-reserved}")
    private String stockReservedKey;

    @Value("${custom.rabbitmq.routing-key.stock-rejected}")
    private String stockRejectedKey;

    // 1. El Exchange (debe llamarse igual que en Inventory)
    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(exchangeName);
    }

    // 2. La Cola donde llegan las respuestas (Responses)
    @Bean
    public Queue repliesQueue() {
        return QueueBuilder.durable(repliesQueueName).build();
    }

    // 3. Bindings: Amarramos la cola a las DOS posibles respuestas
    @Bean
    public Binding bindingStockReserved(Queue repliesQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(repliesQueue)
                .to(ordersExchange)
                .with(stockReservedKey);
    }

    @Bean
    public Binding bindingStockRejected(Queue repliesQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(repliesQueue)
                .to(ordersExchange)
                .with(stockRejectedKey);
    }

    // 4. Configuración de JSON (Igual que en Inventory)
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @SuppressWarnings("removal")
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}