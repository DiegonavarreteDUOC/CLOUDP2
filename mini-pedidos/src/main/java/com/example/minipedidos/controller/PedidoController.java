package com.example.minipedidos.controller;

import com.example.minipedidos.messaging.producer.PedidoProducer;
import com.example.minipedidos.model.Pedido;
import com.example.minipedidos.service.PedidoSqsProducer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST del dominio Pedidos.
 * Recibe pedidos por HTTP y los delega a los productores de mensajeria.
 */
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoProducer pedidoProducer;
    private final PedidoSqsProducer pedidoSqsProducer;

    public PedidoController(PedidoProducer pedidoProducer,
                            PedidoSqsProducer pedidoSqsProducer) {
        this.pedidoProducer = pedidoProducer;
        this.pedidoSqsProducer = pedidoSqsProducer;
    }

    /**
     * Crea un nuevo pedido y lo publica en RabbitMQ y SQS.
     * La anotacion @Valid activa las validaciones del modelo Pedido.
     */
    @PostMapping
    public ResponseEntity<String> crearPedido(@RequestBody Pedido pedido) {
        System.out.println("[API] Pedido recibido: " + pedido);

        // Publicar en RabbitMQ: cola pedidos + notificaciones + reportes
        pedidoProducer.enviarPedido(pedido);
        pedidoProducer.enviarNotificacion(pedido);
        pedidoProducer.enviarReporte(pedido);

        // Publicar en SQS (ElasticMQ)
        pedidoSqsProducer.enviarPedido(pedido);

        return ResponseEntity.ok("Pedido encolado correctamente en RabbitMQ y SQS");
    }
}
