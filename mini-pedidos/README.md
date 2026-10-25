# Mini Pedidos — Microservicio de Procesamiento Asíncrono con RabbitMQ y AWS SQS
**Evaluación Parcial N° 3 — Desarrollo Cloud Native I (DSY1107) — Duoc UC**

Microservicio central de pedidos desarrollado con Java 21 y Spring Boot, integrando mensajería asíncrona desacoplada mediante **RabbitMQ** (Direct & Topic Exchanges, Dead Letter Queues) y **AWS SQS** (simulado localmente con ElasticMQ).

---

## 🏛 Arquitectura de Mensajería

### 1. Configuración Centralizada (`application.yml`)
Todos los nombres de colas, exchanges y routing keys se definen en `application.yml` y se inyectan en `RabbitMQConfig.java`, aislando completamente la infraestructura de la lógica de dominio.

### 2. Estructura de Colas y Dead Letter Queues (DLQ)
- **`pedidos`**: Cola principal de pedidos enlazada a `pedidos.direct`.
  - Configurada con `x-dead-letter-exchange: pedidos.dlx` y routing key `pedidos.error`.
- **`pedidos.dlq`**: Cola de mensajes muertos para pedidos rechazados o inválidos.
- **`notificaciones`**: Cola de eventos de notificación al cliente, con `notificaciones.dlx` y `notificaciones.dlq`.
- **`reportes`**: Cola de generación asíncrona de reportes y tickets, enlazada a `pedidos.topic` con patrón `reportes.#`, con `reportes.dlx` y `reportes.dlq`.

### 3. Consumidores por Dominio Funcional
- **`PedidoConsumer`**: Procesa pedidos principales. Implementa confirmación explícita con `basicAck`. Si la cantidad es `<= 0`, rechaza con `channel.basicNack(deliveryTag, false, requeue = false)`, enrutando automáticamente el mensaje a `pedidos.dlq`.
- **`NotificacionConsumer`**: Procesa notificaciones a clientes de manera independiente.
- **`ReporteConsumer`**: Procesa la emisión de comprobantes en PDF.
- **`PedidoSqsConsumer`**: Consume y confirma periódicamente pedidos desde ElasticMQ / SQS.

---

## 📡 Endpoints REST

- **`POST /pedidos`**: Recibe un pedido y lo distribuye concurrentemente a RabbitMQ y ElasticMQ.
  ```json
  {
    "id": 101,
    "producto": "Teclado Mecanico RGB",
    "cantidad": 2,
    "cliente": "Carlos Silva"
  }
  ```

---

## 🚀 Despliegue y Ejecución

```bash
# 1. Iniciar servicios en segundo plano con Docker Compose
docker compose up -d

# 2. Compilar aplicación
./mvnw clean package -DskipTests

# 3. Iniciar aplicación (Puerto 8080)
./mvnw spring-boot:run
```
