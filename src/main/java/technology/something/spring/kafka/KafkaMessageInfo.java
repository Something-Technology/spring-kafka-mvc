package technology.something.spring.kafka;

/**
 * Kafka messages info holds the message type of a message.
 */
public class KafkaMessageInfo {

    public static final String HEADER_TYPE = "type";

    private String type;

    public KafkaMessageInfo(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
