package org.sds.sdslocation.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author joseph.kibe
 * Created On 22/03/2025 18:07
 **/
@Configuration
@SuppressWarnings("unused")
public class SwaggerConfig {
    private static final String API_RESPONSE_SCHEMA = "#/components/schemas/ApiResponse";
    public static final String BEARER_AUTH = "bearerAuth";
    private static final String SECURITY_SCHEME = BEARER_AUTH;


    @Bean
    public GroupedOpenApi fullApi() {
        return GroupedOpenApi.builder()
                .group("Internal")
                .pathsToMatch("/**")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SDS Location Service")
                        .description("""
                                ## SDS Location Service
                                
                                This is the API documentation for the SDS Location Service.
                               
                                """)
                        .version("SDS LOCATION SERVICE")
                        .license(new License().name("Product Of SDS.")))
                .externalDocs(new ExternalDocumentation()
                        .description("")
                        .url(""))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your Bearer token in the format: Bearer <token>")))
                .security(List.of(new SecurityRequirement().addList(BEARER_AUTH)))
                .extensions(Map.of(
                        "x-tagGroups", List.of(
                                Map.of(
                                        "name", "API Groups",
                                        "tags", List.of("SDS Service Interface", "Other Tags...")
                                )
                        ),
                        // This is what makes tags closed by default
                        "x-display-tag-groups", "closed"
                ));
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (operation, handlerMethod) -> operation.addSecurityItem(
                new SecurityRequirement().addList(BEARER_AUTH));
    }

    @Bean
    public SwaggerIndexPageTransformer swaggerIndexPageTransformer(
            SwaggerUiConfigProperties swaggerUiConfig,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider) {

        return new SwaggerIndexPageTransformer(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider) {
            @Override
            public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
                // Get the default resource first
                Resource defaultResource = super.transform(request, resource, transformerChain);

                // CRITICAL FIX: Only modify the HTML file. Let JS and CSS files pass through untouched!
                if (resource.getFilename() != null && resource.getFilename().contains("index.html")) {
                    String html = StreamUtils.copyToString(defaultResource.getInputStream(), StandardCharsets.UTF_8);

                    String contextPath = request.getContextPath();
                    String cssUrl = contextPath + "/swagger-custom.css";

                    String customHtml = html.replace("</head>",
                            "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssUrl + "\">\n</head>");

                    return new TransformedResource(defaultResource, customHtml.getBytes(StandardCharsets.UTF_8));
                }

                // Return all other files (JS, CSS, PNG) perfectly untouched
                return defaultResource;
            }

        };
    }
}
