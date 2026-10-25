# Mini Pedidos Cloud Native — Arquitectura de Microservicios con RabbitMQ y AWS SQS
**Evaluación Parcial N° 3 — Desarrollo Cloud Native I (DSY1107) — Duoc UC**

Sistema desacoplado de procesamiento asíncrono de pedidos compuesto por microservicios en **Java 21 / Spring Boot**, mensajería distribuida con **RabbitMQ**, integración con **AWS SQS** (simulado con ElasticMQ) y un frontend en **React** con autenticación **AWS Cognito (IDaaS)**.

---

## 🏛 Arquitectura del Sistema

```
                      +-------------------+
                      |  Frontend React   | (Port 5173)
                      |  AWS Cognito JWT  |
                      +---------+---------+
                                |
             +------------------+------------------+
             | HTTP POST                           | HTTP REST
             v                                     v
+-------------------------+           +-------------------------+
|      mini-pedidos       |           |      rabbit-admin       |
|    (Spring Boot :8080)  |           |    (Spring Boot :8081)  |
+------------+------------+           +------------+------------+
             |                                     |
    +--------+--------+                            |
    |                 |                            | RabbitAdmin API
    v                 v                            v
+---------+     +-----------+             +-----------------+
| RabbitMQ|     | ElasticMQ |             | RabbitMQ Broker |
| Broker  |     |   (SQS)   |             | (Dynamic Queues,|
+---------+     +-----------+             | Exchanges, etc) |
                                          +-----------------+
```

---

## 📦 Componentes del Repositorio

| Directorio | Componente | Tecnología | Puerto | Descripción |
|---|---|---|---|---|
| **`/mini-pedidos`** | Microservicio Central de Pedidos | Java 21, Spring Boot, Spring AMQP, AWS SDK | `8080` | Gestión asíncrona de pedidos, notificaciones y reportes con confirmaciones ACK/NACK y DLQ. |
| **`/rabbit-admin`** | Microservicio Administrador | Java 21, Spring Boot, Spring AMQP, Bean Validation | `8081` | API REST para aprovisionamiento dinámico de colas, exchanges y bindings con validaciones estrictas. |
| **`/frontend`** | Dashboard de Pedidos | React, Vite, Bootstrap, AWS Cognito SDK | `5173` | Interfaz de usuario con inicio de sesión IDaaS, autorización vía JWT y vista de métricas. |

---

## 📋 Cumplimiento de Indicadores de Rúbrica (EP3)

1. **Configuración Centralizada (12%):** Todos los nombres de colas, exchanges, routing keys y DLQs se definen en `mini-pedidos/src/main/resources/application.yml` y se inyectan en `RabbitMQConfig.java`.
2. **Beans de Queue, Exchange y Binding (13%):** Se declaran explícitamente beans para `DirectExchange`, `TopicExchange`, Dead Letter Exchanges (`DLX`), 6 colas (`pedidos`, `notificaciones`, `reportes` y sus respectivas `*.dlq`) y sus bindings asociados.
3. **Aislamiento de Responsabilidades (10%):** La lógica de negocio está totalmente separada de la configuración de mensajería (paquetes independientes `config`, `controller`, `messaging.consumer`, `messaging.producer`, `model`, `service`).
4. **Consumidores por Dominio Funcional (15%):** Consumidores desacoplados con `@RabbitListener` organizados por dominio: `PedidoConsumer`, `NotificacionConsumer` y `ReporteConsumer`.
5. **Manejo Explícito de ACK, NACK y DLQ (20%):** Confirmación manual mediante `channel.basicAck`. Ante pedidos inválidos (cantidad `<= 0`), se ejecuta `channel.basicNack(requeue = false)`, redirigiendo el mensaje a `pedidos.dlq`.
6. **Microservicio Administrador REST (13%):** Endpoints REST estandarizados (`POST /queues`, `DELETE /queues/{name}`, `POST /exchanges`, `DELETE /exchanges/{name}`, `POST /bindings`).
7. **Servicio Encapsulado (10%):** Toda la lógica de administración reside en `RabbitAdminService`, desacoplando a los controladores de Spring AMQP.
8. **Validación de Entradas (7%):** DTOs fuertemente tipados con anotaciones de validación (`@NotBlank`, `@Pattern`) que impiden nombres vacíos o configuraciones inválidas (HTTP 400 Bad Request).

---

## 🚀 Guía de Ejecución Rápida

### 1. Iniciar Infraestructura Docker (RabbitMQ y ElasticMQ)
```bash
cd mini-pedidos
docker compose up -d
docker compose ps
```
- **RabbitMQ Management:** `http://localhost:15672` (Usuario: `guest` / Contraseña: `guest`)
- **ElasticMQ (SQS):** `http://localhost:9324`

### 2. Ejecutar Microservicio de Pedidos (`mini-pedidos`)
```bash
cd mini-pedidos
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

### 3. Ejecutar Microservicio Administrador (`rabbit-admin`)
```bash
cd rabbit-admin
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

### 4. Ejecutar Frontend React
```bash
cd frontend
npm install
npm run dev
```

---

## 🧪 Pruebas Rápidas de API

### Pedido Válido (Confirmación con ACK)
```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{"id": 101, "producto": "Teclado Mecanico RGB", "cantidad": 2, "cliente": "Carlos Silva"}'
```

### Pedido Inválido (Rechazo NACK -> Dead Letter Queue)
```bash
curl -X POST http://localhost:8080/pedidos \
  -H "Content-Type: application/json" \
  -d '{"id": 102, "producto": "Mouse Inalambrico", "cantidad": 0, "cliente": "Maria Gomez"}'
```

### Crear Cola vía Microservicio Admin
```bash
curl -X POST http://localhost:8081/queues \
  -H "Content-Type: application/json" \
  -d '{"name": "cola-auditoria", "durable": true}'
```

---

## 👨‍💻 Autor
- **Estudiante:** Diego Navarrete
- **Carrera:** Ingeniería en Informática — Duoc UC
- **Asignatura:** Desarrollo Cloud Native I (DSY1107)
