# Message Handler

A simple library to send and receive messages in AWS.
Built on the `spring-cloud-aws-messaging` library by `io.awspring.cloud` in `2.4.X` version,
with a new implementation of the message handler.

## Project goal

The goal was to provide a solution that reduces the use of `spring-cloud-aws-messaging` library.
The auto-configuration, SqsListener and messaging template were used only.
A new message resolver / converter and message dispatcher implementation was proposed.
Therefore, it doesn't rely on the library's resolver interface or annotations.

## Use

### Requirements

- Java 17
- Spring Boot 2.7.X

### Configuration

Configuration consist of adding the dependency,
annotating a configuration class, and specifying the resources.

#### Dependency

```xml
<dependency>
    <groupId>au.michalwojcik</groupId>
    <artifactId>message-handler</artifactId>
</dependency>
```

#### Annotation

```java
@EnableMessaging(
        receiver = {ReceiveStrategy.NOTIFICATION, ReceiveStrategy.S3},
        sender = {SendStrategy.NOTIFICATION}
)
```

#### Properties
```yaml
notification:
  receiver:
    queue:
      name: queue-name
  sender:
    topic:
      name: topic-name
```

### Sender

### Receiver

#### Event

#### S3 Notification
