### Maven Depedencies For Redis Cache :::
```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
```
### in application.properties file :::
spring.redis.host=localhost
spring.redis.port=6379

### setting up redis in windows using wsl2
```
 PS C:\Users\prath> wsl -l -v
  NAME                   STATE           VERSION
* docker-desktop-data    Stopped         2
  docker-desktop         Stopped         2
  Ubuntu                 Stopped         2



To stop your running Ubuntu distribution on WSL
wsl --terminate Ubuntu

This command will terminate the Ubuntu distribution. If you want to stop all running WSL distributions, you can use:
wsl --shutdown

To start your Ubuntu distribution on WSL, you can use the following command in either Command Prompt or PowerShell:
wsl -d Ubuntu

pratheush@Lord-Shiva:/mnt/c/Users/prath$



sudo apt install redis

sudo service redis-server start



pratheush@Lord-Shiva:/mnt/c/Users/prath$ redis-cli
127.0.0.1:6379> ping
PONG
127.0.0.1:6379>



IN REDIS CLI ::::
127.0.0.1:6379> set salary 100000
OK
127.0.0.1:6379> get salary
"100000"
127.0.0.1:6379> set salary 10k
OK
127.0.0.1:6379> get email
"gmail@email.com"
```

### WSL UBUNTU :::
```
### TO DISABLE AUTO STARTUP OF SERVICES IN UBUNTU:::::::
 sudo systemctl disable redis-server
Synchronizing state of redis-server.service with SysV service script with /lib/systemd/systemd-sysv-install.
Executing: /lib/systemd/systemd-sysv-install disable redis-server
Removed /etc/systemd/system/multi-user.target.wants/redis-server.service.
Removed /etc/systemd/system/redis.service.



pratheush@Lord-Shiva:~$ sudo service redis-server restart
pratheush@Lord-Shiva:~$ sudo service redis-server status
● redis-server.service - Advanced key-value store
     Loaded: loaded (/lib/systemd/system/redis-server.service; disabled; vendor preset: enabled)
     Active: active (running) since Mon 2024-09-30 06:56:24 IST; 6s ago
       Docs: http://redis.io/documentation,
             man:redis-server(1)
   Main PID: 765 (redis-server)
     Status: "Ready to accept connections"
      Tasks: 5 (limit: 18959)
     Memory: 11.6M
     CGroup: /system.slice/redis-server.service
             └─765 "/usr/bin/redis-server 127.0.0.1:6379" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" "" >
Sep 30 06:56:24 Lord-Shiva systemd[1]: Starting Advanced key-value store...
Sep 30 06:56:24 Lord-Shiva systemd[1]: Started Advanced key-value store.


```

we have to set serializer and deserializer for redis while exchanging key value pair to store for redis cache to work properly

In Redis We can save any type of Data , redis service is generic

##### To clear data in Redis after using the SET and GET commands, you can use the DEL command to delete specific keys or the FLUSHALL command to clear all data from the database. 

### Delete Specific Keys:
* SET a key: SET mykey "value"
* GET the key: GET mykey
* Delete the key: DEL mykey

Example ::
```
redis-cli SET mykey "value"
redis-cli GET mykey
redis-cli DEL mykey

```

### Clear All Data:
FLUSHALL command to remove all keys from all databases

Example :::
```
redis-cli FLUSHALL

```

### Clear Data from a Specific Database
Use the FLUSHDB command to clear all keys from the currently selected database.
```
redis-cli FLUSHDB

```

### To list all the databases in Redis, you can use the INFO command along with the keyspace parameter. This command will provide information about all the databases, including the number of keys in each database.
```
redis-cli INFO keyspace

The output will look something like this:::
# Keyspace
db0:keys=2,expires=0,avg_ttl=0
db1:keys=3,expires=0,avg_ttl=0
db2:keys=1,expires=0,avg_ttl=0

```

### Retrieve all keys:
```
redis-cli KEYS *

```
### Get Values For Each Key :: 
```
for key in $(redis-cli KEYS *); do echo "$key: $(redis-cli GET $key)"; done

```
































































































