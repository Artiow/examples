package artiow.examples.ibmmq;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.springframework.messaging.Message;

@UtilityClass
public class MessageTestUtils {

    public static Set<UUID> generate(int limit, Consumer<UUID> action) {
        return Stream.generate(UUID::randomUUID).limit(limit).peek(action).collect(Collectors.toSet());
    }

    public static boolean match(Message<UUID> msg, Set<UUID> setToTest) {
        return Optional.ofNullable(msg).map(Message::getPayload).filter(setToTest::contains).isPresent();
    }
}
