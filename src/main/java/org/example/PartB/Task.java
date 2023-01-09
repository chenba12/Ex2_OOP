package org.example.PartB;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class Task<T> implements Callable<T> {

    //data members
    private final TaskType taskType;
    private final Callable callable;
    private final FutureTask<T> future;


    //constructor
    private <T> Task(Callable<T> callable) {
        this(callable, TaskType.COMPUTATIONAL);
    }

    private <T> Task(Callable<T> callable, TaskType taskType) {
        this.taskType = taskType;
        this.callable = callable;
        future = new FutureTask(callable);
    }


    //get functions
    public FutureTask getFuture() {
        return future;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public Callable getCallable() {
        return callable;
    }


    /**
     * factory method design pattern.
     * @param callable the "task" that will be executed
     * @param taskType the priority of the task
     * @return a new instance of the Task Class
     */
    public static <T> Task<T> createTask(Callable<T> callable, TaskType taskType) {
            return new Task<>(callable, taskType);
    }

    /**
     * factory method design pattern with a default priority.
     * @param callable the "task" that will be executed
     * @return a new instance of the Task Class
     */
    public static <T> Task<T> createTask(Callable<T> callable) {
        return new Task<>(callable);
    }

    /**
     * @return retuns the result from the thread with a given future
     * @throws Exception
     */
    @Override
    public T call() throws Exception {
        Thread thread = new Thread(future);
        thread.start();
        return future.get();
    }


    /**
     * equals and hascode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskType == task.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskType);
    }

    /**
     * @return string representation of the class
     */
    @Override
    public String toString() {
        return "Task{" +
                "taskType=" + taskType +
                ", callable=" + callable +
                ", future=" + future +
                '}';
    }
}
