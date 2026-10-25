package com.example.minipedidos.service;

import com.example.minipedidos.model.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Productor SQS para ElasticMQ.
 * Envia mensajes de pedidos al servicio SQS simulado localmente.
 */
@Service
public class PedidoSqsProducer {

    @Value("${sqs.queue-url}")
    private String queueUrl;

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public PedidoSqsProducer(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    public void enviarPedido(Pedido pedido) {

        try {
            String pedidoJson = objectMapper.writeValueAsString(pedido);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(pedidoJson)
                    .build();

            SendMessageResponse response = sqsClient.sendMessage(request);

            System.out.println("[SQS] Pedido enviado a ElasticMQ. MessageId: " + response.messageId());

        } catch (JacksonException e) {
            throw new RuntimeException("Error convirtiendo Pedido a JSON para SQS", e);
        }
    }
}
