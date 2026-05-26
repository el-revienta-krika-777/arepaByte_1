package com.projecto.ecomarket.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "ecomarket_usuario",
        version = "1.0",
        description = "Proveedor de datos de usuarios. " +
                    "Los microservicios carrito y pedidos lo consultan via FeignClient. " +
                    "El microservicio favoritos lo consulta via WebClient. " +
                    "Endpoint clave: GET /api/usuarios/nombre/{nombre} " +
                    "Puerto: 8084"
    )
)
@Configuration
public class OpenAPIConfig {}