import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class OS {

    public static ProcessControlBlock pcb = new ProcessControlBlock();

    final static int RAM_SIZE = 1024;
    final static int DISK_SIZE = 2048;

    public static String[] RAM = new String[RAM_SIZE];
    public static String[] disk = new String[DISK_SIZE];

    //Queues
    public static Queue newQueue = new Queue();
    public static Queue waitQueue = new Queue();
    public static Queue readyQueue = new Queue();

    private static boolean multiProcessor = false;
    private static boolean FIFOSort = false;
    private static boolean SJFsort = true;

    private static int largestRAMSpaceStart = 0;

    public static int[] programLines = new int[8];

    public static int IOcount[] = new int[30];

    public static int[] CPUs = new int[4];

    //Program measurements
    public static int waitIndex = 0;
    public static long waitTimeTotal = 0;
    public static long completionTime = 0;
    public static long[] jobTimes = new long[30];
    public static double maxPercent = 0;
    public static double currentPercent = 0;
    public static int[] IdCPU = new int[30];

    public OS(){

    }

    public void launch() throws FileNotFoundException{

        CPUs[0] = -1;
        CPUs[1] = -1;
        CPUs[2] = -1;
        CPUs[3] = -1;

        if (!multiProcessor) {
            try {
                Loader.loadFile();
            } catch (FileNotFoundException e) {
                System.err.println("File not found");
            }
            System.out.println("Single CPU simulation starting.");

            System.out.println("Priority sorting\n");
            longTermScheduler();


            while (OS.readyQueue.hasJobs()) {
                OS.longTermScheduler();
                OS.Sort();
                if (SJFsort)
                    SJFsort();
                String job = OS.dispatcher();
                System.out.println("***** Job " + job + " started. *****");

                long jobTimeStart = System.currentTimeMillis();
                pcb.setCompletionStartTime(job, Long.toString(jobTimeStart));


                long currentWait = processWaitTime(job, jobTimeStart);
                waitTimeTotal += currentWait;
                waitIndex++;

                int jobNumber = ProcessControlBlock.getJobId(job);
                OS.CPU(0, job);
                long jobTimeEnd = System.currentTimeMillis();
                long currentCompletion = jobTimeEnd - jobTimeStart;
                completionTime += currentCompletion;

                OS.jobTimes[jobNumber - 1] = jobTimeStart - jobTimeEnd;
                System.out.println("Wait time of job " + job + ": " + currentWait + " ms.");
                System.out.println("Completion time of job " + job + ": " + currentCompletion + " ms.");
                System.out.println("IO count for job " + job + ": " + IOcount[Integer.parseInt(job, 16) - 1]);
                System.out.println("Job " + job + " completed.\n");
                System.out.println("________________________________________________________________");
            }
            System.out.println("Single CPU simulation completed.\n");

            System.out.println("Measurements:");
            System.out.println("________________________________________________________________");

            System.out.println("Total wait time: " + waitTimeTotal + " ms; Avg wait time: " + waitTimeTotal / waitIndex + " ms.");
            System.out.println("Total completion time: " + completionTime + " ms; Avg completion time: " + completionTime / waitIndex + " ms.");
            System.out.println("Max percentage of RAM used during this run: " + maxPercent + "%.");

            System.out.println("________________________________________________________________");

            System.out.println();

        }

        else{
            System.out.println("Multi-CPU Simulation starting");
            if(FIFOSort)
                System.out.println("Fifo sorting\n");
            else
                System.out.println("Priority sorting\n");
            Threads task = new Threads();
            Thread t1 = new Thread(task);
            Thread t2 = new Thread(task);
            Thread t3 = new Thread(task);
            Thread t4 = new Thread(task);
            t1.setName("0");
            t2.setName("1");
            t3.setName("2");
            t4.setName("3");
            longTermScheduler();
            shortTermScheduler();
            if (SJFsort)
                SJFsort();
            synchronized(disk){
                synchronized(RAM){
                    synchronized(pcb){

                        if(OS.readyQueue.hasJobs()){
                            System.out.println("Thread 1 starting\n");
                            t1.start();
                        }
                        if(OS.readyQueue.hasJobs()){
                            System.out.println("Thread 2 starting\n");
                            t2.start();
                        }
                        if(OS.readyQueue.hasJobs()){
                            System.out.println("Thread 3 starting\n");
                            t3.start();
                        }
                        if(OS.readyQueue.hasJobs()){
                            System.out.println("Thread 4 starting\n");
                            t4.start();
                        }
                    }
                }
            }
            try{
                t1.join();
                t2.join();
                t3.join();
                t4.join();

                System.out.println("Measurements");
                System.out.println("________________________________________________________________");

                System.out.println("Total wait time: " + waitTimeTotal + " ms; Avg wait time: " + waitTimeTotal/waitIndex + " ms.");
                System.out.println("Total completion time: " + completionTime + " ms; Avg completion time: " + completionTime/waitIndex + " ms.");


                System.out.println();

                System.out.println("Jobs ran on CPU 1: " + jobsOnCPU(t1.getName()));
                System.out.println("The percentage of jobs ran on CPU 1: " + percentCPUJobs(t1.getName()) + "%.\n");
                System.out.println("Jobs ran on CPU 2: " + jobsOnCPU(t2.getName()));
                System.out.println("The percentage of jobs ran on CPU 2: " + percentCPUJobs(t2.getName()) + "%.\n");
                System.out.println("Jobs ran on CPU 3: " + jobsOnCPU(t3.getName()));
                System.out.println("The percentage of jobs ran on CPU 3: " + percentCPUJobs(t3.getName()) + "%.\n");
                System.out.println("Jobs ran on CPU 4: " + jobsOnCPU(t4.getName()));
                System.out.println("The percentage of jobs ran on CPU 4: " + percentCPUJobs(t4.getName()) + "%.\n");

                System.out.println("________________________________________________________________");
            }
            catch(Exception e){
                System.out.println("Error has occured.");
            }
        }

    }


    public static void decode(String [] programCache, String hexCode, String jobId, int [] registers, int CPUNumber) {
        //Converts string to binary
        String hexString = hexCode.substring(2, 10);
        String binString = String.format("%32s", Long.toBinaryString(Long.parseLong(hexString, 16)));
        binString = binString.replace(' ', '0');

        Processor.execute(binString, jobId, registers, CPUNumber, programCache);
    }


    public static void Sort()
    {
        int prNum, prNumNext;
        String temp;
        for(int i = 0; i < readyQueue.size()-1; i++){
            prNum = pcb.getPriority(readyQueue.checkJob(i));
            for(int j = i+1; j < readyQueue.size(); j++){
                prNumNext = pcb.getPriority(readyQueue.checkJob(j));
                if(prNumNext > prNum){
                    temp = readyQueue.checkJob(i);
                    readyQueue.replace(i,readyQueue.checkJob(j));
                    readyQueue.replace(j, temp);
                    prNum = pcb.getPriority(readyQueue.checkJob(i));
                }
                else if(prNumNext == prNum){
                    int currentJob = Integer.parseInt(readyQueue.checkJob(i), 16);
                    int nextJob = Integer.parseInt(readyQueue.checkJob(j), 16);
                    if(nextJob < currentJob){
                        temp = readyQueue.checkJob(i);
                        readyQueue.replace(i,readyQueue.checkJob(j));
                        readyQueue.replace(j, temp);
                        prNum = pcb.getPriority(readyQueue.checkJob(i));
                    }
                }
            }
        }
    }


    private static Lock LTSLock = new ReentrantLock();
    public static void longTermScheduler()
    {
        LTSLock.lock();
        String jobId;
        if(waitQueue.hasJobs()){
            if(!FullRAM(waitQueue.checkJob(0))){
                jobId = waitQueue.dequeue();
                LoadRAM(jobId);
                readyQueue.enqueue(jobId);
                long timeStart = System.currentTimeMillis();
                pcb.setWaitStartTime(jobId, Long.toString(timeStart));
                pcb.setState(jobId, "ready");
            }
        }
        while(newQueue.hasJobs()){
            jobId = newQueue.dequeue();
            if(!FullRAM(jobId)){
                LoadRAM(jobId);
                readyQueue.enqueue(jobId);
                long timeStart = System.currentTimeMillis();
                pcb.setWaitStartTime(jobId, Long.toString(timeStart));
                pcb.setState(jobId, "ready");
            }
            else{
                waitQueue.enqueue(jobId);
                pcb.setState(jobId, "wait");
            }

        }
        LTSLock.unlock();
    }

    public static void LoadRAM(String jobId){
        if(!FullRAM(jobId)){
            int index = largestRAMSpaceStart;
            for(int i = pcb.getStartIndex(jobId); i <= pcb.getEndIndex(jobId); i++){
                RAM[index] = disk[i];
                disk[i] = null;
                index++;
            }
            pcb.setStartIndex(jobId, Integer.toHexString(largestRAMSpaceStart));
            pcb.setEndIndex(jobId, Integer.toHexString(index-1));
            pcb.setProgramCounter(jobId, Integer.toHexString(largestRAMSpaceStart));
        }
    }

    //If False, RAM is not full
    private static boolean FullRAM(String jobId){
        if(pcb.getProcessSize(jobId) <= RAMSpace()){
            return false;
        }
        else{
            return true;
        }
    }


    private static int RAMSpace(){
        int maxSpace = 0;
        int space = 0;
        int RAMIndexS = 0;
        int RAMIndexE = 0;
        for(int i = 0; i < RAM.length; i++){
            if(RAM[i] == null && space == 0){
                space++;
                RAMIndexS = i;
                RAMIndexE = RAMIndexS;
                if(space > maxSpace){
                    maxSpace = space;
                    largestRAMSpaceStart = RAMIndexS;

                }
            }
            else if(RAM[i] == null && space > 0){
                space++;
                RAMIndexE++;
                if(space > maxSpace){
                    maxSpace = space;
                    largestRAMSpaceStart = RAMIndexS;
                }
            }
            else{
                space = 0;
            }
        }
        return maxSpace;
    }

    private static Lock PCBLock = new ReentrantLock();
    public static void PCBiterator(String jobId)  {
        PCBLock.lock();

        int thePC = pcb.getProgramCounter(jobId) + 1;
        pcb.setProgramCounter(jobId,Integer.toString(thePC));

        PCBLock.unlock();

    }

    public static void setIdCPU(String job, int CPU){
        IdCPU[Integer.parseInt(job, 16)-1] = CPU;
    }

    private static Lock CPULock = new ReentrantLock();
    public static void CPU(int CPUNumber, String jobId)
    {
        CPULock.lock();
        currentPercent = getRAMpercent();
        if(currentPercent > maxPercent){
            maxPercent = currentPercent;
        }
        System.out.println("Percentage of RAM used: " + OS.getRAMpercent());
        System.out.println();

        String [] programCache = new String [pcb.getEndIndex(jobId) - pcb.getStartIndex(jobId)];
        int [] registers = new int[16];
        fetch(programCache, jobId, registers, CPUNumber);
        CPULock.unlock();
    }

    //First part of CPU, fetches
    public static void fetch(String [] programCache, String jobId, int [] registers, int CPUNumber)
    {
        int StartingLine = pcb.getStartIndex(jobId);
        int EndingLine = pcb.getEndIndex(jobId);
        String StartingLinestring;
        StartingLinestring = Integer.toString(StartingLine);
        pcb.setProgramCounter(jobId,StartingLinestring);
        String [] programCacheClone = new String [pcb.getEndIndex(jobId) - pcb.getStartIndex(jobId)];

        for(int i = 0; i < programCache.length; i++)
        {
            programCache[i] = RAM[pcb.getStartIndex(jobId) + i];
            programCacheClone[i] = RAM[pcb.getStartIndex(jobId) + i];
        }

        //Fetches until ProgramCounter goes passed the last line of code.
        while ( EndingLine > pcb.getProgramCounter(jobId))
        {
            decode(programCache,programCache[programLines[CPUNumber]],  jobId, registers, CPUNumber);
        }

        // Amends the changes made in the Cache to RAM once program running is complete
        for(int i = 0; i < programCache.length; i++)
        {
            if(!programCache[i].equals(programCacheClone[i]))
            {
                RAM[pcb.getStartIndex(jobId) + i] = programCache[i];
            }
        }

        if( EndingLine < pcb.getProgramCounter(jobId))
        {
            for(int i= StartingLine; i < EndingLine+1; i++)
            {
                RAM[i] = null;
            }
            programLines[CPUNumber] = 0;
            CPUs[CPUNumber] = -1;
            System.out.println("Deleting....");
            pcb.deleteProcess(jobId);
            System.out.println("Process deleted.\n");
        }
    }

    public static double percentCPUJobs(String CPUId){
        int CPU = Integer.parseInt(CPUId);
        double jbCtr = 0;
        if(CPU == 0){
            for(int i : IdCPU){
                if(i == 0)
                    jbCtr++;
            }
        }
        if(CPU == 1){
            for(int i : IdCPU){
                if(i == 1)
                    jbCtr++;
            }
        }
        if(CPU == 2){
            for(int i : IdCPU){
                if(i == 2)
                    jbCtr++;
            }
        }
        if(CPU == 3){
            for(int i : IdCPU){
                if(i == 3)
                    jbCtr++;
            }
        }
        return (jbCtr/IdCPU.length)*100;
    }

    private static Lock dispatcherLock = new ReentrantLock();
    public static String dispatcher(){
        dispatcherLock.lock();
        String temp  = readyQueue.dequeue();
        dispatcherLock.unlock();
        return temp;
    }

    public static long processWaitTime(String jobId, long end) {
        synchronized (pcb) {
            return end - Long.parseLong(pcb.getWaitStartTime(jobId));
        }
    }
    public static long processCompletionTime(String jobId, long end) {
        synchronized (pcb) {
            return end - Long.parseLong(pcb.getCompletionStartTime(jobId));
        }
    }

    private static Lock STSLock = new ReentrantLock();
    public  static int shortTermScheduler()
    {
        STSLock.lock();
        int y = -1;
        int CPUAssignment;
        if(CPUs[0] == (y))
        {
            CPUs[0] = 0;
            CPUAssignment = 0;
            programLines[0] = 0;
        }
        else if (CPUs[1] == (y))
        {
            CPUs[1] = 0;
            CPUAssignment = 1;
            programLines[1] = 0;
        }
        else if (CPUs[2] == (y))
        {
            CPUs[2] = 0;
            CPUAssignment = 2;
            programLines[2] = 0;
        }
        else if (CPUs[3] == (y))
        {
            CPUs[3] = 0;
            CPUAssignment = 3;
            programLines[3] = 0;
        }

        else{
            CPUAssignment = 0;
            System.err.println("Error at cpu");
        }

        if(!FIFOSort){
            Sort();
        }

        STSLock.unlock();
        return CPUAssignment;
    }

    public String jobsOnCPU(String CPU){
        String jobs = new String();
        for(int i = 0; i < IdCPU.length; i++){
            if(IdCPU[i] == (Integer.parseInt(CPU))){
                jobs += Integer.toHexString(i+1) + " ";
            }
        }
        return jobs;
    }

    public static double getRAMpercent()
    {
        int FreeRAMcount = 0;
        for(int i = 0; i < RAM.length; i++)
        {
            if(RAM[i]== null)
            {
                FreeRAMcount++;
            }

        }
        if (FreeRAMcount == 0)
        {
            return 100;
        }
        else
        {
            double RAMusage = (1024.0 - FreeRAMcount)/1024.0;
            RAMusage = RAMusage*100;
            return RAMusage;
        }
    }

    public static void SJFsort()
    {
        System.out.println("Starting Shortest-Job-First Sort\n");
        int prNum = 0, prNumNext = 0;
        String temp = new String();
        for(int i = 0; i < readyQueue.size()-1; i++){
            prNum = pcb.getProcessSize(readyQueue.checkJob(i));
            for(int j = i+1; j < readyQueue.size(); j++){
                prNumNext = pcb.getProcessSize(readyQueue.checkJob(j));
                if(prNumNext < prNum){
                    temp = readyQueue.checkJob(i);
                    readyQueue.replace(i,readyQueue.checkJob(j));
                    readyQueue.replace(j, temp);
                    prNum = pcb.getProcessSize(readyQueue.checkJob(i));
                }
                else if(prNumNext == prNum){
                    int currentJob = Integer.parseInt(readyQueue.checkJob(i), 16);
                    int nextJob = Integer.parseInt(readyQueue.checkJob(j), 16);
                    if(nextJob < currentJob){
                        temp = readyQueue.checkJob(i);
                        readyQueue.replace(i,readyQueue.checkJob(j));
                        readyQueue.replace(j, temp);
                        prNum = pcb.getPriority(readyQueue.checkJob(i));
                    }
                }
            }
        }
    }


} //End of OS Class
