package com.example.minipedidos.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Modelo de dominio: Pedido.
 * Incluye validaciones de entrada para la API REST.
 */
public class Pedido {

    private Long id;

    @NotBlank(message = "El producto no puede estar vacio")
    private String producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor que cero")
    private Integer cantidad;

    @NotBlank(message = "El cliente no puede estar vacio")
    private String cliente;

    public Pedido() {
    }

    public Pedido(Long id, String producto, Integer cantidad, String cliente) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", producto='" + producto + '\'' +
                ", cantidad=" + cantidad +
                ", cliente='" + cliente + '\'' +
                '}';
    }
}
