package technology.something.spring.kafka;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.something.spring.kafka.services.impl.DefaultKafkaTopicProvider;

import java.util.Map;

public class KafkaAvroDeserializer<T extends SpecificRecordBase> implements Deserializer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAvroDeserializer.class);

    private DefaultKafkaTopicProvider topicProvider;

    public KafkaAvroDeserializer(DefaultKafkaTopicProvider topicProvider) {
        this.topicProvider = topicProvider;
    }

    @Override
    public void configure(Map configs, boolean isKey) {
        // do nothing
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        T returnObject = null;

        try {
            SpecificRecord record = topicProvider.fetchRecordForTopic(topic);
            Schema schema = record.getSchema();

            if (bytes != null) {
                DatumReader<SpecificRecord> datumReader = new ReflectDatumReader<>(schema);
                Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
                returnObject = (T) datumReader.read(null, decoder);
                LOG.info("deserialized data='{}'", returnObject.toString());
            }
        } catch (Exception e) {
            LOG.error("Unable to Deserialize bytes[] ", e);
        }

        return returnObject;
    }

    @Override
    public void close() {
        // do nothing
    }

}
