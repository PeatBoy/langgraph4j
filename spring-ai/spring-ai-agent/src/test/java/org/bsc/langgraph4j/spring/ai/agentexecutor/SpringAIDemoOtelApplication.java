package org.bsc.langgraph4j.spring.ai.agentexecutor;

import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAIDemoOtelApplication {

    @Autowired
    OpenTelemetrySdk openTelemetrySdk;

    public static void main(String[] args) {

        SpringApplication.run( SpringAIDemoOtelApplication.class, args );

    }
}
