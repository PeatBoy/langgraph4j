package org.bsc.langgraph4j.spring.ai.agentexecutor;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAIDemoOtelApplication {


    SpringAIDemoOtelApplication( OpenTelemetry openTelemetrySdk ) {

        GlobalOpenTelemetry.set( openTelemetrySdk );
    }
    public static void main(String[] args) {

        SpringApplication.run( SpringAIDemoOtelApplication.class, args );

    }
}
