/*
This is a simple test case to verify that it is possible to send events to a Kafka topic.
To do this, we launch a single node Kafka server and create a topic called
‘quickstart-events’ using the bin/kafka-topics.sh command.
For default, we then specify the bootstrap server address as
‘localhost:9092’ in the configuration file for the server.
If the server is not running on localhost,
we must update the server.properties file to specify the correct host or IP address.
Additionally, we specify the type of listener as
‘PLAINTEXT://your.host.name:9092’ if it is not running on localhost.
Otherwise, it will raise a timeout exception
because the server is not listening on the correct socket.
*/
package org.presto.plugin.events.writer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class KafkaWriter {
    private Producer<String, String> producer;
    private static final String TOPIC = "quickstart-events";

    public KafkaWriter() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(props);
    }

    public void write(String content) {
        System.out.println(content);
        try {
            RecordMetadata recordMetadata = producer.send(new ProducerRecord<String,String>(TOPIC, content)).get();
            System.out.println(recordMetadata.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }

    public static void main(String[] args) {
        KafkaWriter kw = new KafkaWriter();
        kw.write("Hello, World!");
    }
}

