# presto-event-listener-plugin

This can be configured to send the presto query create, split computed and the query complete events to different locations.
Currently supported locations are :
1. local disk 
2. kafka 

These events can be used to further analysed to derive insights about the below things and much more : 
1. Kind of queries getting executed in the systems.
2. Query execution time
3. Can be used to infer about the scalability requirement of the presto and further be used to auto up-scale and down-scale presto clusters.
4. Auditing
5. Query Analytics like most frequent used tables & columns, most joined tables, etc.  


# Package
```shell
mvn package

ls target/
...
presto-event-listener-plugin-1.0.0-SNAPSHOT-shaded.jar
```

# Deploy
```shell
# installed on the coordinator
/opt/presto-server/plugin/event-listener/presto-event-listener-plugin-1.0.0-SNAPSHOT-shaded.jar

# create config file
$ vim /opt/presto-server/etc/event-listener.properties
event-listener.name=event-listener

mapper=default
writer=kafka

kafka.complete.topic=presto_complete
kafka.complete.bootstrap.servers=localhost:9092
kafka.complete.max.block.ms=3000  # fail fast if kafka server down.
kafka.complete.acks=all
kafka.complete.retries=2
kafka.complete.batch.size=5
kafka.complete.key.serializer=org.apache.kafka.common.serialization.StringSerializer
kafka.complete.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

# Test
```shell
# kafka 2.4.1
https://kafka.apache.org/24/documentation.html#quickstart

bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties

bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic presto_complete

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic presto_complete --from-beginning
```
