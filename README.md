# ThreadPoolTaskExecutor in Spring Boot

## 🚀 Overview
This project demonstrates the implementation of ThreadPoolTaskExecutor in a Spring Boot application. It provides RESTful APIs to trigger asynchronous execution of tasks. The project includes two services:
- Order Service: Handles order processing asynchronously.
- Forgot Password Service: Manages forgot password requests asynchronously.

---

## ThreadPoolTaskExecutor Configuration

`ThreadPoolTaskExecutor` is a class provided by Spring Framework to handle asynchronous tasks in Spring. It is a powerful asynchronous task execution mechanism that allows you to manage a pool of worker threads efficiently. The ThreadPoolTaskExecutor is configured with various properties to optimize performance:

1. **corePoolSize**
   - **Description**: The number of threads to keep in the pool, even if they are idle.
   - If the number of threads is less than the core, a new thread is created to handle the task.
   - If the number of threads is greater than the core, the task is added to the queue.

2. **maxPoolSize**
   - **Description**: The maximum number of threads to allow in the pool.
   - This limit prevents the thread pool from creating too many threads, which could overwhelm the system.
   - When tasks are submitted, the thread pool starts with corePoolSize threads.
   - If all corePoolSize threads are busy, new tasks are added to the queue.
   - If the queue is full, additional threads are created up to maxPoolSize.
   - Once maxPoolSize is reached, new tasks cannot be executed immediately and are handled based on the rejection policy.

3. **queueCapacity**
   - **Description**: The maximum number of tasks that can be queued.
   - It acts as a buffer that holds tasks waiting to be executed.
   - If all corePoolSize threads are busy, the task goes into the queue (if there is space).
   - If the number of tasks exceeds the queue capacity, additional threads are created up to maxPoolSize.

4. **threadNamePrefix**
   - **Description**: The prefix to use for the names of the threads.

5. **allowCoreThreadTimeout**
   - **Description**: Whether to allow core threads to time out and terminate if no tasks arrive within the keepAliveSeconds time.
   - By default, core threads are always kept alive, but enabling allowCoreThreadTimeOut(true) allows them to be removed when not needed.
   - If allowCoreThreadTimeOut(false), core threads stay alive forever, even if idle.
   - If allowCoreThreadTimeOut(true), core threads are removed if they remain idle for keepAliveSeconds.
   - It only works if keepAliveSeconds is set.
   - This can help reduce resource consumption when the number of tasks is low.

6. **keepAliveSeconds**
   - **Description**: When the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
   - Controls how long extra threads stay idle before being removed.

7. **rejectedExecutionHandler**
   - **Description**: The handler to use when the executor cannot accept a task.
   - There are four types of RejectedExecutionHandler:
        - AbortPolicy: Throws a RejectedExecutionException.
        - CallerRunsPolicy: Executes the task on the caller's thread.
        - DiscardPolicy: Discards the task silently.
        - DiscardOldestPolicy: Discards the oldest task in the queue and adds the new task.

8. **waitForTasksToCompleteOnShutdown**
   - **Description**: Whether to wait for scheduled tasks to complete on shutdown.

9. **awaitTerminationSeconds**
   - **Description**: The maximum time to wait for the executor to terminate.

### Example Configuration in `SchedulerConfig.java`

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Value("${async.executor.core.pool.size}")
    private int corePoolSize;

    @Value("${async.executor.max.pool.size}")
    private int maxPoolSize;

    @Value("${async.executor.queue.capacity}")
    private int queueCapacity;

    @Value("${async.executor.thread.name.prefix}")
    private String threadNamePrefix;

    @Value("${async.executor.allow.core.thread.timeout}")
    private boolean allowCoreThreadTimeout;
    
    @Value("${async.executor.keep.alive.seconds}")
    private int keepAliveSeconds;

    @Value("${async.executor.rejected.execution.handler}")
    private String rejectedExecutionHandler;

    @Value("${async.executor.wait.for.tasks.to.complete.on.shutdown}")
    private boolean waitForTasksToCompleteOnShutdown;

    @Value("${async.executor.await.termination.seconds}")
    private int awaitTerminationSeconds;
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeout);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setRejectedExecutionHandler(getRejectedExecutionHandler(rejectedExecutionHandler));
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        executor.initialize();
        return executor;
    }
}
```
---

## ✨Tech Stack
The technology used in this project are:
- `Spring Boot Starter Web` – Provides essential components for building RESTful APIs.
- `ThreadPoolTaskExecutor` – Manages a pool of worker threads efficiently for executing tasks asynchronously.
- `Spring Retry` – Handles retry mechanisms for failed operations.
---

## 📋 Project Structure
The project is organized into the following package structure:
```bash
- async
- config
- controller
- dto
- entity
- service
  \---impl
```

### Explanation of Each Package
- **async**: Defines task executor.
- **config**: Configures ThreadPoolTaskExecutor (corePoolSize, maxPoolSize, queueCapacity, etc.).
- **controller**: Contains REST controllers handling Forgot Password and Order Service requests.
- **dto**: Data Transfer Objects (DTOs) for request/response payloads.
- **entity**: Contains Order and OrderDetail classes representing order data.
- **service**: Defines business logic and application functionality.
    - **impl**: Implements the service interfaces.
---

## 📂 Environment Configuration
Configuration values are stored in `.env.development` and referenced in `application.properties`.

Example `.env.development` file content:
```properties
# application
APP_PORT=8081
SPRING_PROFILES_ACTIVE=development

# ThreadPoolTaskExecutor
ASYNC_CORE_POOL_SIZE=5
ASYNC_MAX_POOL_SIZE=10
ASYNC_QUEUE_CAPACITY=100
ASYNC_THREAD_NAME_PREFIX=async-
ASYNC_ALLOW_CORE_THREAD_TIMEOUT=true
ASYNC_KEEP_ALIVE_SECONDS=10
ASYNC_REJECTED_EXECUTION_HANDLER=CALLER_RUNS
ASYNC_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN=true
ASYNC_AWAIT_TERMINATION_SECONDS=10
```

Example `application.properties` file content:
```properties
# application
spring.application.name=async-executor
server.port=${APP_PORT}
spring.profiles.active=${SPRING_PROFILES_ACTIVE}

# ThreadPoolTaskExecutor
async.executor.core.pool.size=${ASYNC_CORE_POOL_SIZE}
async.executor.max.pool.size=${ASYNC_MAX_POOL_SIZE}
async.executor.queue.capacity=${ASYNC_QUEUE_CAPACITY}
async.executor.thread.name.prefix=${ASYNC_THREAD_NAME_PREFIX}
async.executor.allow.core.thread.timeout=${ASYNC_ALLOW_CORE_THREAD_TIMEOUT}
async.executor.keep.alive.seconds=${ASYNC_KEEP_ALIVE_SECONDS}
async.executor.rejected.execution.handler=${ASYNC_REJECTED_EXECUTION_HANDLER}
async.executor.wait.for.tasks.to.complete.on.shutdown=${ASYNC_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN}
async.executor.await.termination.seconds=${ASYNC_AWAIT_TERMINATION_SECONDS}
```
---

## 🛠 Installation & Setup
A step by step series of examples that tell you how to get a development env running.
1. Clone the repository
```bash
git clone https://github.com/yoanesber/Spring-Boot-ThreadPoolTaskExecutor.git
```

2. Navigate to the project directory
```bash
cd <project_directory>
```

4. Build and run the application
```bash
mvn spring-boot:run
```
---

## 🔗 API Endpoints
### Order Service
`POST http://localhost:8081/api/v1/order/process/{orderId}` - Process order asynchronously.

**Response**
```bash
Order processed successfully
```

### Forgot Password Service
`POST http://localhost:8081/api/v1/password/forgot-password` - Initiate forgot password request (async processing).

**Request Body:**
```bash
{
    "email": "jhonny@myemail.com"
}
```

**Response:**
```bash
Password reset email sent successfully
```
---

This project follows best practices in Spring Boot development, ensuring efficiency and maintainability.