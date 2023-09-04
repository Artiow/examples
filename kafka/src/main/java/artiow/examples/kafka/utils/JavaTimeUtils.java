package artiow.examples.kafka.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class JavaTimeUtils {

    public static LocalDateTime localDateTime(long timestamp, ZoneOffset offset) {
        final long epochSecond = timestamp / 1000;
        final int nanoOfSecond = ((int) (timestamp % 1000)) * 1_000_000;
        return LocalDateTime.ofEpochSecond(epochSecond, nanoOfSecond, offset);
    }
}
