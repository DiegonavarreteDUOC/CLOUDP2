package com.example.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para creacion de bindings entre queue y exchange.
 */
public class BindingRequest {

    @NotBlank(message = "El nombre del exchange no puede estar vacio")
    private String exchangeName;

    @NotBlank(message = "El nombre de la cola no puede estar vacio")
    private String queueName;

    @NotBlank(message = "La routing key no puede estar vacia")
    private String routingKey;

    public BindingRequest() {}

    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }

    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }

    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
}
