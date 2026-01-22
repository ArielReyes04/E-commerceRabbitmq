# Order Service - Microservicio de Gestión de Pedidos

Este microservicio es parte de la plataforma de e-commerce distribuida. Su responsabilidad principal es la creación, gestión y consulta de pedidos, manejando la comunicación asíncrona con el servicio de inventario para validar el stock.

## 📋 Descripción

El Order Service implementa el flujo de negocio "Procesamiento de pedido":
1. Recibe solicitudes HTTP para crear pedidos.
2. Registra el pedido en estado `PENDING`.
3. (Próximamente) Publica eventos en RabbitMQ para validación de stock.
4. Permite consultar el estado actual de un pedido.

## 🛠️ Tecnologías

* **Lenguaje:** Java 17 / 21
* **Framework:** Spring Boot 3.x
* **Base de Datos:** PostgreSQL
* **Mensajería:** RabbitMQ (Integración pendiente)
* **Herramientas:** Maven, Lombok, Docker

## ⚙️ Configuración y Variables de Entorno

El proyecto utiliza un archivo `application.properties` (o `.yml`). Puedes configurar las siguientes variables de entorno para despliegue o ejecución local:

| Variable | Descripción | Valor por Defecto (Local) |
|----------|-------------|---------------------------|
| `DB_URL` | URL de conexión a PostgreSQL | `jdbc:postgresql://localhost:5432/order_db` |
| `DB_USERNAME` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña de la base de datos | `postgres` |
| `RABBITMQ_HOST` | Host de RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Puerto de RabbitMQ | `5672` |

## 🚀 Cómo ejecutar el proyecto

### Prerrequisitos
1. Tener **Java 17+** y **Maven** instalados.
2. Tener una instancia de **PostgreSQL** corriendo y una base de datos creada llamada `order_db`.

### Pasos
1. Clonar el repositorio.
2. Navegar a la carpeta del servicio:
   ```bash
   cd order-service