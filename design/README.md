# Analytic like system design

## system workload

* billions of events written per day
* Millions users(merchants) query for analytic results
* processing time allowance: 1 hour

## additional requirements

* minimal downtime
* need of reprocessing historical data

## workload analysis

### input
* 10 billions events means 120k messages per second.
* assume on average, event is around 0.5KB in size, the system need to process 60MB of data per second
* which is around 5TB a day. 
* actually storage could be much less, since data would be compressed while storing in file system.

### output - read from users

* there will be millions of users who want to get semi-realtime update about analytical result from the system.
* each query could put heavy work load to the system, as it might need to read and aggregate from historical data long back.
* however each user will only care about their own data. (except few internal admin users(system) who would care about overall metrics)

### other requirements

* to achieve minimal downtime, our system need to have hot/active backup instead of cold one.
* it's necessary to reprocess historical data, so we'll need to store the events for future use. (up to certain time frame, depends on analytic system needs)
* 1 year of historical data is around 2PB. however as mentioned above, if compressed, it should be much smaller.(when using Kafka, data will batched together and compressed, so we could achieve better storage performances)

## design

* since the query workload is less predictable compare to event writes. it's necessary to separate the write route and read route of system. so we can scale the read route independently as needed.
* to separate the two, we'll use Kafka cluster as our event storage, all write and read from the cluster will be sequential to get maximal performances. (60MB/second is moderate volume compare to what Kafka can handle, so we should be able to handle peak volume quite easy too)
* historical data storage could be rational database or Hadoop file storage. how to balance between the live and historical data storage will depends how analytical data being used in downstream.
* to ensure the performance requirement for queries(1 hour delay), each(or group of) analytic workers will only serve limited number of users, and they'll only read(and optionally keep a copy of) events as needed from the Kafka cluster. 
* workers can be deployed independently, when started, they'll read from cluster for past message to reprocess. so the need for reprocessing of historical data in case of bug in worker logic can be fulfilled too.
* query load balancer will route user's query to target group of workers. 
* etcd/zookeeper or other distributed config storage can be used to configure the worker and load balance(know who is serving who).
* overall design would be like below:

![system architecture](https://github.com/gigiyy/SoftwareEngineerChallenge/blob/master/design/design.png)

