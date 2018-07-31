# BVTech assessment

author: Zoltan Domahidi  
date: 30th of July, 2018  


## Action-Monitor Application

### Build an application fulfilling the following requirements.

1. The app must be written in Java.  
Using the following libraries/ frameworks/ containers is optional but if used, it
will be appreciated.

2. The app should monitor the inserts in a database table and notify all the
browsers with a session established every time the contents of the table
change.  
- Maven
- Tomcat
- Spring (core, mvc, integration)
- SL4j + Logback (for logging purposes)
- A lightweight database
- WebSockets - Spring
- ActiveMQ
- EasyMock, Mockito or similar

![action-monitor](files/screenshot.jpg)

### Bonus Points
Expose two REST endpoints returning:
- OK - if the application is running
- Version of the application

### Submission
- Submit your source code along with A README file detailing how to build and
deploy the code or other detail you find relevant.
- You can use any web-based service for version control to host and share your
solution.

### Points to take into consideration
- Make sure we can easily build and run the app.
- Make it easy for us to exercise the system (interacting with the database you
choose inserting/ updating rows).
- We will be looking at the code, assessing style, implementation and test
coverage.

__Important Note__: Even if you are not able to complete the exercise, if you feel that
whatever you achieve will give us an idea on the way you think and code, submit your
effort anyway. We also take into consideration the decisions given the time constraints.


## Solution details and design considerations

> Note: Directories and commands are relative to the project's root path: `assessment-betvictor`

The solution contains the Action Monitor server application in the `application` folder 
and the Client application in the `client` folder.

The server application uses Redis as the database of choice. Redis is configured by the `/files/redis.conf` file. 
At this stage Redis is configured to publish events, but it is not configured to be persistent (data will be lost after Docker container restart)  
Server application uses interfaces, so it makes easy to extend the functionality with any other database.  
`EventNotificationService` prepares and triggers sending notifications. Event notifications are using `EventType`s 
(create, read, update, delete). This is an abstraction over a concrete database event.
I.e. in Redis there are many operations like `sadd`, `del`, `lrem` what will be mapped to this generic event types. 
Some events could be mapped for many event types in case of Redis, but at the moment we use only one event type per operation. 
This means that some event types are not straightforward in case of Redis, but it might be in case of Postgres. 
(For testing purposes I recommend using List data types. In every other case update event type might appear as create event type)

> Note: The list of Redis operations are not complete at the moment.

`NotificationSender` sends out the event payload using a messaging system. In our case we send a JSON payload over WebSocket.  
The server application is using Redis, what could run in Docker container. Run command `docker-compose -f files/docker-compose.yml up` 
to start Redis and Redis Commander containers. Redis Commander will run on http://localhost:/8081
The server application will run on http://localhost:9000 and has Actuator installed, what will show the application version on 
http://localhost:9000/actuator/info and the health status on http://localhost:/9000/actuator/health

> Note: Server application has an integration test what requires a running Redis instance

The client application connects to the server's websocket and reads the action events. The main endpoint is at 
http://localhost:8080/action-monitor
This endpoint polls the server status. This information could be reached separately at http://localhost:8080/notification-server-info


## Installation

The project has a `install-and-run.sh` bash script what will install and run the applications:
- build server application and create Docker container
- build client application and create Docker container
- start Docker containers (Redis, Redis Commander, Action Monitor and Client)

**The install script requires Docker Compose**

It is recommended to access the applications by using the host machines domain name.
This should be printed at the end of the install script, like this:

```
--> applications are up and running at
 
Redis Commander: http://workstation-domain:8081
Action Monitor: http://workstation-domain:9000/actuator/health
Client: http://workstation-domain:8080/action-monitor
```

If the install script would fail by any chance, the applications could be run easily by using Gradle Wrapper command in the application folder  
`./gradlew clean bootrun`  
In this case the application could be accessed on `localhost`
