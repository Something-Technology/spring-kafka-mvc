package technology.something.spring.kafka;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class KafkaAvroSerializer<T extends SpecificRecordBase> implements Serializer<T> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAvroSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // do nothing
    }

    @Override
    public byte[] serialize(String topic, T payload) {
        byte[] bytes = null;
        try {
            if (payload != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                BinaryEncoder binaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null);
                DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(payload.getSchema());
                datumWriter.write(payload, binaryEncoder);
                binaryEncoder.flush();
                byteArrayOutputStream.close();
                bytes = byteArrayOutputStream.toByteArray();
                LOG.info("serialized payload='{}'", DatatypeConverter.printHexBinary(bytes));
            }
        } catch (Exception e) {
            LOG.error("Unable to serialize payload ", e);
        }
        return bytes;
    }

    @Override
    public void close() {
        // do nothing
    }
}
