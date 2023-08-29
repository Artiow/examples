package artiow.examples.kafka;

import com.github.dockerjava.api.command.InspectContainerResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.NonNull;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.RemoteDockerImage;
import org.testcontainers.utility.DockerImageName;

public class InspectedContainer<SELF extends InspectedContainer<SELF>> extends GenericContainer<SELF> {

    private final List<Consumer<ContainerState>> createdInspections = new ArrayList<>();
    private final List<BiConsumer<ContainerState, InspectContainerResponse>> startingInspections = new ArrayList<>();
    private final List<BiConsumer<ContainerState, InspectContainerResponse>> startedInspections = new ArrayList<>();
    private final List<BiConsumer<ContainerState, InspectContainerResponse>> stoppingInspections = new ArrayList<>();
    private final List<BiConsumer<ContainerState, InspectContainerResponse>> stoppedInspections = new ArrayList<>();


    public InspectedContainer(@NonNull DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public InspectedContainer(@NonNull RemoteDockerImage image) {
        super(image);
    }

    public InspectedContainer(@NonNull String dockerImageName) {
        super(dockerImageName);
    }

    public InspectedContainer(@NonNull Future<String> image) {
        super(image);
    }


    @Override
    protected void containerIsCreated(String containerId) {
        super.containerIsCreated(containerId);
        createdInspections.forEach(c -> c.accept(self()));
    }

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo) {
        super.containerIsStarting(containerInfo);
        startingInspections.forEach(c -> c.accept(self(), containerInfo));
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        super.containerIsStarted(containerInfo);
        startedInspections.forEach(c -> c.accept(self(), containerInfo));
    }

    @Override
    protected void containerIsStopping(InspectContainerResponse containerInfo) {
        super.containerIsStarted(containerInfo);
        stoppingInspections.forEach(c -> c.accept(self(), containerInfo));
    }

    @Override
    protected void containerIsStopped(InspectContainerResponse containerInfo) {
        super.containerIsStarted(containerInfo);
        stoppedInspections.forEach(c -> c.accept(self(), containerInfo));
    }


    public SELF withCreatedInspection(Consumer<ContainerState> createdInspection) {
        createdInspections.add(createdInspection);
        return self();
    }

    public SELF withStartingInspection(BiConsumer<ContainerState, InspectContainerResponse> startingInspection) {
        startingInspections.add(startingInspection);
        return self();
    }

    public SELF withStartedInspection(BiConsumer<ContainerState, InspectContainerResponse> startedInspection) {
        startedInspections.add(startedInspection);
        return self();
    }

    public SELF withStoppingInspection(BiConsumer<ContainerState, InspectContainerResponse> stoppingInspection) {
        stoppingInspections.add(stoppingInspection);
        return self();
    }

    public SELF withStoppedInspection(BiConsumer<ContainerState, InspectContainerResponse> stoppedInspection) {
        stoppedInspections.add(stoppedInspection);
        return self();
    }
}
