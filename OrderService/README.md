# Order Service

Microservicio de gestión de pedidos para e-commerce con Spring Boot 4.0.1 y RabbitMQ.

## Descripción

Este microservicio maneja la creación y consulta de pedidos en un sistema de comercio electrónico distribuido. Se comunica con el servicio de inventario mediante mensajería asíncrona usando RabbitMQ para la validación y reserva de stock.

## Tecnologías

- Java 21
- Spring Boot 4.0.1
- Spring Data JPA
- PostgreSQL
- RabbitMQ
- Lombok
- Maven

## Endpoints

### Base URL
```
http://localhost:8086/api/v1/orders
```

### 1. Crear Pedido
**POST** `/api/v1/orders`

Crea un nuevo pedido y envía un evento a RabbitMQ para reservar el stock.

**Request Body:**
```json
{
  "customerId": "string",
  "paymentReference": "string",
  "orderItems": [
    {
      "productId": "uuid",
      "quantity": integer,
      "price": decimal
    }
  ],
  "shippingAddress": {
    "street": "string",
    "city": "string",
    "state": "string",
    "zipCode": "string",
    "country": "string"
  }
}
```

**Response:** `201 Created`
```json
{
  "orderId": "uuid",
  "customerId": "string",
  "status": "PENDING",
  "totalAmount": decimal,
  "paymentReference": "string",
  "orderItems": [...],
  "shippingAddress": {...},
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### 2. Consultar Pedido
**GET** `/api/v1/orders/{orderId}`

Obtiene los detalles de un pedido por su ID.

**Path Parameters:**
- `orderId` (UUID) - ID del pedido

**Response:** `200 OK`
```json
{
  "orderId": "uuid",
  "customerId": "string",
  "status": "PENDING|CONFIRMED|CANCELLED",
  "totalAmount": decimal,
  "paymentReference": "string",
  "orderItems": [...],
  "shippingAddress": {...},
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## Variables de Entorno

### Base de Datos (PostgreSQL)
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db_order
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=admin123
```

### RabbitMQ
```properties
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=admin
SPRING_RABBITMQ_PASSWORD=admin123
```

### Configuración del Servidor
```properties
SERVER_PORT=8086
```

### Configuración de Colas (Custom)
```properties
CUSTOM_RABBITMQ_EXCHANGE=orders-exchange
CUSTOM_RABBITMQ_QUEUE_ORDER_CREATED=q.order-created
CUSTOM_RABBITMQ_ROUTING_KEY_ORDER_CREATED=order.created
CUSTOM_RABBITMQ_ROUTING_KEY_STOCK_RESERVED=stock.reserved
CUSTOM_RABBITMQ_ROUTING_KEY_STOCK_REJECTED=stock.rejected
```

## Requisitos Previos

1. **Java 21** o superior instalado
2. **Maven 3.8+** instalado
3. **PostgreSQL** corriendo en el puerto 5432
4. **RabbitMQ** corriendo en el puerto 5672
5. Base de datos `db_order` creada en PostgreSQL

### Crear la Base de Datos

```sql
CREATE DATABASE db_order;
```

## Cómo Ejecutar

### Opción 1: Usando Maven Wrapper (Recomendado)

#### En Windows:
```bash
mvnw.cmd spring-boot:run
```

#### En Linux/Mac:
```bash
./mvnw spring-boot:run
```

### Opción 2: Usando Maven Instalado

```bash
mvn spring-boot:run
```

### Opción 3: Ejecutar el JAR

1. Compilar el proyecto:
```bash
mvn clean package
```

2. Ejecutar el JAR generado:
```bash
java -jar target/OrderService-0.0.1-SNAPSHOT.jar
```

## Configuración con Docker Compose (Opcional)

Si deseas usar Docker para PostgreSQL y RabbitMQ:

```yaml
version: '3.8'
services:
  postgres-order:
    image: postgres:15
    environment:
      POSTGRES_DB: db_order
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
    volumes:
      - postgres-order-data:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3-management
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    ports:
      - "5672:5672"
      - "15672:15672"

volumes:
  postgres-order-data:
```

Ejecutar:
```bash
docker-compose up -d
```

## Verificación

Una vez iniciado el servicio, verifica que está corriendo:

```bash
curl http://localhost:8086/api/v1/orders
```

O accede desde tu navegador a: `http://localhost:8086`

## Logs

Los logs del servicio se mostrarán en la consola. Para nivel de debug, modifica `application.yaml`:

```yaml
logging:
  level:
    ec.edu.espe.OrderService: DEBUG
```

## Arquitectura de Mensajería

El servicio utiliza RabbitMQ para comunicación asíncrona:

1. **Publicación**: Cuando se crea un pedido, se publica un evento `OrderCreatedEvent` en el exchange `orders-exchange` con routing key `order.created`
2. **Consumo**: Escucha respuestas del servicio de inventario:
   - `stock.reserved`: Stock reservado exitosamente → Pedido CONFIRMADO
   - `stock.rejected`: Stock insuficiente → Pedido CANCELADO

## Estados del Pedido

- `PENDING`: Pedido creado, esperando confirmación de stock
- `CONFIRMED`: Stock reservado, pedido confirmado
- `CANCELLED`: Stock insuficiente o error en la reserva

## Autor

Desarrollado por estudiantes de ESPE - Aplicaciones Distribuidas

## Licencia

Este proyecto es parte de un ejercicio académico.
