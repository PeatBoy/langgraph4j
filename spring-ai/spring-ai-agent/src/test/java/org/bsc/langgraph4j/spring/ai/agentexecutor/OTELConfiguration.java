package org.bsc.langgraph4j.spring.ai.agentexecutor;

import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Properties;

@Configuration
public class OTELConfiguration {

    @PostConstruct
    public void loadOtelPropertiesToSystem() throws Exception {

        var resource = new ClassPathResource("otel-config.properties");

        var props = new Properties();
        try (InputStream input = resource.getInputStream()) {
            props.load(input);
        }

        System.getProperties().putAll(props);

    }

    @Bean
    public OpenTelemetrySdk openTelemetry() throws Exception {

        // var props = System.getProperties();

        var autoConfig = AutoConfiguredOpenTelemetrySdk.initialize();

        var otel = autoConfig.getOpenTelemetrySdk();

        OpenTelemetryAppender.install(otel);

        return otel;
    }
}
