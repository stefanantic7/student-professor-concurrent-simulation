package main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.*;

public class Main {
    private static final int STUDENTS_COUNT = 15;

    private static final int PROFESSOR_STARTS_AFTER_MILLIS = 2000;
    private static final int ASSISTANT_STARTS_AFTER_MILLIS = 0;

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch teacherReadyLatch = new CountDownLatch(2) ; // professor thread and assistant thread
        Professor professor = new Professor(teacherReadyLatch, PROFESSOR_STARTS_AFTER_MILLIS);
        Assistant assistant = new Assistant(teacherReadyLatch, ASSISTANT_STARTS_AFTER_MILLIS);
        Thread professorThread = new Thread(professor);
        Thread assistantThread = new Thread(assistant);
        professorThread.setName("Professor-thread");
        assistantThread.setName("Assistant-thread");
        professorThread.start();
        assistantThread.start();

        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(STUDENTS_COUNT);


        //First wait to professor and assistant to start
        teacherReadyLatch.await();
        //From now start timer of 5 seconds.


        Student s1 = new Student(100, professor, assistant);
        new Thread(s1).start();
        Student s2 = new Student(500, professor, assistant);
        new Thread(s2).start();
        Student s3 = new Student(200, professor, assistant);
        new Thread(s3).start();
        Student s4 = new Student(500, professor, assistant);
        new Thread(s4).start();
        Student s5 = new Student(100, professor, assistant);
        new Thread(s5).start();

////        for (int i = 0; i < STUDENTS_COUNT; i++) {
////            double defenseDuration = 0.5 + Math.random() * (1 - 0.5); // from 0.5 to 1 seconds
////            long startAfterMilliseconds = (long)((Math.random()+Double.MIN_VALUE) * 1000); // 0<startAfterSeconds<=1
////
////            System.out.println("idemo "+i);
////
//////            Student student = new Student(defenseDuration);
//////            scheduledThreadPool.schedule(student, startAfterMilliseconds, TimeUnit.MILLISECONDS);
////        }
//
////        scheduledThreadPool.shutdown();
    }
}
