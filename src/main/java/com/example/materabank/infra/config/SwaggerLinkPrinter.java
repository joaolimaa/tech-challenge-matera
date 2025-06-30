package com.example.materabank.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SwaggerLinkPrinter implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerLinkPrinter.class);

    @Value("${server.port:8080}")
    private int port;

    @Value("${swagger.path:/swagger-ui/index.html}")
    private String swaggerPath;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String url = "http://localhost:" + port + swaggerPath;
        logger.info("Swagger UI disponível em: {}", url);
    }
}
