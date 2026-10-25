package com.example.rabbitadmin.controller;

import com.example.rabbitadmin.dto.QueueRequest;
import com.example.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para administracion de colas RabbitMQ.
 * Expone endpoints bien definidos siguiendo convencion REST.
 */
@RestController
@RequestMapping("/queues")
public class QueueAdminController {

    private final RabbitAdminService rabbitAdminService;

    public QueueAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    /**
     * Crea una nueva cola en RabbitMQ.
     * POST /queues
     */
    @PostMapping
    public ResponseEntity<String> crearCola(@Valid @RequestBody QueueRequest request) {
        rabbitAdminService.crearCola(request);
        return ResponseEntity.ok("Cola '" + request.getName() + "' creada correctamente");
    }

    /**
     * Elimina una cola existente de RabbitMQ.
     * DELETE /queues/{name}
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<String> eliminarCola(@PathVariable String name) {
        rabbitAdminService.eliminarCola(name);
        return ResponseEntity.ok("Cola '" + name + "' eliminada correctamente");
    }
}
