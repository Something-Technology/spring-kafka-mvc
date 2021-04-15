package technology.something.spring.kafka.services.impl;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecord;

public class ExampleMessage implements SpecificRecord {

    private  Double longitude;

    private Double latitude;

    @Override
    public Schema getSchema() {
        return null;
    }

    @Override
    public Object get(int field) {
        return null;
    }

    @Override
    public void put(int field, Object value) {

    }
}
