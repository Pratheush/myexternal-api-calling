### Kafka is Open Source Distributed Event Streaming Platform

### Kafka is designed to handle data that is constantly being generated and needs to be processed as it comes in, without delays.

### Through Kafka we can make our system Fault-Tolerant, Scalable, Implement Asynchronous Communication Between Services Or Distributed Services.

## Basic Teminologies:
### 1. Kafka Cluster : Group of kafka Brokers like kafka-broker-1,kafka-broker-2 and so on . Kafka Broker is the server where kafka is running.

### 2. Kafka Producer : kafka producer writes data into the cluster.
### 3. Kafka Consumer : kafka consumer consumes data from the cluster which is sent by Kafka producer.

### 4. Zookeeper : zookeeper keeps track or monitor Kafka clusters health.

### 5. Kafka-Connect: when we have to collect data from external entity to kafka-cluster then we use kafka-connect like we want to collect data from database or any file to kafka-cluster then we use kafka-connect without writing code we can collect data from database to kafka-cluster. This is what we call declarative integration where we take or collect data from one server to the other server without writing code. from Source data goes into kafka-cluster and for data fetching data will go to sink from kafka-cluster. using kafka-connect data movement in and out of kakfa-cluster can be achieved.

### 6. Kafka-Stream : from kafka-cluster we collect data and we do data transformation and then send back the transformed data into kafka-cluster.

------------

### Kafka-Topic :
Kafka-Topic : Named container for similar events. Unique identifier of a topic is its name.
Example: Student topic will have student related data, Food Topic will have food related data they are like tables in a database.
They live inside a broker.
Producer produce a message into the topic ( ultimately to partitions in round robin fashion) or directly to the partitions. Consumer poll continuously for new messages using the topic name.
Partition - A topic is partitioned and distributed to Kafka brokers in round robin fashion to achieve distributed system.
Replication Factor: A partition is replicated by this factor and it is replicated in another broker to prevent fault tolerance.


------

### PARTITIONS :
A topic is split into several parts which are known as the partitions of the Topic.
Partitions is where actually the message is located inside the topic.
Therefore, while creating a topic, we need to specify the number of partitions(the number is arbitrary and can be changed later).
Each partition is an ordered, immutable sequence of records.
Each partition is an independent of each other.
Each message gets stored into partitions with an incremental id known as its Offset value.
Ordering is there only at partition level.(so if data is to be stored in order then do it on same partition)
Partition continuously grows(Offset increases) as new records are produced.
All the records exist in distributed log file.


-------------

we can send data or message to topic inside partition with key and without key. when data is send without key then data is not received or fetched in order.when we send data to the partition without key then data is send according to the round robin fashion.
Ordering is done at partition level.

When data is send with key into the partition then partitioner comes into picture . partitioner does hashing if key is found with the data. hashing Is applied with the data to find out which partition data will be send. if the key is same then data will be send to the same partition.

we can send the data with key or without key. key Is optional.

When Sending messages with key, ordering will be maintained as they will be in the same partition.

Without key we can not guarantee the ordering of message as consumer poll the messages from all the partitions at the same time.

```
kafka-topics.bat --create --topic fruits --bootstrap-server localhost:9092 --replication-factor 1 --partitions 4

kafka-console-producer.bat --broker-list localhost:9092 --topic fruits --property "key.separator=-" --property "parse.key=true"

kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic fruits --from-beginning --property "key.separator=-" --property "print.key=false"
```

Consumer OFFSET
Consumer GROUPS


### Consumer OFFSET : Position of a consumer in a specific partition of a topic.
It represents the latest message consumer has read.

### When a consumer group reads messages from a topic, each member of the group maintains its own offset and updates it as it consumes messages.

### What is __Consumer_Offset : By default __Consumer_Offset named topic is created where it
__consumer_offset is a built-in topic in Apache Kafka that keeps track of the latest offset commited for each partition of each consumer group.

The Topic is internal to the Kafka cluster and not meant to be read or written to directly by clients. Instead, the offset information is stored in the topic and updated by the Kafka broker to reflect the position of each consumer in each partition.

The Information in __consumer_offset is used by Kafka to maintain the reliability of the consumer groups and to ensure that messages are not lost or duplicated.

There is a separate __consumer_offsets topic created for each consumer group. So if you have 2 consumer groups containing 4 consumers each, you will have a total of 2 __consumer_offsets topics created.

The __consumer_offsets topic is used to store the current offset of each consumer in each partition for a given consumer group. Each consumer in the group updates its own offset for the partitions it is assigned in the __consumer_offsets topic,
and the group coordinator uses this information to manage the assignment of partitions to consumers and to ensure that each partition is being consumed by exactly one consumer in the group.

When a Consumer joins a consumer group, it sends a join request to the group coordinator.
The Group coordinator determines which partitions the consumer should be assigned based on the number of consumers in the group and the current assignment of partitions to consumers.

The group coordinator then sends a new assignment of partitions to the consumer, which includes the set of partitions that the consumer is responsible for consuming.

The consumer starts consuming data from the assigned partitions.

It is important to note that consumers in a consumer group are always assigned partitions in a "sticky" fashion, meaning that a consumer will continue to be assigned the same partitions as long as it remains in the group. This allows consumers to maintain their position in the topic and continue processing where they left off, even after a rebalance.


Consumer OFFSET
Consumer GROUP
DEMONSTRATION

```
# this command will show us the list of the topics
kafka-topics.bat --bootstrap-server=localhost:9092 --list

# this command will show us consumer groups list
kafka-consumer-groups.bat --bootstrap-server localhost:9092 --list

# gives us console to produce message into the console to the kafka cluster to partitions of my-topic
kafka-console-producer.bat --broker-list localhost:9092 --topic my-topic


# this command will let us join the specific consumer group named console-consumer-2682
kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic my-topic --group console-consumer-2682 # console-consumer-2682 this consumer was earlier created by group coordinator itself when we run the kafka-console-consumer.bat command so now here we are assigning one consumer group to topic by us when we are running kafka-console-consumer.bat command by running --group attribute with the command.


# this command will show us partitions of the topic
kafka-topics.bat --describe --topic my-topic --bootstrap-server localhost:9092


# for older version of kafka
kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 3 --partitions 3 --topic my-gadgets

```

to create 3 kafka broker to create a cluster with 1 topic and 3 partitions and 3 replication-factor 3
copy server.properties file and make a copy of 3 server.properties like server1.properties, server2.properties, server3.properties
inside server.properties file change broker-id as 1,2,3 for each kafka-broker
and change ports as 9092,9093,9094 for each kafka brokers
and change logs which is saved in tmp/logs folder inside there define kafka-broker-logs1, kafka-broker-logs2,kafka-broker-logs3

```
# FOR NEW VERSION OF KAFKA use --boostrap-server instead of --zookeeper
kafka-topics.bat --create --topic my-gadgets --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --replication-factor 3 --partitions 3

kafka-console-producer.bat --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --topic my-gadgets

kafka-console-consumer.bat --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --topic my-gadgets --from-beginning


kafka-topics.bat --describe --topic my-gadgets --bootstrap-server localhost:9092,localhost:9093,localhost:9094

```

ISR : InSync-Replica


kafka's replication-factor gives us fault-tolerance


SEGMENTS
COMMIT LOG
RETENTION POLICY















