package main;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Teacher implements Runnable {

    private CountDownLatch teacherReadyLatch;
    private CountDownLatch defenseLatch;
    private CyclicBarrier startDefenseBarrier;
    private int startAfterMilliseconds;
    private Semaphore semaphore;
    private int maxStudents;
    private AtomicBoolean running;

    public Teacher(CountDownLatch teacherReadyLatch, int startAfterMilliseconds, int maxStudents) {
        this.teacherReadyLatch = teacherReadyLatch;
        this.startAfterMilliseconds = startAfterMilliseconds;

        this.startDefenseBarrier = new CyclicBarrier(maxStudents);
        this.defenseLatch = new CountDownLatch(maxStudents);

        this.semaphore = new Semaphore(maxStudents, true);
        this.maxStudents = maxStudents;

        this.running = new AtomicBoolean();
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public CountDownLatch getDefenseLatch() {
        return defenseLatch;
    }

    public CyclicBarrier getStartDefenseBarrier() {
        return startDefenseBarrier;
    }

    public void resetDefenseLatch() {
        this.defenseLatch = new CountDownLatch(maxStudents);
    }
    @Override
    public void run() {
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
