# RabbitMQ Admin Microservice (`rabbit-admin`)
**Evaluación Parcial N° 3 — Desarrollo Cloud Native I (DSY1107) — Duoc UC**

Microservicio REST desacoplado construido en Java 21 con Spring Boot para la administración y aprovisionamiento dinámico de recursos en RabbitMQ.

---

## 🏛 Arquitectura y Diseño

El microservicio desacopla las operaciones de administración del broker respecto a la lógica de negocio, cumpliendo con los estándares de la rúbrica EP3:

- **Servicio Encapsulado (`RabbitAdminService`):** Centraliza la comunicación con `RabbitAdmin` de Spring AMQP. Ningún controlador interactúa directamente con clientes de bajo nivel.
- **Validación de Entradas (`@Valid`):** DTOs fuertemente tipados con anotaciones de Bean Validation (`@NotBlank`, `@Pattern`) que impiden nombres vacíos, formatos ilegales o tipos de exchange no soportados.
- **Diseño RESTful Coherente:** Rutas y verbos HTTP estándar (`POST /queues`, `DELETE /queues/{name}`, `POST /exchanges`, etc.).

---

## 📡 Endpoints de la API

### 1. Colas (`/queues`)
- **`POST /queues`**: Crea una nueva cola en RabbitMQ con soporte opcional de DLX/DLQ.
  ```json
  {
    "name": "cola-auditoria",
    "durable": true,
    "autoDelete": false,
    "deadLetterExchange": "pedidos.dlx",
    "deadLetterRoutingKey": "pedidos.error"
  }
  ```
- **`DELETE /queues/{name}`**: Elimina una cola existente por su nombre.

### 2. Exchanges (`/exchanges`)
- **`POST /exchanges`**: Crea un exchange según el tipo especificado (`direct`, `topic`, `fanout`, `headers`).
  ```json
  {
    "name": "exchange-auditoria",
    "type": "topic",
    "durable": true
  }
  ```
- **`DELETE /exchanges/{name}`**: Elimina un exchange por su nombre.

### 3. Bindings (`/bindings`)
- **`POST /bindings`**: Vincula una cola a un exchange con una routing key.
  ```json
  {
    "exchangeName": "exchange-auditoria",
    "queueName": "cola-auditoria",
    "routingKey": "auditoria.#"
  }
  ```
- **`DELETE /bindings`**: Remueve la vinculación.

---

## 🚀 Ejecución

```bash
# Compilar y empaquetar
./mvnw clean package -DskipTests

# Ejecutar microservicio (Puerto 8081)
./mvnw spring-boot:run
```
