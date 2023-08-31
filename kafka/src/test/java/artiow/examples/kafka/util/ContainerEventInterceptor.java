package artiow.examples.kafka.util;

import com.github.dockerjava.api.command.InspectContainerResponse;
import java.util.function.BiConsumer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.testcontainers.containers.ContainerState;

@FunctionalInterface
public interface ContainerEventInterceptor extends BiConsumer<ContainerState, InspectContainerResponse> {

    @Override
    void accept(@NonNull ContainerState containerState, @Nullable InspectContainerResponse containerInfo);
}
