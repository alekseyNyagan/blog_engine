package main.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Blog Engine REST API",
                version = "1.0",
                description = "Blog Engine",
                contact = @Contact(name = "Aleksey")
        )
)
public class OpenApiConfig {
}
