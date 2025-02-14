package by.itacademy.ws.productMicroservice.configuration;

import lombok.*;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class KafkaProducerProperties {
    private String bootstrapServers;
    private String keySerializer;
    private String valueSerializer;
    private String acks;
    private Integer retries;

    private Properties properties;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Properties {
        private Delivery delivery;
        private Linger linger;
        private Request request;
        private Retry retry;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Delivery {
        private Integer timeoutMs;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Linger {
        private Integer ms;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Integer timeoutMs;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Retry {
        private Backoff backoff;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Backoff {
        private Integer ms;
    }
}
