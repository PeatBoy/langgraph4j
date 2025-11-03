package org.bsc.langgraph4j.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.*;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public interface Instrumentable {

    default OpenTelemetry openTelemetry() {
        return Optional.ofNullable( io.opentelemetry.api.GlobalOpenTelemetry.get() )
                .orElseGet(OpenTelemetry::noop);
    }

    final class TracerHolder {
        final String scope;
        private final Instrumentable owner;

        public TracerHolder( Instrumentable owner, String scope ) {
            this.scope = requireNonNull( scope, "scope cannot be null");
            this.owner = requireNonNull(owner, "owner cannot be null");
        }

        public Tracer tracer() {
            return owner.openTelemetry().getTracer(scope);
        }

        public SpanBuilder spanBuilder(String spanName ) {
            return tracer().spanBuilder( requireNonNull(spanName, "spanName cannot be null"));
        }

    }

    final class MeterHolder {
        final String scope;
        private final Instrumentable owner;

        public MeterHolder(Instrumentable owner, String scope ) {
            this.scope = requireNonNull( scope, "scope cannot be null");
            this.owner = requireNonNull(owner, "owner cannot be null");
        }

        public Meter meter() {
            return owner.openTelemetry().getMeter(scope);
        }

        public LongCounterBuilder countBuilder(String counterName ) {
            return meter().counterBuilder( requireNonNull(counterName, "counterName cannot be null"));
        }

        public DoubleGaugeBuilder gaugeBuilder(String gaugeName ) {
            return meter().gaugeBuilder( requireNonNull(gaugeName, "gaugeName cannot be null"));
        }

        public DoubleHistogramBuilder histogramBuilder(String histogramName ) {
            return meter().histogramBuilder( requireNonNull(histogramName, "histogramName cannot be null"));
        }

        public LongUpDownCounterBuilder upDownCounterBuilder(String upDownCounterName ) {
            return meter().upDownCounterBuilder( requireNonNull(upDownCounterName, "upDownCounterName cannot be null"));
        }

    }

    default TracerHolder trace(String scope ) {
        return new TracerHolder( this, scope );
    }

    default MeterHolder meter(String scope ) {
        return new MeterHolder( this, scope );
    }

}
