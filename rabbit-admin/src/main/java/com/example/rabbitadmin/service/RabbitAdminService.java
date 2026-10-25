package com.example.rabbitadmin.service;

import com.example.rabbitadmin.dto.BindingRequest;
import com.example.rabbitadmin.dto.ExchangeRequest;
import com.example.rabbitadmin.dto.QueueRequest;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio encapsulado para administracion de RabbitMQ.
 * Toda la logica de creacion y eliminacion de colas, exchanges y bindings
 * se centraliza aqui. Los controladores llaman metodos de alto nivel.
 */
@Service
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    // ==========================================
    // OPERACIONES DE COLAS
    // ==========================================

    /**
     * Crea una nueva cola en RabbitMQ.
     * Soporta configuracion de DLX/DLQ opcional.
     */
    public void crearCola(QueueRequest request) {
        Map<String, Object> args = new HashMap<>();

        if (request.getDeadLetterExchange() != null && !request.getDeadLetterExchange().isBlank()) {
            args.put("x-dead-letter-exchange", request.getDeadLetterExchange());
        }
        if (request.getDeadLetterRoutingKey() != null && !request.getDeadLetterRoutingKey().isBlank()) {
            args.put("x-dead-letter-routing-key", request.getDeadLetterRoutingKey());
        }

        Queue queue = new Queue(request.getName(), request.isDurable(), false, request.isAutoDelete(), args.isEmpty() ? null : args);
        rabbitAdmin.declareQueue(queue);

        System.out.println("[ADMIN] Cola creada: " + request.getName());
    }

    /**
     * Elimina una cola existente de RabbitMQ.
     */
    public void eliminarCola(String nombre) {
        rabbitAdmin.deleteQueue(nombre);
        System.out.println("[ADMIN] Cola eliminada: " + nombre);
    }

    // ==========================================
    // OPERACIONES DE EXCHANGES
    // ==========================================

    /**
     * Crea un nuevo exchange en RabbitMQ segun el tipo especificado.
     */
    public void crearExchange(ExchangeRequest request) {
        Exchange exchange = switch (request.getType().toLowerCase()) {
            case "direct"  -> new DirectExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            case "topic"   -> new TopicExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            case "fanout"  -> new FanoutExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            case "headers" -> new HeadersExchange(request.getName(), request.isDurable(), request.isAutoDelete());
            default -> throw new IllegalArgumentException("Tipo de exchange no soportado: " + request.getType());
        };

        rabbitAdmin.declareExchange(exchange);
        System.out.println("[ADMIN] Exchange creado: " + request.getName() + " (tipo: " + request.getType() + ")");
    }

    /**
     * Elimina un exchange existente de RabbitMQ.
     */
    public void eliminarExchange(String nombre) {
        rabbitAdmin.deleteExchange(nombre);
        System.out.println("[ADMIN] Exchange eliminado: " + nombre);
    }

    // ==========================================
    // OPERACIONES DE BINDINGS
    // ==========================================

    /**
     * Crea un binding entre una cola y un exchange.
     */
    public void crearBinding(BindingRequest request) {
        Binding binding = new Binding(
                request.getQueueName(),
                Binding.DestinationType.QUEUE,
                request.getExchangeName(),
                request.getRoutingKey(),
                null
        );
        rabbitAdmin.declareBinding(binding);
        System.out.println("[ADMIN] Binding creado: " + request.getExchangeName() + " -> " + request.getQueueName() + " [" + request.getRoutingKey() + "]");
    }

    /**
     * Elimina un binding existente.
     */
    public void eliminarBinding(BindingRequest request) {
        Binding binding = new Binding(
                request.getQueueName(),
                Binding.DestinationType.QUEUE,
                request.getExchangeName(),
                request.getRoutingKey(),
                null
        );
        rabbitAdmin.removeBinding(binding);
        System.out.println("[ADMIN] Binding eliminado: " + request.getExchangeName() + " -> " + request.getQueueName());
    }
}
