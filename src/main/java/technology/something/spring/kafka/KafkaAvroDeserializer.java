package technology.something.spring.kafka;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.something.spring.kafka.services.impl.DefaultKafkaTopicProvider;

import java.util.Map;

public class KafkaAvroDeserializer<T> implements Deserializer {

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
            Class<? extends SpecificRecord> record = topicProvider.fetchRecordForTopic(topic);
            Schema schema = record.newInstance().getSchema();

            if (bytes != null) {
                DatumReader datumReader = new GenericDatumReader<GenericRecord>(schema);
                Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
                GenericRecord genericRecord = (GenericRecord) datumReader.read(null, decoder);
                returnObject = (T) genericRecord;
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
