package com.gavin.cloud.reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class ReactorTest {

    @Test
    public void testFlux() {
        Flux.range(1, 10).subscribe(System.out::println);
    }

    @Test
    public void testMono() {
        Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
    }

}