package com.example.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para creacion de exchanges.
 * Valida nombre y tipo de exchange (direct, topic, fanout, headers).
 */
public class ExchangeRequest {

    @NotBlank(message = "El nombre del exchange no puede estar vacio")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "El nombre solo puede contener letras, numeros, puntos, guiones y guiones bajos"
    )
    private String name;

    @NotBlank(message = "El tipo de exchange es obligatorio")
    @Pattern(
        regexp = "^(direct|topic|fanout|headers)$",
        message = "El tipo debe ser: direct, topic, fanout o headers"
    )
    private String type;

    private boolean durable = true;
    private boolean autoDelete = false;

    public ExchangeRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isDurable() { return durable; }
    public void setDurable(boolean durable) { this.durable = durable; }

    public boolean isAutoDelete() { return autoDelete; }
    public void setAutoDelete(boolean autoDelete) { this.autoDelete = autoDelete; }
}
