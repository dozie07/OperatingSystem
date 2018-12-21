import java.io.*;
import java.util.concurrent.locks.*;

public class Threads implements Runnable{
    private OS os = new OS();
    private int cpu = 0;
    private boolean runningTask = false;

    public Threads()throws FileNotFoundException{
        Loader.loadFile();

    }


    public void setCPUID(int n){
        cpu = n;
    }
    public int getCPUID(){
        return cpu;
    }

    private static Lock MetricLock = new ReentrantLock();
    private static Lock SchedulerLock = new ReentrantLock();

    public void run(){
        Thread name = Thread.currentThread();

        while(OS.readyQueue.hasJobs()){
            if(runningTask == false){

                runningTask = true;
                SchedulerLock.lock();
                OS.longTermScheduler();

                int CPUNumber = OS.shortTermScheduler();
                long jobTimeStart = System.currentTimeMillis();
                String job = OS.dispatcher();
                System.out.println("Job " + job + " started.\n");
                System.out.println("Loaded to CPU number " + CPUNumber);
                OS.pcb.setCompletionStartTime(job, Long.toString(jobTimeStart));


                long wStart = Long.parseLong(OS.pcb.getWaitStartTime(job));
                long currentWait = jobTimeStart - wStart;
                OS.waitTimeTotal += currentWait;
                OS.waitIndex++;


                SchedulerLock.unlock();

                int jobNumber = ProcessControlBlock.getJobId(job);
                int nm = Integer.parseInt(name.getName());
                OS.pcb.setCpuId(job, nm);
                OS.setIdCPU(job, nm);


                OS.CPU(CPUNumber, job);

                MetricLock.lock();

                long jobTimeEnd = System.currentTimeMillis();


                long currentCompletion = jobTimeEnd - jobTimeStart;
                OS.completionTime += currentCompletion;


                OS.jobTimes [jobNumber-1] = jobTimeStart - jobTimeEnd;
                System.out.println("Job " + job + "completed. ");
                cpu++;

                System.out.println("Wait time of job " + job + ": " + currentWait + " ms.");
                System.out.println("Completion time of job " + job + ": " + currentCompletion+ " ms.");

                MetricLock.unlock();
                System.out.println("IO count for job " + job + ": " + OS.IOcount[Integer.parseInt(job, 16)-1]);

                System.out.println("COMPLETED______________________________________________\n");
            }

            if(!OS.pcb.hasJobs()){
                System.out.println("All jobs completed._______________________________________\n");
            }


            runningTask = false;

        }
        return;
    }

}