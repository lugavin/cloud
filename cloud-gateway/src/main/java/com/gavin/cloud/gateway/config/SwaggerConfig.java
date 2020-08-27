package com.gavin.cloud.gateway.config;

import com.gavin.cloud.common.base.util.Constants;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Retrieves all registered microservices Swagger resources.
 * For example:
 *
 * <a href="http://127.0.0.1:9000/rest/auth/swagger-ui.html">Auth RESTful API</a>
 */
@Primary
@Component
@Profile(Constants.PROFILE_SWAGGER)
class SwaggerConfig implements SwaggerResourcesProvider {

    private final RouteLocator routeLocator;
    private final DiscoveryClient discoveryClient;

    public SwaggerConfig(RouteLocator routeLocator, DiscoveryClient discoveryClient) {
        this.routeLocator = routeLocator;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public List<SwaggerResource> get() {
        return routeLocator.getRoutes().stream()
                // Add the registered microservices swagger docs as additional swagger resources
                .map(r -> swaggerResource(r.getId(), r.getFullPath().replace("**", "v2/api-docs")))
                .collect(
                        Collectors.collectingAndThen(Collectors.toList(), c -> {
                            // Add the default swagger resource that correspond to the gateway's own swagger doc
                            c.add(swaggerResource("default", "/v2/api-docs"));
                            return c;
                        })
                );
    }

    private SwaggerResource swaggerResource(String name, String location) {
        // 根据serviceName获取所有服务实例(一个服务可部署多个实例)
        //List<ServiceInstance> instances = discoveryClient.getInstances(name);
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

}
