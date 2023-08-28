package artiow.examples.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder(builderClassName = "Builder")
public class DemoData {

    private UUID uuid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSS")
    private LocalDateTime datetime;


    public static DemoData generate() {
        final var now = LocalDateTime.now(ZoneOffset.UTC);
        final var uuid = UUID.randomUUID();
        return DemoData
            .builder()
            .uuid(uuid)
            .datetime(now)
            .build();
    }


    @SuppressWarnings("unused")
    public String getView() {
        return toString();
    }
}
