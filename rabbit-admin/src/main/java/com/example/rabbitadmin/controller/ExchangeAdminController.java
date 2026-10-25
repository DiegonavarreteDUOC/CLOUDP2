package com.example.rabbitadmin.controller;

import com.example.rabbitadmin.dto.ExchangeRequest;
import com.example.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para administracion de exchanges RabbitMQ.
 */
@RestController
@RequestMapping("/exchanges")
public class ExchangeAdminController {

    private final RabbitAdminService rabbitAdminService;

    public ExchangeAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    /**
     * Crea un nuevo exchange en RabbitMQ.
     * POST /exchanges
     */
    @PostMapping
    public ResponseEntity<String> crearExchange(@Valid @RequestBody ExchangeRequest request) {
        rabbitAdminService.crearExchange(request);
        return ResponseEntity.ok("Exchange '" + request.getName() + "' de tipo '" + request.getType() + "' creado correctamente");
    }

    /**
     * Elimina un exchange existente de RabbitMQ.
     * DELETE /exchanges/{name}
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<String> eliminarExchange(@PathVariable String name) {
        rabbitAdminService.eliminarExchange(name);
        return ResponseEntity.ok("Exchange '" + name + "' eliminado correctamente");
    }
}
