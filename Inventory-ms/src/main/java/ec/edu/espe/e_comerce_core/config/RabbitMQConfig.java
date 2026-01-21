package ec.edu.espe.e_comerce_core.config;

import org.hibernate.Remove;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RabbitMQConfig {

    @Value("${custom.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${custom.rabbitmq.queue.order-created}")
    private String orderCreatedQueueName;

    @Value("${custom.rabbitmq.routing-key.order-created}")
    private String orderCreatedRoutingKey;

    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(exchangeName);
    }

    // 2. Definir la cola de entrada (donde llegan los pedidos)
    @Bean
    public Queue orderCreatedQueue() {
        // QueueBuilder.durable asegura que la cola sobreviva a reinicios
        return QueueBuilder.durable(orderCreatedQueueName).build();
    }

    // 3. Binding (Unir cola al exchange)
    @Bean
    public Binding bindingOrderCreated(Queue orderCreatedQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(orderCreatedQueue)
                .to(ordersExchange)
                .with(orderCreatedRoutingKey);
    }

    // 4. Configuración del Mapper (Fechas, etc)
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    @SuppressWarnings("removal")
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        // Configuramos el conversor para usar el ObjectMapper personalizado
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        converter.setCreateMessageIds(true);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}