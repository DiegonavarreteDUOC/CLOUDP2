package com.example.minipedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada de la aplicacion Mini Pedidos.
 * Habilita el scheduling para el consumidor SQS periodico.
 */
@SpringBootApplication
@EnableScheduling
public class MiniPedidosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniPedidosApplication.class, args);
    }
}
