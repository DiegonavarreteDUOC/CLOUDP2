package com.example.minipedidos.service;

import com.example.minipedidos.model.Pedido;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import tools.jackson.databind.ObjectMapper;

/**
 * Consumidor SQS para ElasticMQ.
 * Recibe y procesa mensajes de la cola SQS local de forma periodica.
 */
@Service
public class PedidoSqsConsumer {

    @Value("${sqs.queue-url}")
    private String queueUrl;

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public PedidoSqsConsumer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 3000)
    public void recibirPedidos() {

        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(1)
                .build();

        List<Message> mensajes = sqsClient.receiveMessage(request).messages();

        for (Message mensaje : mensajes) {
            try {
                Pedido pedido = objectMapper.readValue(mensaje.body(), Pedido.class);

                System.out.println("==============================");
                System.out.println("[SQS] Pedido recibido desde ElasticMQ");
                System.out.println("  ID:       " + pedido.getId());
                System.out.println("  Producto: " + pedido.getProducto());
                System.out.println("  Cantidad: " + pedido.getCantidad());
                System.out.println("  Cliente:  " + pedido.getCliente());

                // Eliminar mensaje de la cola (equivalente a ACK en SQS)
                DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(mensaje.receiptHandle())
                        .build();

                sqsClient.deleteMessage(deleteRequest);
                System.out.println("  [OK] Mensaje eliminado de SQS: " + mensaje.messageId());
                System.out.println("==============================");

            } catch (Exception e) {
                System.err.println("[SQS] Error procesando mensaje: " + e.getMessage());
            }
        }
    }
}
