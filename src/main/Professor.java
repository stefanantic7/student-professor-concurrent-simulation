package main;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Professor extends Teacher {

    private CountDownLatch defenseLatch;
    private CyclicBarrier startDefenseBarrier;
    private int maxStudents;

    public Professor(CountDownLatch teacherReadyLatch, int startAfterMilliseconds) {
        super(teacherReadyLatch, startAfterMilliseconds, 2);

        this.maxStudents = 2;
        this.startDefenseBarrier = new CyclicBarrier(maxStudents);
        this.defenseLatch = new CountDownLatch(maxStudents);
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
}
