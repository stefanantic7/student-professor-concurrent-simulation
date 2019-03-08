package main;

import java.util.concurrent.*;

public class Student implements Runnable {

    private long defenseDurationMills;
    private Professor professor;
    private Assistant assistant;
    private boolean finished = false;

    public Student(long defenseDurationMills, Professor professor, Assistant assistant) {
        this.defenseDurationMills = defenseDurationMills;
        this.professor = professor;
        this.assistant = assistant;
    }

    @Override
    public void run() {
        while(!this.finished) {
            //If professor is available
            if(this.professor.getSemaphore().tryAcquire()) {
                try {
                    this.professor.getStartDefenseBarrier().await(1000, TimeUnit.MILLISECONDS);
                    Thread.sleep(defenseDurationMills);
                    this.finished = true;
                    //TODO: oceni me
                    this.professor.getDefenseLatch().countDown();
                    //cekaj drugi da zavrsi.
                    this.professor.getDefenseLatch().await();
                    //restartuj barijeru
                    this.professor.getStartDefenseBarrier().reset();
                    //vrati latch, posle barijere, da ne bi neko uleteo dok ovaj drugi ne oslobodi semafor
                    this.professor.resetDefenseLatch();
                    //oslobodi semafor
                    this.professor.getSemaphore().release();
                    System.out.println("student zavrsio kod profesora");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    this.professor.getSemaphore().release();
                } catch (TimeoutException e) {
                    //nece profesor da me oceni, odoh kod asistenta, ili cu doci ponovo kod profesora
                    this.professor.getSemaphore().release();
                }
            }
            if (!this.finished && this.assistant.getSemaphore().tryAcquire() ) {
                try {
                    Thread.sleep(defenseDurationMills);
                    this.finished = true;
                    //TODO: oceni me
                    this.assistant.getSemaphore().release();
                    System.out.println("student zavrsio kod asistenta");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        System.out.println("Student kraj");
    }
}
