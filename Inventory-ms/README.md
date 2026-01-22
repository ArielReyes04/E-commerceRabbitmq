# Inventory Microservice

Microservicio de gestión de inventario y stock para e-commerce con Spring Boot 4.0.1 y RabbitMQ.

## Descripción

Este microservicio maneja la gestión de stock de productos en un sistema de comercio electrónico distribuido. Escucha eventos de pedidos creados a través de RabbitMQ, valida disponibilidad de stock, realiza reservas y notifica al servicio de pedidos sobre el resultado de la operación.

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
http://localhost:8085/api/v1/products
```

### 1. Consultar Stock de Producto
**GET** `/api/v1/products/{productId}/stock`

Obtiene la información de stock de un producto específico.

**Path Parameters:**
- `productId` (UUID) - ID del producto

**Response:** `200 OK`
```json
{
  "productId": "uuid",
  "productName": "string",
  "availableQuantity": integer,
  "reservedQuantity": integer,
  "price": decimal,
  "lastUpdated": "timestamp"
}
```

**Response:** `404 Not Found`
- Cuando el producto no existe en el inventario

## Variables de Entorno

### Base de Datos (PostgreSQL)
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/db_inventory
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
SERVER_PORT=8085
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
5. Base de datos `db_inventory` creada en PostgreSQL

### Crear la Base de Datos

```sql
CREATE DATABASE db_inventory;
```

### Datos Iniciales (Opcional)

Puedes insertar productos de prueba:

```sql
-- Insertar productos de ejemplo
INSERT INTO product_stock (product_id, product_name, available_quantity, reserved_quantity, price, last_updated) 
VALUES 
  (gen_random_uuid(), 'Laptop Dell XPS 15', 50, 0, 1299.99, NOW()),
  (gen_random_uuid(), 'Mouse Logitech MX Master', 200, 0, 99.99, NOW()),
  (gen_random_uuid(), 'Teclado Mecánico RGB', 150, 0, 159.99, NOW()),
  (gen_random_uuid(), 'Monitor LG 27 4K', 75, 0, 399.99, NOW()),
  (gen_random_uuid(), 'Webcam Logitech C920', 100, 0, 79.99, NOW());
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
java -jar target/e-comerce_core-0.0.1-SNAPSHOT.jar
```

## Configuración con Docker Compose (Opcional)

Si deseas usar Docker para PostgreSQL y RabbitMQ:

```yaml
version: '3.8'
services:
  postgres-inventory:
    image: postgres:15
    environment:
      POSTGRES_DB: db_inventory
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"
    volumes:
      - postgres-inventory-data:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3-management
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    ports:
      - "5672:5672"
      - "15672:15672"

volumes:
  postgres-inventory-data:
```

Ejecutar:
```bash
docker-compose up -d
```

## Verificación

Una vez iniciado el servicio, verifica que está corriendo:

```bash
curl http://localhost:8085/api/v1/products/{product-id}/stock
```

O accede desde tu navegador a: `http://localhost:8085`

## Logs

Los logs del servicio se mostrarán en la consola. Para nivel de debug, modifica `application.yaml`:

```yaml
logging:
  level:
    ec.edu.espe.e_comerce_core: DEBUG
```

## Arquitectura de Mensajería

### Consumo de Eventos

El servicio escucha eventos de RabbitMQ:

1. **Consumo**: Escucha la cola `q.order-created` para eventos `OrderCreatedEvent`
2. **Procesamiento**: 
   - Valida disponibilidad de stock para cada producto del pedido
   - Si hay stock suficiente: reserva el stock y reduce la cantidad disponible
   - Si no hay stock: rechaza el pedido

### Publicación de Eventos

Después de procesar un pedido, publica una respuesta:

1. **Stock Reservado** (`stock.reserved`):
   - Se publica cuando todos los productos tienen stock disponible
   - El servicio de pedidos actualiza el estado a `CONFIRMED`

2. **Stock Rechazado** (`stock.rejected`):
   - Se publica cuando no hay suficiente stock para algún producto
   - El servicio de pedidos actualiza el estado a `CANCELLED`

## Modelo de Datos

### ProductStock
```java
{
  "productId": "UUID",           // ID único del producto
  "productName": "String",       // Nombre del producto
  "availableQuantity": "Integer", // Cantidad disponible para venta
  "reservedQuantity": "Integer",  // Cantidad reservada en pedidos
  "price": "BigDecimal",         // Precio del producto
  "lastUpdated": "LocalDateTime" // Última actualización
}
```

## Flujo de Reserva de Stock

1. **Evento Recibido**: El servicio recibe un `OrderCreatedEvent` de RabbitMQ
2. **Validación**: Verifica stock disponible para cada producto del pedido
3. **Reserva**: Si hay stock, incrementa `reservedQuantity` y decrementa `availableQuantity`
4. **Notificación**: Publica evento de éxito (`stock.reserved`) o fallo (`stock.rejected`)

## Testing

Puedes probar el endpoint de consulta de stock:

```bash
# Obtener stock de un producto
curl -X GET http://localhost:8085/api/v1/products/{product-id}/stock
```

## Troubleshooting

### Error: Base de datos no conecta
- Verifica que PostgreSQL esté corriendo: `pg_isready`
- Comprueba las credenciales en `application.yaml`
- Asegúrate de que la base de datos `db_inventory` existe

### Error: RabbitMQ no conecta
- Verifica que RabbitMQ esté corriendo
- Accede a la consola de gestión: `http://localhost:15672` (admin/admin123)
- Comprueba que el exchange `orders-exchange` y la cola `q.order-created` existen

### No se procesan los eventos
- Revisa los logs del servicio
- Verifica que el binding entre exchange y cola esté configurado correctamente
- Comprueba que el routing key `order.created` coincide en ambos servicios

## Orden de Inicio

Para el correcto funcionamiento del sistema distribuido:

1. Inicia **PostgreSQL** y **RabbitMQ**
2. Inicia el **Inventory Microservice** (puerto 8085)
3. Inicia el **Order Service** (puerto 8086)

## Autor

Desarrollado por estudiantes de ESPE - Aplicaciones Distribuidas

## Licencia

Este proyecto es parte de un ejercicio académico.
