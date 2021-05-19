package technology.something.spring.kafka.services.impl;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificRecord;

public class ExampleResponseMessage implements SpecificRecord {

    public static final String TOPIC = "EXAMPLE_RESPONSE";

    private  Double longitude;

    private Double latitude;

    @Override
    public Schema getSchema() {
        return ReflectData.get().getSchema(ExampleResponseMessage.class);
    }

    @Override
    public Object get(int field) {
        return null;
    }

    @Override
    public void put(int field, Object value) {

    }
}
