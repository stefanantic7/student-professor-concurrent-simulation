package main;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Teacher implements Runnable {

    private CountDownLatch teacherReadyLatch;
    private int startAfterMilliseconds;
    private Semaphore semaphore;
    private AtomicBoolean running;
    private String threadName;

    public Teacher(CountDownLatch teacherReadyLatch, int startAfterMilliseconds, int maxStudents) {
        this.teacherReadyLatch = teacherReadyLatch;
        this.startAfterMilliseconds = startAfterMilliseconds;

        this.semaphore = new Semaphore(maxStudents, true);

        this.running = new AtomicBoolean();
        this.threadName = Thread.currentThread().getName();
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public String getThreadName() {
        return threadName;
    }

    @Override
    public void run() {
        this.threadName = Thread.currentThread().getName();
        try {
            Thread.sleep(this.startAfterMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.running.set(true);
        this.teacherReadyLatch.countDown();

        try {
            //Cekaju se obojica
            this.teacherReadyLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(this.getClass().getSimpleName()+" is ready");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        this.running.set(false);

    }


}
