package main;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class Student implements Runnable {

    private long defenseDurationMills;
    private Professor professor;
    private Assistant assistant;
    private boolean finished = false;
    private int rate;
    private String teacherThreadName;

    public Student(long defenseDurationMills, Professor professor, Assistant assistant) {
        this.defenseDurationMills = defenseDurationMills;
        this.professor = professor;
        this.assistant = assistant;
        this.rate = 0;
    }

    @Override
    public void run() {
        AtomicReference<String> arrivalTime = new AtomicReference<>(Main.getCurrentTimeStamp());
        AtomicReference<String> startedTime = new AtomicReference<>();

        // moraom da imam i ovaj flag running. Moze da se desi da shutdownNow ne ugasi sve, jer su se hendlovali ti interapti.
        while(!this.finished && Main.running.get()) {
            //If professor is available
            if(this.professor.getSemaphore().tryAcquire()) {
                try {
                    this.professor.getStartDefenseBarrier().await(1000, TimeUnit.MILLISECONDS);
                    startedTime.set(Main.getCurrentTimeStamp());
                    Thread.sleep(defenseDurationMills);
                    this.finish(professor);
                } catch (InterruptedException e) {
                    if(this.professor.getStartDefenseBarrier().isBroken()) {
                        //Nije ni poceo, a prekinut je
                        break;
//                        this.professor.getSemaphore().release();
                    }
                    else {
                        //Poceo je, ali je zvonilo
                        try {
                            this.finish(professor);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } catch (BrokenBarrierException e) {
                    this.professor.getSemaphore().release();
                } catch (TimeoutException e) {
                    //nece profesor da me oceni, odoh kod asistenta, ili cu doci ponovo kod profesora
                    this.professor.getSemaphore().release();
                }
            }
            if (!this.finished && this.assistant.getSemaphore().tryAcquire() ) {
                try {
                    startedTime.set(Main.getCurrentTimeStamp());
                    Thread.sleep(defenseDurationMills);

                    this.finish(assistant);
                } catch (InterruptedException e) {
                    try {
                        this.finish(assistant);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }

        if(this.finished) {
            Main.sumOfRates.addAndGet(this.rate);

            System.out.println(String.format("Thread: %s Arrival: %s Prof: %s TTC: %d ms:%s Score: %d",
                    Thread.currentThread().getName(),
                    arrivalTime.get(),
                    this.teacherThreadName,
                    this.defenseDurationMills,
                    startedTime.get(),
                    this.rate
            ));
        }
    }

    private void finish(Teacher teacher) throws InterruptedException {
        this.teacherThreadName = teacher.getThreadName();
        this.rate = teacher.rateStudent();
        this.finished = true;

        if(teacher instanceof Professor) {
            this.professor.getDefenseLatch().countDown();
            //cekaj drugi da zavrsi.
            this.professor.getDefenseLatch().await();
            //restartuj barijeru
//            this.professor.getStartDefenseBarrier().reset(); // Barijera se sama resetuje
            //vrati latch, posle barijere, da ne bi neko uleteo dok ovaj drugi ne oslobodi semafor
            this.professor.resetDefenseLatch();
        }

        //oslobodi semafor
        teacher.getSemaphore().release();
    }
}
