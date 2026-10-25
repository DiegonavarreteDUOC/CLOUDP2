package com.example.rabbitadmin.controller;

import com.example.rabbitadmin.dto.BindingRequest;
import com.example.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para administracion de bindings RabbitMQ.
 */
@RestController
@RequestMapping("/bindings")
public class BindingAdminController {

    private final RabbitAdminService rabbitAdminService;

    public BindingAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    /**
     * Crea un binding entre una cola y un exchange.
     * POST /bindings
     */
    @PostMapping
    public ResponseEntity<String> crearBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.crearBinding(request);
        return ResponseEntity.ok("Binding creado: " + request.getExchangeName() + " -> " + request.getQueueName() + " [" + request.getRoutingKey() + "]");
    }

    /**
     * Elimina un binding existente.
     * DELETE /bindings
     */
    @DeleteMapping
    public ResponseEntity<String> eliminarBinding(@Valid @RequestBody BindingRequest request) {
        rabbitAdminService.eliminarBinding(request);
        return ResponseEntity.ok("Binding eliminado: " + request.getExchangeName() + " -> " + request.getQueueName());
    }
}
