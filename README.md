# Sistema de E-Commerce con Microservicios y RabbitMQ

Sistema distribuido de comercio electrónico implementado con arquitectura de microservicios utilizando Spring Boot y RabbitMQ para comunicación asíncrona entre servicios.

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Arquitectura](#arquitectura)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Microservicios](#microservicios)
- [Flujo de Comunicación](#flujo-de-comunicación)
- [Requisitos Previos](#requisitos-previos)
- [Configuración e Instalación](#configuración-e-instalación)
- [Endpoints de la API](#endpoints-de-la-api)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Base de Datos](#base-de-datos)

## 📖 Descripción General

Este proyecto implementa un sistema de e-commerce distribuido que consta de dos microservicios principales:

1. **OrderService**: Gestiona la creación y seguimiento de pedidos
2. **Inventory-ms**: Gestiona el inventario de productos y la reserva de stock

Los microservicios se comunican de manera asíncrona mediante **RabbitMQ**, implementando un patrón de mensajería basado en eventos para garantizar la consistencia eventual del sistema.

## 🏗 Arquitectura

```
┌─────────────────┐         ┌─────────────────────┐         ┌──────────────────┐
│                 │         │                     │         │                  │
│  OrderService   │────────▶│      RabbitMQ       │────────▶│   Inventory-ms   │
│  (Port 8086)    │         │   (Port 5672)       │         │   (Port 8085)    │
│                 │◀────────│   Management UI     │◀────────│                  │
└─────────────────┘         │   (Port 15672)      │         └──────────────────┘
        │                   └─────────────────────┘                  │
        │                                                             │
        ▼                                                             ▼
┌─────────────────┐                                         ┌──────────────────┐
│  PostgreSQL     │                                         │   PostgreSQL     │
│  db_order       │                                         │  db_inventory    │
└─────────────────┘                                         └──────────────────┘
```

### Patrón de Comunicación

El sistema utiliza el patrón **Event-Driven Architecture** con las siguientes características:

- **Exchange Type**: Topic Exchange (`orders-exchange`)
- **Colas**:
  - `q.order-created`: Recibe eventos de nuevos pedidos
  - `q.order-replies`: Recibe respuestas de inventario
- **Routing Keys**:
  - `order.created`: Publicación de nuevos pedidos
  - `stock.reserved`: Confirmación de reserva de stock
  - `stock.rejected`: Rechazo por falta de stock

## 🛠 Tecnologías Utilizadas

### Backend
- **Java 21**
- **Spring Boot 4.0.1**
- **Spring Data JPA**
- **Spring AMQP** (RabbitMQ)
- **PostgreSQL**
- **Lombok**
- **Jackson** (para serialización JSON)

### Infraestructura
- **RabbitMQ 3.13** (con Management Plugin)
- **Docker Compose** (para orquestación)
- **Maven** (gestión de dependencias)

## 📁 Estructura del Proyecto

```
E-commerceRabbitmq/
│
├── docker-compose.yml           # Configuración de RabbitMQ
├── README.md                    # Este archivo
│
├── OrderService/                # Microservicio de Pedidos
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/ec/edu/espe/OrderService/
│           │   ├── OrderServiceApplication.java
│           │   ├── config/
│           │   │   └── RabbitMQConfig.java
│           │   ├── controllers/
│           │   │   └── OrderController.java
│           │   ├── dto/
│           │   │   ├── mapper/
│           │   │   ├── messaging/
│           │   │   ├── request/
│           │   │   └── response/
│           │   ├── messaging/
│           │   │   ├── OrderListener.java
│           │   │   └── OrderProducer.java
│           │   ├── models/
│           │   ├── repositories/
│           │   └── services/
│           └── resources/
│               └── application.yaml
│
└── Inventory-ms/                # Microservicio de Inventario
    ├── pom.xml
    └── src/
        └── main/
            ├── java/ec/edu/espe/e_comerce_core/
            │   ├── EComerceCoreApplication.java
            │   ├── config/
            │   │   └── RabbitMQConfig.java
            │   ├── controller/
            │   │   └── ProductController.java
            │   ├── dto/
            │   ├── messaging/
            │   │   └── NotificationProducer.java
            │   ├── model/
            │   ├── repository/
            │   └── service/
            └── resources/
                └── application.yaml
```

## 🔧 Microservicios

### 1. OrderService (Puerto 8086)

**Responsabilidades:**
- Recibir solicitudes de creación de pedidos
- Validar datos del pedido
- Publicar eventos de creación de pedidos a RabbitMQ
- Escuchar respuestas del servicio de inventario
- Actualizar el estado de los pedidos (PENDING → CONFIRMED/CANCELLED)

**Modelo de Datos:**

```java
Order {
    UUID orderId;
    UUID customerId;
    LocalDateTime createdAt;
    OrderStatus status;        // PENDING, CONFIRMED, CANCELLED
    String rejectionReason;
    List<OrderItem> items;
    ShippingAddress shippingAddress;
}
```

**Estados de Pedido:**
- `PENDING`: Pedido creado, esperando confirmación de inventario
- `CONFIRMED`: Stock reservado exitosamente
- `CANCELLED`: Rechazado por falta de stock

**Componentes Principales:**

| Componente | Descripción |
|------------|-------------|
| `OrderController` | API REST para crear y consultar pedidos |
| `OrderServiceImp` | Lógica de negocio para gestión de pedidos |
| `OrderProducer` | Publica eventos de pedidos a RabbitMQ |
| `OrderListener` | Escucha respuestas de inventario |

### 2. Inventory-ms (Puerto 8085)

**Responsabilidades:**
- Gestionar el stock de productos
- Escuchar eventos de nuevos pedidos
- Validar disponibilidad de stock
- Reservar o rechazar stock según disponibilidad
- Publicar eventos de respuesta (stock reservado/rechazado)

**Modelo de Datos:**

```java
ProductStock {
    UUID productId;
    String productName;
    Integer availableStock;    // Stock disponible para venta
    Integer reservedStock;     // Stock reservado (pendiente de envío)
    BigDecimal price;
}
```

**Componentes Principales:**

| Componente | Descripción |
|------------|-------------|
| `ProductController` | API REST para consultar stock de productos |
| `InventoryServiceImp` | Lógica de validación y reserva de stock |
| `NotificationProducer` | Publica respuestas de inventario |
| `@RabbitListener` | Procesa eventos de pedidos entrantes |

## 🔄 Flujo de Comunicación

### Flujo Completo de Creación de Pedido

```
1. Cliente HTTP POST → OrderService
   └─ POST /api/v1/orders
   
2. OrderService guarda el pedido (Estado: PENDING)
   └─ Persiste en db_order
   
3. OrderService publica evento → RabbitMQ
   └─ Exchange: orders-exchange
   └─ Routing Key: order.created
   └─ Cola destino: q.order-created
   
4. Inventory-ms escucha el evento
   └─ @RabbitListener en q.order-created
   
5. Inventory-ms valida stock
   ├─ SI hay stock suficiente:
   │  ├─ Decrementa availableStock
   │  ├─ Incrementa reservedStock
   │  └─ Publica StockReserved (routing-key: stock.reserved)
   │
   └─ NO hay stock suficiente:
      └─ Publica StockRejected (routing-key: stock.rejected)
      
6. OrderService escucha respuesta
   └─ @RabbitListener en q.order-replies
   
7. OrderService actualiza estado del pedido
   ├─ StockReserved → Estado: CONFIRMED
   └─ StockRejected → Estado: CANCELLED (con razón)
   
8. Cliente consulta estado del pedido
   └─ GET /api/v1/orders/{orderId}
```

### Diagrama de Secuencia

```
Cliente          OrderService          RabbitMQ          Inventory-ms
  │                   │                   │                    │
  │  POST /orders     │                   │                    │
  ├──────────────────▶│                   │                    │
  │                   │                   │                    │
  │  201 Created      │                   │                    │
  │◀──────────────────┤                   │                    │
  │  (PENDING)        │                   │                    │
  │                   │  order.created    │                    │
  │                   ├──────────────────▶│                    │
  │                   │                   │  OrderCreatedEvent │
  │                   │                   ├───────────────────▶│
  │                   │                   │                    │
  │                   │                   │   [Validar Stock]  │
  │                   │                   │                    │
  │                   │                   │  StockResponse     │
  │                   │  stock.reserved   │◀───────────────────┤
  │                   │◀──────────────────┤                    │
  │                   │                   │                    │
  │  [Actualiza DB]   │                   │                    │
  │                   │                   │                    │
  │  GET /orders/{id} │                   │                    │
  ├──────────────────▶│                   │                    │
  │  200 OK           │                   │                    │
  │◀──────────────────┤                   │                    │
  │  (CONFIRMED)      │                   │                    │
```

## ✅ Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java 21** o superior
- **Maven 3.8+**
- **PostgreSQL 12+**
- **Docker y Docker Compose** (opcional, para RabbitMQ)
- **Git**

## ⚙️ Configuración e Instalación

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd E-commerceRabbitmq
```

### 2. Levantar RabbitMQ con Docker

```bash
docker-compose up -d
```

Esto iniciará RabbitMQ con:
- **AMQP Port**: 5672
- **Management UI**: http://localhost:15672
- **Credenciales**: admin / admin123

### 3. Configurar Bases de Datos

Crear las bases de datos en PostgreSQL:

```sql
-- Base de datos para OrderService
CREATE DATABASE db_order;

-- Base de datos para Inventory-ms
CREATE DATABASE db_inventory;
```

### 4. Verificar Configuración

#### OrderService (application.yaml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_order
    username: postgres
    password: admin123
    
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123

server:
  port: 8086
```

#### Inventory-ms (application.yaml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/db_inventory
    username: postgres
    password: admin123
    
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123

server:
  port: 8085
```

### 5. Compilar y Ejecutar los Servicios

#### Opción A: Usar Maven Wrapper (Recomendado)

**Terminal 1 - Inventory-ms:**
```bash
cd Inventory-ms
./mvnw clean install
./mvnw spring-boot:run
```

**Terminal 2 - OrderService:**
```bash
cd OrderService
./mvnw clean install
./mvnw spring-boot:run
```

#### Opción B: Usar Maven instalado

```bash
# Inventory-ms
cd Inventory-ms
mvn clean install
mvn spring-boot:run

# OrderService
cd OrderService
mvn clean install
mvn spring-boot:run
```

### 6. Verificar que los Servicios Están Funcionando

- **OrderService**: http://localhost:8086
- **Inventory-ms**: http://localhost:8085
- **RabbitMQ Management**: http://localhost:15672

## 📡 Endpoints de la API

### OrderService (Puerto 8086)

#### Crear Pedido
```http
POST http://localhost:8086/api/v1/orders
Content-Type: application/json

{
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "items": [
    {
      "productId": "123e4567-e89b-12d3-a456-426614174000",
      "quantity": 2
    }
  ],
  "shippingAddress": {
    "street": "Av. Principal 123",
    "city": "Quito",
    "state": "Pichincha",
    "zipCode": "170101",
    "country": "Ecuador"
  }
}
```

**Respuesta Exitosa (201 Created):**
```json
{
  "orderId": "789e0123-e89b-12d3-a456-426614174000",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2026-01-21T10:30:00",
  "status": "PENDING",
  "items": [
    {
      "productId": "123e4567-e89b-12d3-a456-426614174000",
      "quantity": 2
    }
  ],
  "shippingAddress": {
    "street": "Av. Principal 123",
    "city": "Quito",
    "state": "Pichincha",
    "zipCode": "170101",
    "country": "Ecuador"
  }
}
```

#### Consultar Pedido
```http
GET http://localhost:8086/api/v1/orders/{orderId}
```

**Respuesta (200 OK):**
```json
{
  "orderId": "789e0123-e89b-12d3-a456-426614174000",
  "customerId": "550e8400-e29b-41d4-a716-446655440000",
  "createdAt": "2026-01-21T10:30:00",
  "status": "CONFIRMED",
  "items": [
    {
      "productId": "123e4567-e89b-12d3-a456-426614174000",
      "quantity": 2
    }
  ],
  "shippingAddress": {
    "street": "Av. Principal 123",
    "city": "Quito",
    "state": "Pichincha",
    "zipCode": "170101",
    "country": "Ecuador"
  }
}
```

### Inventory-ms (Puerto 8085)

#### Consultar Stock de Producto
```http
GET http://localhost:8085/api/v1/products/{productId}/stock
```

**Respuesta (200 OK):**
```json
{
  "productId": "123e4567-e89b-12d3-a456-426614174000",
  "productName": "Laptop HP",
  "availableStock": 15,
  "reservedStock": 5,
  "price": 899.99
}
```

## 💡 Ejemplos de Uso

### Escenario 1: Pedido Exitoso

1. **Insertar productos en el inventario** (directamente en la base de datos):

```sql
INSERT INTO product_stock (product_id, product_name, available_stock, reserved_stock, price)
VALUES 
  ('123e4567-e89b-12d3-a456-426614174000', 'Laptop HP', 20, 0, 899.99);
```

2. **Crear un pedido**:

```bash
curl -X POST http://localhost:8086/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "productId": "123e4567-e89b-12d3-a456-426614174000",
        "quantity": 2
      }
    ],
    "shippingAddress": {
      "street": "Av. Principal 123",
      "city": "Quito",
      "state": "Pichincha",
      "zipCode": "170101",
      "country": "Ecuador"
    }
  }'
```

3. **Verificar el pedido** (espera unos segundos para que se procese):

```bash
curl http://localhost:8086/api/v1/orders/{orderId-retornado}
```

4. **Verificar el stock actualizado**:

```bash
curl http://localhost:8085/api/v1/products/123e4567-e89b-12d3-a456-426614174000/stock
```

### Escenario 2: Pedido Rechazado por Falta de Stock

1. **Crear pedido con cantidad mayor al stock disponible**:

```bash
curl -X POST http://localhost:8086/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "550e8400-e29b-41d4-a716-446655440000",
    "items": [
      {
        "productId": "123e4567-e89b-12d3-a456-426614174000",
        "quantity": 100
      }
    ],
    "shippingAddress": {
      "street": "Av. Principal 123",
      "city": "Quito",
      "state": "Pichincha",
      "zipCode": "170101",
      "country": "Ecuador"
    }
  }'
```

2. **Consultar el pedido** (verás status: "CANCELLED" y una razón):

```json
{
  "orderId": "...",
  "status": "CANCELLED",
  "reason": "Stock insuficiente para producto: 123e4567-e89b-12d3-a456-426614174000",
  ...
}
```

## 🗄️ Base de Datos

### Esquema OrderService (db_order)

```sql
-- Tabla de pedidos
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    rejection_reason VARCHAR(255)
);

-- Tabla de items del pedido
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID REFERENCES orders(order_id),
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL
);

-- Tabla de direcciones de envío
CREATE TABLE shipping_addresses (
    id BIGSERIAL PRIMARY KEY,
    order_id UUID REFERENCES orders(order_id),
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100)
);
```

### Esquema Inventory-ms (db_inventory)

```sql
-- Tabla de stock de productos
CREATE TABLE product_stock (
    product_id UUID PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    available_stock INTEGER NOT NULL DEFAULT 0,
    reserved_stock INTEGER NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL
);
```

## 🔍 Monitoreo y Debugging

### RabbitMQ Management UI

Accede a http://localhost:15672 con las credenciales `admin/admin123` para:

- Ver las colas y sus mensajes
- Monitorear el Exchange `orders-exchange`
- Revisar los bindings entre colas y exchange
- Inspeccionar mensajes en tránsito
- Ver estadísticas de consumo y publicación

### Logs

Ambos microservicios generan logs detallados:

```
# Inventory-ms
INFO - Procesando orden recibida: 789e0123-e89b-12d3-a456-426614174000
INFO - Enviando evento StockReserved a exchange orders-exchange con key stock.reserved

# OrderService
INFO - Creando nueva orden para cliente: 550e8400-e29b-41d4-a716-446655440000
INFO - Procesando respuesta de inventario para orden: 789e0123-e89b-12d3-a456-426614174000
INFO - Orden 789e0123-e89b-12d3-a456-426614174000 confirmada.
```

## 🎯 Características Implementadas

- ✅ Arquitectura de microservicios desacoplados
- ✅ Comunicación asíncrona mediante eventos
- ✅ Patrón de mensajería con Topic Exchange
- ✅ Consistencia eventual entre servicios
- ✅ Manejo de transacciones con Spring `@Transactional`
- ✅ Validación de datos con Bean Validation
- ✅ Mapeo de entidades con DTOs
- ✅ Serialización JSON personalizada para fechas
- ✅ Manejo de errores y logging
- ✅ API REST documentada

## 🚀 Mejoras Futuras

- [ ] Implementar Dead Letter Queue (DLQ) para mensajes fallidos
- [ ] Agregar reintentos automáticos con backoff exponencial
- [ ] Implementar Circuit Breaker con Resilience4j
- [ ] Agregar autenticación y autorización (Spring Security + JWT)
- [ ] Implementar API Gateway (Spring Cloud Gateway)
- [ ] Agregar servicio de descubrimiento (Eureka)
- [ ] Implementar tracing distribuido (Sleuth + Zipkin)
- [ ] Agregar métricas con Prometheus y Grafana
- [ ] Implementar versionado de API
- [ ] Agregar documentación con Swagger/OpenAPI
- [ ] Containerizar los microservicios con Docker
- [ ] Implementar CI/CD
- [ ] Agregar tests unitarios e integración

## 📝 Notas Adicionales

### Gestión de Transacciones

- Las operaciones de base de datos están protegidas con `@Transactional`
- En caso de error, se realiza rollback automático
- La comunicación con RabbitMQ ocurre **después** del commit de la transacción

### Idempotencia

Los eventos incluyen un `correlationId` único para:
- Rastrear el flujo completo de un pedido
- Implementar lógica de idempotencia en futuras versiones
- Debugging y auditoría

### Escalabilidad

Cada microservicio puede escalarse independientemente:
- Múltiples instancias de OrderService compartirán la cola `q.order-replies`
- Múltiples instancias de Inventory-ms compartirán la cola `q.order-created`
- RabbitMQ distribuye mensajes equitativamente entre consumidores

---

**Desarrollado como proyecto académico para ESPE - Aplicaciones Distribuidas**

**Fecha**: Tercer Parcial - 2026
