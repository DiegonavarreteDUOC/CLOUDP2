package com.example.minipedidos.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion centralizada de RabbitMQ.
 * Define todos los beans de Queue, Exchange y Binding para cada caso de uso.
 * Los nombres se leen desde application.yml para facilitar el mantenimiento.
 */
@Configuration
public class RabbitMQConfig {

    // ==========================================
    // NOMBRES DE COLAS (inyectados desde application.yml)
    // ==========================================
    @Value("${rabbitmq.queues.pedidos}")
    private String colasPedidos;

    @Value("${rabbitmq.queues.notificaciones}")
    private String colasNotificaciones;

    @Value("${rabbitmq.queues.reportes}")
    private String colasReportes;

    @Value("${rabbitmq.queues.pedidos-dlq}")
    private String colasPedidosDlq;

    @Value("${rabbitmq.queues.notificaciones-dlq}")
    private String colasNotificacionesDlq;

    @Value("${rabbitmq.queues.reportes-dlq}")
    private String colasReportesDlq;

    // ==========================================
    // NOMBRES DE EXCHANGES (inyectados desde application.yml)
    // ==========================================
    @Value("${rabbitmq.exchanges.pedidos-direct}")
    private String exchangePedidosDirect;

    @Value("${rabbitmq.exchanges.pedidos-topic}")
    private String exchangePedidosTopic;

    @Value("${rabbitmq.exchanges.pedidos-dlx}")
    private String exchangePedidosDlx;

    @Value("${rabbitmq.exchanges.notificaciones-dlx}")
    private String exchangeNotificacionesDlx;

    @Value("${rabbitmq.exchanges.reportes-dlx}")
    private String exchangeReportesDlx;

    // ==========================================
    // ROUTING KEYS (inyectadas desde application.yml)
    // ==========================================
    @Value("${rabbitmq.bindings.pedidos-routing-key}")
    private String rkPedidos;

    @Value("${rabbitmq.bindings.notificaciones-routing-key}")
    private String rkNotificaciones;

    @Value("${rabbitmq.bindings.reportes-routing-key}")
    private String rkReportes;

    @Value("${rabbitmq.bindings.pedidos-dlq-routing-key}")
    private String rkPedidosDlq;

    @Value("${rabbitmq.bindings.notificaciones-dlq-routing-key}")
    private String rkNotificacionesDlq;

    @Value("${rabbitmq.bindings.reportes-dlq-routing-key}")
    private String rkReportesDlq;

    // ==========================================
    // EXCHANGES: Direct y Topic
    // ==========================================

    @Bean
    public DirectExchange pedidosDirectExchange() {
        return new DirectExchange(exchangePedidosDirect, true, false);
    }

    @Bean
    public TopicExchange pedidosTopicExchange() {
        return new TopicExchange(exchangePedidosTopic, true, false);
    }

    // ==========================================
    // DEAD LETTER EXCHANGES (DLX)
    // ==========================================

    @Bean
    public DirectExchange pedidosDlxExchange() {
        return new DirectExchange(exchangePedidosDlx, true, false);
    }

    @Bean
    public DirectExchange notificacionesDlxExchange() {
        return new DirectExchange(exchangeNotificacionesDlx, true, false);
    }

    @Bean
    public DirectExchange reportesDlxExchange() {
        return new DirectExchange(exchangeReportesDlx, true, false);
    }

    // ==========================================
    // COLAS PRINCIPALES (con DLX configurado)
    // ==========================================

    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(colasPedidos)
                .withArgument("x-dead-letter-exchange", exchangePedidosDlx)
                .withArgument("x-dead-letter-routing-key", rkPedidosDlq)
                .build();
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(colasNotificaciones)
                .withArgument("x-dead-letter-exchange", exchangeNotificacionesDlx)
                .withArgument("x-dead-letter-routing-key", rkNotificacionesDlq)
                .build();
    }

    @Bean
    public Queue reportesQueue() {
        return QueueBuilder.durable(colasReportes)
                .withArgument("x-dead-letter-exchange", exchangeReportesDlx)
                .withArgument("x-dead-letter-routing-key", rkReportesDlq)
                .build();
    }

    // ==========================================
    // DEAD LETTER QUEUES (DLQ)
    // ==========================================

    @Bean
    public Queue pedidosDlq() {
        return QueueBuilder.durable(colasPedidosDlq).build();
    }

    @Bean
    public Queue notificacionesDlq() {
        return QueueBuilder.durable(colasNotificacionesDlq).build();
    }

    @Bean
    public Queue reportesDlq() {
        return QueueBuilder.durable(colasReportesDlq).build();
    }

    // ==========================================
    // BINDINGS: Colas principales -> Direct Exchange
    // ==========================================

    @Bean
    public Binding pedidosBinding(Queue pedidosQueue, DirectExchange pedidosDirectExchange) {
        return BindingBuilder.bind(pedidosQueue).to(pedidosDirectExchange).with(rkPedidos);
    }

    @Bean
    public Binding notificacionesBinding(Queue notificacionesQueue, DirectExchange pedidosDirectExchange) {
        return BindingBuilder.bind(notificacionesQueue).to(pedidosDirectExchange).with(rkNotificaciones);
    }

    @Bean
    public Binding reportesBinding(Queue reportesQueue, TopicExchange pedidosTopicExchange) {
        return BindingBuilder.bind(reportesQueue).to(pedidosTopicExchange).with("reportes.#");
    }

    // ==========================================
    // BINDINGS: DLQ -> DLX
    // ==========================================

    @Bean
    public Binding pedidosDlqBinding(Queue pedidosDlq, DirectExchange pedidosDlxExchange) {
        return BindingBuilder.bind(pedidosDlq).to(pedidosDlxExchange).with(rkPedidosDlq);
    }

    @Bean
    public Binding notificacionesDlqBinding(Queue notificacionesDlq, DirectExchange notificacionesDlxExchange) {
        return BindingBuilder.bind(notificacionesDlq).to(notificacionesDlxExchange).with(rkNotificacionesDlq);
    }

    @Bean
    public Binding reportesDlqBinding(Queue reportesDlq, DirectExchange reportesDlxExchange) {
        return BindingBuilder.bind(reportesDlq).to(reportesDlxExchange).with(rkReportesDlq);
    }

    // ==========================================
    // CONVERSOR DE MENSAJES JSON
    // ==========================================

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
