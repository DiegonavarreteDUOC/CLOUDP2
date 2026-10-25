package com.example.minipedidos.messaging.consumer;

import com.example.minipedidos.model.Pedido;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Consumidor del dominio PEDIDOS.
 * Gestiona el procesamiento de mensajes de la cola principal de pedidos.
 * Implementa ACK manual y NACK con enrutamiento a DLQ en caso de error.
 */
@Service
public class PedidoConsumer {

    @RabbitListener(queues = "${rabbitmq.queues.pedidos}")
    public void procesarPedido(
            Pedido pedido,
            Channel channel,
            Message message) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            System.out.println("==============================");
            System.out.println("[PEDIDOS] Mensaje recibido de RabbitMQ");
            System.out.println("  ID:       " + pedido.getId());
            System.out.println("  Producto: " + pedido.getProducto());
            System.out.println("  Cantidad: " + pedido.getCantidad());
            System.out.println("  Cliente:  " + pedido.getCliente());

            // Validacion de negocio antes de confirmar
            if (pedido.getCantidad() == null || pedido.getCantidad() <= 0) {
                throw new IllegalArgumentException(
                        "Cantidad invalida: debe ser mayor que cero"
                );
            }

            // Pedido valido: confirmar con ACK
            channel.basicAck(deliveryTag, false);

            System.out.println("  [OK] ACK enviado. Pedido procesado correctamente.");
            System.out.println("==============================");

        } catch (IllegalArgumentException e) {

            System.err.println("[PEDIDOS] Pedido rechazado: " + e.getMessage());

            // NACK sin reencolar -> mensaje va a DLX -> DLQ
            channel.basicNack(deliveryTag, false, false);

            System.err.println("[PEDIDOS] NACK enviado (requeue=false). Mensaje enrutado a DLQ (pedidos.dlq).");
            System.err.println("==============================");

        } catch (Exception e) {

            System.err.println("[PEDIDOS] Error inesperado: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
            System.err.println("[PEDIDOS] NACK enviado por error inesperado. Mensaje enrutado a DLQ.");
            System.err.println("==============================");
        }
    }
}
