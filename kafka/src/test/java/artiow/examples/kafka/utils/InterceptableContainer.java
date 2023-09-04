package artiow.examples.kafka.utils;

import com.github.dockerjava.api.command.InspectContainerResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.Future;
import lombok.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.utility.DockerImageName;

public class InterceptableContainer<SELF extends InterceptableContainer<SELF>> extends GenericContainer<SELF> {

    private final MultiValueMap<ContainerEvent, ContainerEventInterceptor> hooks = new MultiValueMapAdapter<>(new HashMap<>());


    public InterceptableContainer(@NonNull DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public InterceptableContainer(@NonNull RemoteDockerImage image) {
        super(image);
    }

    public InterceptableContainer(@NonNull String dockerImageName) {
        super(dockerImageName);
    }

    public InterceptableContainer(@NonNull Future<String> image) {
        super(image);
    }


    @Override
    protected void containerIsCreated(String containerId) {
        super.containerIsCreated(containerId);
        intercept(ContainerEvent.CREATED, null);
    }

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo) {
        super.containerIsStarting(containerInfo);
        intercept(ContainerEvent.STARTING, containerInfo);
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        super.containerIsStarted(containerInfo);
        intercept(ContainerEvent.STARTED, containerInfo);
    }

    @Override
    protected void containerIsStopping(InspectContainerResponse containerInfo) {
        super.containerIsStopping(containerInfo);
        intercept(ContainerEvent.STOPPING, containerInfo);
    }

    @Override
    protected void containerIsStopped(InspectContainerResponse containerInfo) {
        super.containerIsStopped(containerInfo);
        intercept(ContainerEvent.STOPPED, containerInfo);
    }

    private void intercept(ContainerEvent event, InspectContainerResponse containerInfo) {
        // noinspection resource
        final ContainerState containerState = self();
        hooks
            .getOrDefault(event, Collections.emptyList())
            .forEach(interceptor -> interceptor.accept(containerState, containerInfo));
    }


    public SELF withInterceptorOn(ContainerEvent event, ContainerEventInterceptor interceptor) {
        hooks.add(event, interceptor);
        return self();
    }
}
