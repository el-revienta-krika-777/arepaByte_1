package com.projecto.ecomarket.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "codigoms-favoritos",
        version = "1.0",
        description = "Gestiona la lista de productos favoritos por usuario. " +
                    "A diferencia de carrito y pedidos, usa WebClient (reactivo) en lugar de FeignClient (declarativo) " +
                    "para comunicarse con codigoms-usuarios y codigoms-catalogo. " +
                    "Puerto: 8085"
    )
)
@Configuration
public class OpenApiConfig {}
