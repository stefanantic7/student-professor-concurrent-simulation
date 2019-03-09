package main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int STUDENTS_THREADS_COUNT = 5;

    private static final int PROFESSOR_STARTS_AFTER_MILLIS = 2000;
    private static final int ASSISTANT_STARTS_AFTER_MILLIS = 0;

    public static final AtomicInteger sumOfRates = new AtomicInteger(0);
    public static final AtomicBoolean running = new AtomicBoolean(true);

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
    }

    public static long convertToMills(double seconds) {
        return (long)(seconds*1000);
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.print("Unesite broj studenata: ");
        Scanner scanner = new Scanner(System.in);
        int studentsCount = scanner.nextInt();

        CountDownLatch teacherReadyLatch = new CountDownLatch(2) ; // professor thread and assistant thread
        Professor professor = new Professor(teacherReadyLatch, PROFESSOR_STARTS_AFTER_MILLIS);
        Assistant assistant = new Assistant(teacherReadyLatch, ASSISTANT_STARTS_AFTER_MILLIS);

        ExecutorService teachersExecutorService = Executors.newFixedThreadPool(2);
        teachersExecutorService.execute(professor);
        teachersExecutorService.execute(assistant);

        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(STUDENTS_THREADS_COUNT);


        //First wait to professor and assistant to start
        teacherReadyLatch.await();
        //From now start timer of 5 seconds.

        for (int i = 0; i < studentsCount; i++) {
            long defenseDuration = Main.convertToMills(0.5 + Math.random() * (1 - 0.5)); // from 0.5 to 1 seconds
            long startAfterMilliseconds = Main.convertToMills(Math.random()+Double.MIN_VALUE); // 0<startAfterSeconds<=1

            Student student = new Student(defenseDuration, professor, assistant);
            scheduledThreadPool.schedule(student, startAfterMilliseconds, TimeUnit.MILLISECONDS);
        }

        Thread.sleep(5000); //Wait 5 seconds ( ako se stavi 100 millis moze da se vidi kako su samo par njih uspeli da rese, max 3)
        Main.running.set(false);
        scheduledThreadPool.shutdownNow();

        professor.stop();
        assistant.stop();
        teachersExecutorService.shutdown();

        // Moze da se desi da je zvonilo a nekome je ostala samo ocena da se upise,
        // pa pre ispisa proseka cemo jos malo da sacekamo
        Thread.sleep(1000);
        System.out.println("Rates sum: "+sumOfRates.get());
        System.out.println("Average: "+sumOfRates.get()/(double)studentsCount);

    }
}
