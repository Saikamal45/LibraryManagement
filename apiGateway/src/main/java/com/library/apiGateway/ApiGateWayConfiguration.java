package com.library.apiGateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGateWayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p->p.path("/get")
                        .filters(f -> f
                                .addRequestHeader("MyHeader", "MYURI")
                                .addRequestParameter("Param","MyValue"))
                        .uri("http://httpbin.org:80"))
                .route(p -> p.path("/books/**")
                        .uri("lb://BOOKSERVICE"))
                .route(p -> p.path("/orders/**")
                        .uri("lb://ORDERSERVICE"))

                .build();
    }
}
