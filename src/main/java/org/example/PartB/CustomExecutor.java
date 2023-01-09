package org.example.PartB;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class CustomExecutor extends Thread {

    //data members
    private final PriorityQueue<Task> priorityQueue;
    private int openThreads = 0;
    private final int maxThreads = Runtime.getRuntime().availableProcessors() - 1;
    private final int minThreads = Runtime.getRuntime().availableProcessors() / 2;
    private final AtomicInteger currentMax = new AtomicInteger(10);
    final ReentrantLock lock1 = new ReentrantLock();
    final ReentrantLock lock2 = new ReentrantLock();
    private boolean isRunning;
    private boolean terminating = false;
    private final List<FutureTask> futureTaskList = new ArrayList<>();


    /**
     * Constructor
     * the constructor creates a new priorityQueue and pass the implementation of the compare function we want to use
     * in this case we are comparing between 2 tasks and deciding which one will have a higher priority.
     * add the minimum amount of threads to the list and start them
     * set isRunning to true and start the "daemon" thread that controls the entire thread-pool
     */
    public CustomExecutor() {
        this.priorityQueue = new PriorityQueue<>((task1, task2) ->
                Integer.compare(task1.getTaskType().getPriorityValue(), task1.getTaskType().getPriorityValue()));

        IntStream.range(0, minThreads).forEach(i -> {
            futureTaskList.add(new FutureTask(() -> (null)));
            Thread thread = new Thread(futureTaskList.get(i));
            thread.start();
            openThreads++;
        });
        isRunning = true;
        this.start();
    }

    //get functions
    public int getCurrentMax() {
        return currentMax.get();
    }
    public PriorityQueue<Task> getPriorityQueue() {
        return priorityQueue;
    }
    public int getOpenThreads() {
        return openThreads;
    }
    public int getMaxThreads() {
        return maxThreads;
    }
    public int getMinThreads() {
        return minThreads;
    }
    public boolean isRunning() {
        return isRunning;
    }
    public List<FutureTask> getFutureTaskList() {
        return futureTaskList;
    }

    /**
     * a "background" thread that runs and execute the threads in the futureTaskList
     * add tasks that are waiting in the queue to the futureTaskList and updates the currentmax
     */
    @Override
    public void run() {
        while (isRunning) {
            if (!priorityQueue.isEmpty()) {
                int i = 0;
                while (i < futureTaskList.size()) {
                    boolean flag = futureTaskList.get(i) == null;
                    if (flag || futureTaskList.get(i).isDone()) {
                        if (flag) {
                            openThreads++;
                        }
                        if (terminating) {
                            futureTaskList.set(i, Objects.requireNonNull(priorityQueue.poll()).getFuture());
                        } else {
                            lock1.lock();
                            futureTaskList.set(i, Objects.requireNonNull(priorityQueue.poll()).getFuture());
                            lock1.unlock();
                        }
                        Thread thread = new Thread(futureTaskList.get(i));
                        thread.start();
                        if (priorityQueue.peek() != null) {
                            currentMax.set(priorityQueue.peek().getTaskType().getPriorityValue());
                        }
                        break;
                    }
                    i++;
                }
            }
            int bound = futureTaskList.size();
            for (int i = 0; i < bound; i++) {
                if (openThreads > minThreads) {
                    if (futureTaskList.get(i) != null &&
                            futureTaskList.get(i).isDone()) {
                        futureTaskList.set(i, null);
                        openThreads--;
                    }
                }
            }
            synchronized (lock2) {
                try {
                    lock2.wait(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }


    /**
     * submit a new task to execute
     * @param callable the "task" you want to execute
     * @param type the priority of the task
     * @return return the future result
     */
    public <T> FutureTask<T> submit(Callable<T> callable, TaskType type) {
        Task<T> task = Task.createTask(callable, type);
        return submit(task);
    }

    /**
     * submit a new task to execute with default priority
     * @param callable the "task" you want to execute
     * @return return the future result
     */
    public <T> FutureTask<T> submit(Callable<T> callable) {
        Task<T> task = Task.createTask(callable);
        return submit(task);
    }

    /**
     * submit a new task to execute
     * add the task to the priorityQueue and also block any other thread from adding to the queue.
     * update the current max to be the task's priority at the head of the queue.
     * @return return the future result
     */
    public <T> FutureTask<T> submit(Task<T> task) {
        lock1.lock();
        priorityQueue.add(task);
        lock1.unlock();
        //force release lock1
        synchronized (lock2) {
            lock2.notify();
        }
        if (priorityQueue.peek() != null) {
            currentMax.set(priorityQueue.peek().getTaskType().getPriorityValue());
        }
        return task.getFuture();
    }


    /**
     * terminates the threadpool
     * locking any other thread to touch this method or add anything to the priority Queue
     * wait for the open threads to finish
     * and check if all ongoing threads are done
     */
    public void gracefullyTerminate() {
        lock1.lock();
        terminating = true;
        while (openThreads > minThreads) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        int finished;
        isRunning = false;
        do {
            finished = 0;
            for (FutureTask futureTask : futureTaskList) {
                if (futureTask != null && (futureTask.isDone())) {
                    finished++;
                }
            }
        } while (finished != openThreads);
    }

    /**
     * equals and hascode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomExecutor that = (CustomExecutor) o;
        return maxThreads == that.maxThreads && minThreads == that.minThreads && openThreads == that.openThreads && isRunning == that.isRunning && terminating == that.terminating && Objects.equals(priorityQueue, that.priorityQueue) && Objects.equals(lock1, that.lock1) && Objects.equals(lock2, that.lock2) && Objects.equals(futureTaskList, that.futureTaskList) && Objects.equals(currentMax, that.currentMax);
    }

    @Override
    public int hashCode() {
        return Objects.hash(priorityQueue, lock1, lock2, maxThreads, minThreads, futureTaskList, openThreads, isRunning, terminating, currentMax);
    }

    /**
     * @return string representation of the class
     */
    @Override
    public String toString() {
        return "CustomExecutor{" +
                "priorityQueue=" + priorityQueue +
                ", lock=" + lock1 +
                ", lock2=" + lock2 +
                ", maxThreads=" + maxThreads +
                ", minThreads=" + minThreads +
                ", futureTaskList=" + futureTaskList +
                ", openThreads=" + openThreads +
                ", isRunning=" + isRunning +
                ", terminating=" + terminating +
                ", currentMax=" + currentMax +
                '}';
    }
}
