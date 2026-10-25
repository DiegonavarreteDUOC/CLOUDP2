package com.example.minipedidos.messaging.consumer;

import com.example.minipedidos.model.Pedido;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Consumidor del dominio REPORTES.
 * Gestiona la generacion asincrona de reportes y tickets de pedidos.
 */
@Service
public class ReporteConsumer {

    @RabbitListener(queues = "${rabbitmq.queues.reportes}")
    public void procesarReporte(
            Pedido pedido,
            Channel channel,
            Message message) throws IOException {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            System.out.println("==============================");
            System.out.println("[REPORTES] Generando reporte para pedido ID: " + pedido.getId());
            System.out.println("  Producto: " + pedido.getProducto());
            System.out.println("  Cantidad: " + pedido.getCantidad());

            if (pedido.getId() == null) {
                throw new IllegalArgumentException("No se puede generar reporte: ID de pedido nulo");
            }

            System.out.println("  [OK] Reporte generado: TICKET-" + pedido.getId() + ".pdf");
            channel.basicAck(deliveryTag, false);
            System.out.println("  [OK] ACK enviado en cola reportes.");
            System.out.println("==============================");

        } catch (IllegalArgumentException e) {
            System.err.println("[REPORTES] Reporte rechazado: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
            System.err.println("[REPORTES] NACK enviado. Mensaje enrutado a reportes.dlq.");
            System.err.println("==============================");

        } catch (Exception e) {
            System.err.println("[REPORTES] Error inesperado: " + e.getMessage());
            channel.basicNack(deliveryTag, false, false);
            System.err.println("==============================");
        }
    }
}
