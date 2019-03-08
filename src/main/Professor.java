package main;

import java.util.concurrent.CountDownLatch;

public class Professor extends Teacher {

    public Professor(CountDownLatch teacherReadyLatch, int startAfterMilliseconds) {
        super(teacherReadyLatch, startAfterMilliseconds, 2);
    }
}
