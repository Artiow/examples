package artiow.examples.kafka.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderClassName = "Builder")
public class DemoData {

    private UUID uuid;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
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
