package artiow.examples.ibmmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class IbmMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(IbmMqApplication.class, args);
    }
}
