# RequestLog

[中文文档](README-zh.md)

## Introduction

**RequestLog** is an HTTP request logging and retry compensation tool based on Spring Boot.
<br/>
**Features**: Low intrusion and easy integration.

---

## Quick Start

Taking **RestTemplate** as an example.

#### Maven Dependency
```xml
<dependency>
    <groupId>io.github.requestlog</groupId>
    <artifactId>request-log-resttemplate-starter</artifactId>
    <version>${latest_stable_version}</version>
</dependency>
```

---

### Enhance RestTemplate

Enhance the RestTemplate client using annotations.
```java
@RequestLogEnhanced 
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

---

#### Define Repository for Persistence

```java
@Component
public class MyRequestLogRepository implements IRequestLogRepository {
    @Override
    public void saveRequestLog(RequestLog requestLog) {
        // save request log
    }
    @Override
    public void saveRequestLogAndRetryJob(RequestLog requestLog, RequestRetryJob requestRetryJob) {
        // save request log and retry job
    }
}
```

---

#### Wrap Request Code

```java
// Original request code
String result = restTemplate.getForObject("url", String.class);

// Wrapped request code
String wrappedResult = LogContext.log().execute(() -> {
    return restTemplate.getForObject("url", String.class);
});
```

For the wrapped request, when an exception occurs or the response status code is not `2xx`, the corresponding save method of the custom `IRequestLogRepository` will be invoked.
