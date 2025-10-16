# CSCI318-Microservice-System-for-Metal-System

### Four Microservices in Metal Manufacturing System
- **Business MS** 
- **Inventory MS** 
- **Maintenance MS** 
- **Workforce MS** 

### Business MS
## Apache Kafka Setup
This Spring Boot project uses Apache Kafka as a messaging platform.
To run this project, you need to set up Kafka first.

#### Linux and MacOS
Make sure you download kafka version 3.9.- or below, as v4 does not have zookeeper.
Download a **binary package** of Apache Kafka (e.g., `kafka_2.13-3.7.0.tgz`) from
[https://kafka.apache.org/downloads](https://kafka.apache.org/downloads)
and unzip it.
In the Terminal, `cd` to the unzip folder, and start Kafka with the following commands (each in a separate Terminal session):
```bash
./bin/zookeeper-server-start.sh ./config/zookeeper.properties
```
```bash
./bin/kafka-server-start.sh ./config/server.properties
```

#### Windows
Make sure you download kafka version 3.9.- or below, as v4 does not have zookeeper.
Download a **binary package** of Apache Kafka (e.g., `kafka_2.13-3.7.0.tgz`) from
[https://kafka.apache.org/downloads](https://kafka.apache.org/downloads)
and unzip it to a directory, e.g., `C:\kafka`&mdash;Windows does not like a complex path name (!).

Use the following two commands in the Windows CMD (one in each window) to start Kafka:
```bash
C:\kafka\bin\windows\zookeeper-server-start.bat C:\kafka\config\zookeeper.properties
```
```bash
C:\kafka\bin\windows\kafka-server-start.bat C:\kafka\config\server.properties
```

## Run The Application
### Stream Processing and Interactive Query
After starting Kafka, run `business-service`'s main class, this main class will create 4 machines and 
will continuously assign random jobs to random machines.

The following REST API is provided to query the results:
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/queries/windowedMachinesByAmount
```

### Adding Jobs and Machine scheduling
#### Windows CMD

Schedule a machine:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"machineId\":\"machine5\",\"employeeName\":\"John Smith\"}" http://localhost:8787/machinescheduling
```
Get all scheduledIds of all machines:
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/machinescheduling/findAllMachineIds
```
Find a machine from a machineId - also shows all allocated jobs (replace `<<machineId>>` with the returned book key):
```shell
set machineId=<<machineId>>
```
```shell
curl "http://localhost:8787/machinescheduling/findMachine?machineId=%machineId%"
```
Add a new job to a machine:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"jobNumber\":1000,\"jobTimeNeededDays\":5,\"priority\":1,\"machineId\":\"machine1\",\"materialNeeded\":\"wood\",\"materialAmount\":23}" http://localhost:8787/addJobToMachine
```
Find all the jobs of a specific machine via machineId
```shell
set machineId=<<machineId>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findJobsByMachineId?machineId=%machineId%"
```
Find the first scheduled job of a specific machine via machineId
```shell
set machineId=<<machineId>>
```
```shell
curl "http://localhost:8787/addJobToMachine/findCurrentJobByMachineId?machineId=%machineId%"
```
Loaded onto carrier:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"CNHKG\",\"handlingType\":\"LOAD\",\"completionTime\":\"2019-08-25\",\"voyageNumber\":\"0100S\"}" http://localhost:8786/cargohandling
```
Unloaded:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"CNHGH\",\"handlingType\":\"UNLOAD\",\"completionTime\":\"2019-08-28\",\"voyageNumber\":\"0100S\"}" http://localhost:8786/cargohandling
```
Loaded onto next carrier:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"CNHGH\",\"handlingType\":\"LOAD\",\"completionTime\":\"2019-09-01\",\"voyageNumber\":\"0101S\"}" http://localhost:8786/cargohandling
```
Unloaded:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"JNTKO\",\"handlingType\":\"UNLOAD\",\"completionTime\":\"2019-09-10\",\"voyageNumber\":\"0101S\"}" http://localhost:8786/cargohandling
```
Loaded onto next carrier:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"JNTKO\",\"handlingType\":\"LOAD\",\"completionTime\":\"2019-09-15\",\"voyageNumber\":\"0102S\"}" http://localhost:8786/cargohandling
```
Unloaded:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"USNYC\",\"handlingType\":\"UNLOAD\",\"completionTime\":\"2019-09-25\",\"voyageNumber\":\"0102S\"}" http://localhost:8786/cargohandling
```
Customs:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"USNYC\",\"handlingType\":\"CUSTOMS\",\"completionTime\":\"2019-09-26\",\"voyageNumber\":\"\"}" http://localhost:8786/cargohandling
```
Claimed:
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingId\":\"%bookingId%\",\"unLocode\":\"USNYC\",\"handlingType\":\"CLAIM\",\"completionTime\":\"2019-09-28\",\"voyageNumber\":\"\"}" http://localhost:8786/cargohandling
```
<!--
(windows)
```shell
curl -X POST -H "Content-Type:application/json" -d "{\"bookingAmount\":20,\"originLocation\":\"HK\",\"destLocation\":\"NY\",\"destArrivalDeadline\":\"2010-08-01\"}" http://localhost:8787/cargobooking
```
```shell
curl -X GET -H "Content-Type:application/json" http://localhost:8787/cargobooking/findAllBookingIds
```
-->


### View Booking Event Stream
After running the `business-service`'s main class, check the Kafka topics with the following command:

(Linux/MacOS)
```shell
./bin/kafka-topics.sh --bootstrap-server=localhost:9092 --list
```
(Windows)
```shell
C:\kafka\bin\windows\kafka-topics.bat --bootstrap-server=localhost:9092 --list
```
You should see three topics. You can read data in the `cargobookings` topic:

(Linux/MacOS)
```shell
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic cargobookings --from-beginning
```
(Windows)
```shell
c:\kafka\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic jobAddedToMachines --from-beginning
```


### Trouble Shooting
If you cannot start Kafka, try to clean up data in the Kafka topics to start over.
For this purpose, in Linux/MacOS, delete the folders `/tmp/zookeeper`, `/tmp/kafka-logs`
and `/tmp/kafka-streams` (if any). In Windows, delete the folders `C:\tmp\zookeeper`,
`C:\tmp\kafka-logs` and `C:\kafka\kafka-streams` (if any).

