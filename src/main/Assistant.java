package main;

import java.util.concurrent.CountDownLatch;

public class Assistant extends Teacher {

    public Assistant(CountDownLatch teacherReadyLatch, int startAfterMilliseconds) {
        super(teacherReadyLatch, startAfterMilliseconds, 1);
    }
}
