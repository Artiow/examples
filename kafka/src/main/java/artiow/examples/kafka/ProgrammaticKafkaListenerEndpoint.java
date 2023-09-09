package artiow.examples.kafka;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionOffset;
import org.springframework.kafka.support.converter.MessageConverter;

@Getter
@Setter
@Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProgrammaticKafkaListenerEndpoint<K, V> implements KafkaListenerEndpoint {

    private String id;
    private String groupId;
    private String group;
    private Collection<String> topics;
    private TopicPartitionOffset[] topicPartitionsToAssign;
    private Pattern topicPattern;
    private String clientIdPrefix;
    private Integer concurrency;
    private Boolean autoStartup;
    private boolean splitIterables;

    @NonNull
    private MessageListener<K, V> messageListener;


    public ProgrammaticKafkaListenerEndpoint(
        @NonNull String id,
        @NonNull MessageListener<K, V> messageListener,
        @NonNull String... topics) {
        setId(id);
        setTopics(new HashSet<>(Arrays.asList(topics)));
        setMessageListener(messageListener);
    }

    public ProgrammaticKafkaListenerEndpoint(
        @NonNull String id,
        @NonNull MessageListener<K, V> messageListener,
        @NonNull TopicPartitionOffset... topicsAndPartitions) {
        setId(id);
        setTopicPartitionsToAssign(Arrays.copyOf(topicsAndPartitions, topicsAndPartitions.length));
        setMessageListener(messageListener);
    }

    public ProgrammaticKafkaListenerEndpoint(
        @NonNull String id,
        @NonNull MessageListener<K, V> messageListener,
        @NonNull Pattern topicPattern) {
        setId(id);
        setTopicPattern(topicPattern);
        setMessageListener(messageListener);
    }


    @Override
    public void setupListenerContainer(
        MessageListenerContainer listenerContainer,
        MessageConverter messageConverter) {
        listenerContainer.setupMessageListener(Objects.requireNonNull(
            getMessageListener(),
            () -> "Instance of " + MessageListener.class.getName() + " must be provided on setup"));
    }


    @SuppressWarnings("unused")
    public static class Builder<K, V> {

        public Builder<K, V> topics(@NonNull String... topics) {
            this.topics = new HashSet<>(Arrays.asList(topics));
            return this;
        }

        public Builder<K, V> topicPartitionsToAssign(@NonNull TopicPartitionOffset... topicsAndPartitions) {
            this.topicPartitionsToAssign = Arrays.copyOf(topicsAndPartitions, topicsAndPartitions.length);
            return this;
        }

        public Builder<K, V> topicPattern(@NonNull Pattern topicPattern) {
            this.topicPattern = topicPattern;
            return this;
        }
    }
}
