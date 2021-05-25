package technology.something.spring.kafka.tools;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import technology.something.spring.kafka.KafkaMessageInfo;
import technology.something.spring.kafka.KafkaRequestData;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class RecordMapperTest {

    private Schema schema;

    @BeforeEach
    void setUp() {
        this.schema = ReflectData.get().getSchema(ExampleObject.class);
    }

    @Test
    void shouldMapRecordToGivenObject() {
        GenericRecord record = new GenericData.Record(this.schema);
        record.put("value", "Super nice rating.");
        record.put("rating", 6.3);

        ExampleObject mappedObject = RecordMapper.mapRecordToObject(record, new ExampleObject());

        Assert.assertEquals(mappedObject.getValue(), "Super nice rating.");
        Assert.assertTrue(mappedObject.getRating() == 6.3);
    }

    @Test
    void shouldThrowErrorWhileMappingRecordToGivenObject() {
        GenericRecord record = new GenericData.Record(this.schema);
        record.put("value", "Super nice rating.");
        record.put("rating", "text");

        Assert.assertThrows(
            TypeMismatchException.class,
            () -> RecordMapper.mapRecordToObject(record, new ExampleObject()));
    }

    @Test
    void shouldMapAMessagetToGenericRecord() {
        KafkaRequestData data = mock(KafkaRequestData.class);
        ConsumerRecord consumerRecord = mock(ConsumerRecord.class);
        when(data.getConsumerRecord()).thenReturn(consumerRecord);

        GenericRecord record = this.createRecord();
        when(consumerRecord.value()).thenReturn(record);

        Message<?> message = MessageBuilder
            .withPayload(data)
            .setHeader(KafkaMessageInfo.HEADER_TYPE, ExampleRecord.class.getName())
            .build();

        GenericRecord mappedRecord = RecordMapper.mapMessagetToGenericRecord(message);
        Assert.assertEquals(GenericData.Record.class, mappedRecord.getClass());
    }

    private GenericRecord createRecord() {
        GenericRecord record = new GenericData.Record(this.schema);
        record.put("value", "Super nice rating.");
        record.put("rating", 6.3);

        return record;
    }


    public static class ExampleRecord implements SpecificRecord {

        private ExampleObject exampleObject;

        public ExampleObject getExampleObject() {
            return exampleObject;
        }

        public void setExampleObject(ExampleObject exampleObject) {
            this.exampleObject = exampleObject;
        }

        @Override
        public void put(int i, Object v) {

        }

        @Override
        public Object get(int i) {
            return null;
        }

        @Override
        public Schema getSchema() {
            return ReflectData.get().getSchema(ExampleObject.class);
        }
    }

    public static class ExampleObject {
        private String value;

        private double rating;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }
    }
}
