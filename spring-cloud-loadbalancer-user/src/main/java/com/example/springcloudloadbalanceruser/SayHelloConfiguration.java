package com.example.springcloudloadbalanceruser;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Flux;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Olga Maciaszek-Sharma
 */
@Configuration
public class SayHelloConfiguration {

    @Bean
    @Primary
    ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new DemoServiceInstanceListSuppler("say-hello");
    }


    @Bean
    ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
                                                            LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(loadBalancerClientFactory
                .getLazyProvider(name, ServiceInstanceListSupplier.class),
                name);
    }

}

class DemoServiceInstanceListSuppler implements ServiceInstanceListSupplier {

    private final String serviceId;

    DemoServiceInstanceListSuppler(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(Arrays
                .asList(new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 8090, false),
                        new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 8091, false),
                        new DefaultServiceInstance(serviceId + "3", serviceId, "localhost", 8092, false)));
    }
}