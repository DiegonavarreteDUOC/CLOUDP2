package com.example.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para creacion de colas.
 * Incluye validaciones para evitar nombres vacios o con caracteres invalidos.
 */
public class QueueRequest {

    @NotBlank(message = "El nombre de la cola no puede estar vacio")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "El nombre solo puede contener letras, numeros, puntos, guiones y guiones bajos"
    )
    private String name;

    private boolean durable = true;
    private boolean autoDelete = false;
    private String deadLetterExchange;
    private String deadLetterRoutingKey;

    public QueueRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }

    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }

    public String getDeadLetterExchange() { return deadLetterExchange; }
    public void setDeadLetterExchange(String deadLetterExchange) { this.deadLetterExchange = deadLetterExchange; }

    public String getDeadLetterRoutingKey() { return deadLetterRoutingKey; }
    public void setDeadLetterRoutingKey(String deadLetterRoutingKey) { this.deadLetterRoutingKey = deadLetterRoutingKey; }
}
