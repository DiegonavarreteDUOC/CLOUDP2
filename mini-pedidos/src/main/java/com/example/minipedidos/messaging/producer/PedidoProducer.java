package com.example.minipedidos.messaging.producer;

import com.example.minipedidos.model.Pedido;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Productor del dominio PEDIDOS.
 * Envia mensajes al Direct Exchange y al Topic Exchange de pedidos.
 */
@Service
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.pedidos-direct}")
    private String exchangeDirect;

    @Value("${rabbitmq.exchanges.pedidos-topic}")
    private String exchangeTopic;

    @Value("${rabbitmq.bindings.pedidos-routing-key}")
    private String rkPedidos;

    @Value("${rabbitmq.bindings.notificaciones-routing-key}")
    private String rkNotificaciones;

    @Value("${rabbitmq.bindings.reportes-routing-key}")
    private String rkReportes;

    public PedidoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Envia pedido a la cola principal via Direct Exchange.
     */
    public void enviarPedido(Pedido pedido) {
        rabbitTemplate.convertAndSend(exchangeDirect, rkPedidos, pedido);
        System.out.println("[PRODUCER] Pedido enviado a direct exchange '" + exchangeDirect + "' con routing key '" + rkPedidos + "': " + pedido);
    }

    /**
     * Envia evento de notificacion via Direct Exchange.
     */
    public void enviarNotificacion(Pedido pedido) {
        rabbitTemplate.convertAndSend(exchangeDirect, rkNotificaciones, pedido);
        System.out.println("[PRODUCER] Notificacion enviada a direct exchange '" + exchangeDirect + "': " + pedido);
    }

    /**
     * Envia solicitud de reporte via Topic Exchange.
     */
    public void enviarReporte(Pedido pedido) {
        rabbitTemplate.convertAndSend(exchangeTopic, rkReportes, pedido);
        System.out.println("[PRODUCER] Reporte enviado a topic exchange '" + exchangeTopic + "' con routing key '" + rkReportes + "': " + pedido);
    }
}
