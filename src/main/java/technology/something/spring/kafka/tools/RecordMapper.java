package technology.something.spring.kafka.tools;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.messaging.Message;
import technology.something.spring.kafka.KafkaRequestData;

public class RecordMapper {

    private static final Logger LOG = LoggerFactory.getLogger(RecordMapper.class);

    public static <T> T mapRecordToObject(GenericRecord record, T object) {
        if (record == null) {
            LOG.error("Record must not be null.", record);
            return null;
        }
        if (object == null) {
            LOG.error("Object must not be null.", object);
            return null;
        }

        final Schema schema = ReflectData.get().getSchema(object.getClass());

        if (schema.getFields().equals(record.getSchema().getFields())) {
            record
                .getSchema()
                .getFields()
                .forEach(field -> PropertyAccessorFactory
                    .forDirectFieldAccess(object)
                    .setPropertyValue(field.name(), record.get(field.name()) == null ?
                        record.get(field.name()) :
                        record.get(field.name()).toString()
                    )
                );
            return object;
        }

        return null;
    }

    public static final GenericRecord mapMessagetToGenericRecord(Message<?> message) {
        KafkaRequestData data = message.getPayload() != null ? (KafkaRequestData) message.getPayload() : null;
        GenericRecord record = data.getConsumerRecord() != null ? (GenericRecord) data.getConsumerRecord().value() : null;

        return record;
    }

}
