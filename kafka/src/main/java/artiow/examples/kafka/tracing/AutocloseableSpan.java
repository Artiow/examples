package artiow.examples.kafka.tracing;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AutocloseableSpan implements AutoCloseable {

    private final Span delegate;
    private final Tracer.SpanInScope delegateInScope;

    @Override
    public void close() {
        delegateInScope.close();
        delegate.end();
    }
}
