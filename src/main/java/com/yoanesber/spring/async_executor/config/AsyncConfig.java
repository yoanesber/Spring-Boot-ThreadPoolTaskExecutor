package com.yoanesber.spring.async_executor.config;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@EnableRetry
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
     * ThreadPoolTaskExecutor is a class provided by Spring Framework to handle asynchronous tasks in Spring.
     * It is a powerful asynchronous task execution mechanism that allows you to manage a pool of worker threads efficiently.
     * Here, we are creating a ThreadPoolTaskExecutor bean and configuring it with the properties defined in the application.properties file.
     * 
     * The properties are:
     * 1. corePoolSize: The number of threads to keep in the pool, even if they are idle.
     *    If the number of threads is less than the core, a new thread is created to handle the task.
     *    If the number of threads is greater than the core, the task is added to the queue.
     * 2. maxPoolSize: The maximum number of threads to allow in the pool.
     *    This limit prevents the thread pool from creating too many threads, which could overwhelm the system.
     *    When tasks are submitted, the thread pool starts with corePoolSize threads
     *    If all corePoolSize threads are busy, new tasks are added to the queue
     *    If the queue is full, additional threads are created up to maxPoolSize
     *    Once maxPoolSize is reached, new tasks cannot be executed immediately and are handled based on the rejection policy
     * 3. queueCapacity: The maximum number of tasks that can be queued.
     *    It acts as a buffer that holds tasks waiting to be executed
     *    If all corePoolSize threads are busy, the task goes into the queue (if there is space)
     *    If the number of tasks exceeds the queue capacity, additional threads are created up to maxPoolSize
     * 4. threadNamePrefix: The prefix to use for the names of the threads.
     * 5. allowCoreThreadTimeout: Whether to allow core threads to time out and terminate if no tasks arrive within the keepAliveSeconds time.
     *    By default, core threads are always kept alive, but enabling allowCoreThreadTimeOut(true) allows them to be removed when not needed
     *    If allowCoreThreadTimeOut(false), core threads stay alive forever, even if idle
     *    If allowCoreThreadTimeOut(true), core threads are removed if they remain idle for keepAliveSeconds
     *    It only works if keepAliveSeconds is set
     *    This can help reduce resource consumption when the number of tasks is low
     * 6. keepAliveSeconds: When the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating
     *    Controls how long extra threads stay idle before being removed
     * 7. rejectedExecutionHandler: The handler to use when the executor cannot accept a task.
     *    There are four types of RejectedExecutionHandler:
     *    - AbortPolicy: Throws a RejectedExecutionException.
     *    - CallerRunsPolicy: Executes the task on the caller's thread.
     *    - DiscardPolicy: Discards the task silently.
     *    - DiscardOldestPolicy: Discards the oldest task in the queue and adds the new task.
     * 8. waitForTasksToCompleteOnShutdown: Whether to wait for scheduled tasks to complete on shutdown.
     * 9. awaitTerminationSeconds: The maximum time to wait for the executor to terminate.
     
     * 
     * Important Considerations:
     * 1. Setting maxPoolSize too high
     *    Can create too many threads, leading to CPU overload
     *    Solution: Balance corePoolSize, maxPoolSize, and queueCapacity
     * 2. Setting maxPoolSize too low
     *    Can cause tasks to wait longer or be rejected
     *    Solution: Increase maxPoolSize or queueCapacity
     * 
     * Scenarios:
     * Scenario 1: Tasks < corePoolSize
     * - Given corePoolSize = 5, queueCapacity = 20, maxPoolSize = 10
     * - If you submit 3 tasks, only 3 threads are created
     * 
     * Scenario 2: Tasks > corePoolSize and < maxPoolSize
     * - Given corePoolSize = 5, queueCapacity = 5, maxPoolSize = 10
     * - If you submit 7 tasks, 5 threads are created to process 5 tasks, 2 tasks are queued
     * 
     * Scenario 3: Tasks > corePoolSize and queueCapacity is full
     * - Given corePoolSize = 5, queueCapacity = 5, maxPoolSize = 10
     * - If you submit 15 tasks, 5 threads are created to process 5 tasks, 5 tasks are queued, the remaining 5 tasks create new threads, up to maxPoolSize = 10
     * 
     * Scenario 4: More Tasks Than maxPoolSize
     * - If maxPoolSize = 10 and queue is full, any extra tasks will be rejected based on the RejectedExecutionHandler
     */
    
    //  Create a ThreadPoolTaskExecutor bean to handle asynchronous tasks
    //  The properties are configured using the values from the application.properties file
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

    // Get the RejectedExecutionHandler based on the handler name
    private RejectedExecutionHandler getRejectedExecutionHandler(String handlerName) {
        switch (handlerName.toLowerCase()) {
            case "abort":
                return new ThreadPoolExecutor.AbortPolicy();
            case "caller_runs":
                return new ThreadPoolExecutor.CallerRunsPolicy();
            case "discard":
                return new ThreadPoolExecutor.DiscardPolicy();
            case "discard_oldest":
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            default:
                throw new IllegalArgumentException("Invalid RejectedExecutionHandler: " + handlerName);
        }
    }

    // Handle exceptions in async methods
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            // Get the exception class
            Class<?> exceptionClass = ex.getClass();

            // Log error
            logger.error("Exception in async method: " + method.getName() + 
                            " with method parameters: " + params.toString() + 
                            " with exception class: " + exceptionClass.getName() + 
                            " with exception message: " + ex.getMessage());
        };
    }
}
