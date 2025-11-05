package org.bsc.langgraph4j.otel;


import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.*;
import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public interface Instrumentable {

    org.slf4j.Logger otelLog = org.slf4j.LoggerFactory.getLogger("langgraph4j-otel");

    default OpenTelemetry otel() {
        return Optional.ofNullable( io.opentelemetry.api.GlobalOpenTelemetry.get() )
                .orElseGet(OpenTelemetry::noop);
    }

    final class TracerHolder {
        @FunctionalInterface
        public interface TryFunction<R> {
            R apply( Span span ) throws Exception;
        }

        public class SB implements SpanBuilder {

            private final SpanBuilder delegate ;

            public SB(SpanBuilder delegate ) {
                this.delegate = requireNonNull( delegate, "delegate cannot be null");
            }

            @Override
            public SB setParent(Context context) {
                delegate.setParent(context);
                return this;
            }

            @Override
            public SB setNoParent() {
                delegate.setNoParent();
                return this;
            }

            @Override
            public SB addLink(SpanContext spanContext) {
                delegate.addLink(spanContext);
                return this;
            }

            @Override
            public SB addLink(SpanContext spanContext, Attributes attributes) {
                delegate.addLink(spanContext, attributes);
                return this;
            }

            @Override
            public SB setAttribute(String s, String s1) {
                delegate.setAttribute(s, s1);
                return this;
            }

            @Override
            public SB setAttribute(String s, long l) {
                delegate.setAttribute(s, l);
                return this;
            }

            @Override
            public SB setAttribute(String s, double v) {
                delegate.setAttribute(s, v);
                return this;
            }

            @Override
            public SB setAttribute(String s, boolean b) {
                delegate.setAttribute(s, b);
                return this;
            }

            @Override
            public <T> SB setAttribute(AttributeKey<T> attributeKey, T t) {
                delegate.setAttribute(attributeKey, t);
                return this;
            }

            @Override
            public SB setAttribute(AttributeKey<Long> key, int value) {
                delegate.setAttribute(key, value);
                return this;
            }

            @Override
            public SB setAllAttributes(Attributes attributes) {
                delegate.setAllAttributes(attributes);
                return this;
            }

            @Override
            public SB setSpanKind(SpanKind spanKind) {
                delegate.setSpanKind(spanKind);
                return this;
            }

            @Override
            public SB setStartTimestamp(long l, TimeUnit timeUnit) {
                delegate.setStartTimestamp(l, timeUnit);
                return this;
            }

            @Override
            public SB setStartTimestamp(Instant startTimestamp) {
                delegate.setStartTimestamp(startTimestamp);
                return this;
            }

            @Override
            public Span startSpan() {
                return delegate.startSpan();
            }

            public <R> R startSpan( TryFunction<R> function ) throws Exception {
                requireNonNull( function, "function cannot be null");
                var span = delegate.startSpan();

                try {
                    var result = function.apply( span );
                    span.setStatus( StatusCode.OK );
                    return result;
                }
                catch( Exception e ) {
                    if( span.isRecording() ) {
                        span.recordException( e );
                    }
                    throw e;
                }
                finally {
                    span.end();
                }

            }
        }

        final String scope;
        private final Instrumentable owner;

        public TracerHolder( Instrumentable owner, String scope ) {
            this.scope = requireNonNull( scope, "scope cannot be null");
            this.owner = requireNonNull(owner, "owner cannot be null");
        }

        public Tracer object() {
            return owner.otel().getTracer(scope);
        }

        public SB spanBuilder(String spanName ) {
            return new SB( object().spanBuilder( requireNonNull(spanName, "spanName cannot be null")))
                            .setSpanKind( SpanKind.INTERNAL );
        }

    }

    final class MeterHolder {
        final String scope;
        private final Instrumentable owner;

        public MeterHolder(Instrumentable owner, String scope ) {
            this.scope = requireNonNull( scope, "scope cannot be null");
            this.owner = requireNonNull(owner, "owner cannot be null");
        }

        public Meter object() {
            return owner.otel().getMeter(scope);
        }

        public LongCounterBuilder countBuilder(String counterName ) {
            return object().counterBuilder( requireNonNull(counterName, "counterName cannot be null"));
        }

        public DoubleGaugeBuilder gaugeBuilder(String gaugeName ) {
            return object().gaugeBuilder( requireNonNull(gaugeName, "gaugeName cannot be null"));
        }

        public DoubleHistogramBuilder histogramBuilder(String histogramName ) {
            return object().histogramBuilder( requireNonNull(histogramName, "histogramName cannot be null"));
        }

        public LongUpDownCounterBuilder upDownCounterBuilder(String upDownCounterName ) {
            return object().upDownCounterBuilder( requireNonNull(upDownCounterName, "upDownCounterName cannot be null"));
        }

    }

    default TracerHolder tracer(String scope ) {
        return new TracerHolder( this, scope );
    }

    default MeterHolder meter( String scope ) {
        return new MeterHolder( this, scope );
    }

}
