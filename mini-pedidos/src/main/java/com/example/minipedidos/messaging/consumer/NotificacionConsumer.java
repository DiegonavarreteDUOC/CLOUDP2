package com.example.minipedidos.messaging.consumer;

import com.example.minipedidos.model.Pedido;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Consumidor del dominio NOTIFICACIONES.
 * Gestiona el envio de notificaciones asincronas asociadas a pedidos.
 */
@Service
public class NotificacionConsumer {

    @RabbitListener(queues = "${rabbitmq.queues.notificaciones}")
    public void procesarNotificacion(
            Pedido pedido,
            Channel channel,
            Message message) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            System.out.println("==============================");
            System.out.println("[NOTIFICACIONES] Enviando notificacion para pedido ID: " + pedido.getId());
            System.out.println("  Cliente:  " + pedido.getCliente());
            System.out.println("  Producto: " + pedido.getProducto());

            if (pedido.getCliente() == null || pedido.getCliente().isBlank()) {
                throw new IllegalArgumentException("No se puede notificar: cliente vacio");
            }

            System.out.println("  [OK] Notificacion enviada al cliente: " + pedido.getCliente());
            channel.basicAck(deliveryTag, false);
            System.out.println("  [OK] ACK enviado en cola notificaciones.");
            System.out.println("==============================");

        } catch (IllegalArgumentException e) {
            System.err.println("[NOTIFICACIONES] Notificacion rechazada: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
            System.err.println("[NOTIFICACIONES] NACK enviado. Mensaje enrutado a notificaciones.dlq.");
            System.err.println("==============================");

        } catch (Exception e) {
            System.err.println("[NOTIFICACIONES] Error inesperado: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
            System.err.println("==============================");
        }
    }
}
