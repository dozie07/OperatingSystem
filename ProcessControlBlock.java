import java.util.ArrayList;

public class ProcessControlBlock {
    // jobID, state, codeSize, priority, inputBuffer, outputBuffer, tempBuffer, startIndex, endIndex, programCounter,
    // registers, structureTimes, CpuId, waitStartTime, completionStartTime
    public static ArrayList<String[]> pcb = new ArrayList<>();

    private int jobIndex(String jobId) {
        for (int i = 0; i < pcb.size(); i++) {
            if (pcb.get(i)[0].equals(jobId))
                return i;
        }
        return -1;
    }

    public static int getJobId(String jobId){
        return Integer.parseInt(jobId, 16);
    }

    public void addProcess(String jobId) {
        String[] newProcess = new String[15];
        newProcess[0] = jobId;
        pcb.add(newProcess);
    }
    public void addProcess(String[] newProcess) {
        pcb.add(newProcess);
    }

    public void deleteProcess(String jobId) {
        pcb.remove(jobIndex(jobId));
    }

    public int pcbSize() {
        return pcb.size();
    }

    public boolean hasJobs() {
        return pcb.size() != 0;
    }

    public void printPcb() {
        System.out.println("Job ID\tState");
        for(String[] job : pcb) {
            System.out.println(job[0] + "\t" + job[1]);
        }
    }

    // State
    public String getState(String jobId) {
        return pcb.get(jobIndex(jobId))[1];
    }
    public void setState(String jobId, String state) {
        pcb.get(jobIndex(jobId))[1] = state;
    }

    // Code Size
    public String getCodeSize(String jobId) {
        return pcb.get(jobIndex(jobId))[2];
    }
    public void setCodeSize(String jobId, String codeSize) {
        pcb.get(jobIndex(jobId))[2] = codeSize;
    }

    // Priority
    public int getPriority(String jobId) {
        int jobNumber = jobIndex(jobId);
        return Integer.parseInt(pcb.get(jobNumber)[3], 16);
    }
    public void setPriority(String jobId, String priority) {
        pcb.get(jobIndex(jobId))[3] = priority;
    }

    // Input Buffer
    public String getInputBuffer(String jobId) {
        return pcb.get(jobIndex(jobId))[4];
    }
    public void setInputBuffer(String jobId, String inputBuffer) {
        pcb.get(jobIndex(jobId))[4] = inputBuffer;
    }

    // Output Buffer
    public String getOutputBuffer(String jobId) {
        return pcb.get(jobIndex(jobId))[5];
    }
    public void setOutputBuffer(String jobId, String outputBuffer) {
        pcb.get(jobIndex(jobId))[5] = outputBuffer;
    }

    // Temp Buffer
    public String getTempBuffer(String jobId) {
        return pcb.get(jobIndex(jobId))[6];
    }
    public void setTempBuffer(String jobId, String tempBuffer) {
        pcb.get(jobIndex(jobId))[6] = tempBuffer;
    }

    // Start Index in Memory
    public int getStartIndex(String jobId) {
        int jobNumber = jobIndex(jobId);
        return Integer.parseInt(pcb.get(jobNumber)[7], 16);
    }
    public void setStartIndex(String jobId, String startIndex) {
        pcb.get(jobIndex(jobId))[7] = startIndex;
    }

    // End Index in Memory
    public int getEndIndex(String jobId) {
        int jobNumber = jobIndex(jobId);
        return Integer.parseInt(pcb.get(jobNumber)[8], 16);
    }
    public void setEndIndex(String jobId, String endIndex) {
        pcb.get(jobIndex(jobId))[8] = endIndex;
    }

    // Program Counter
    public int getProgramCounter(String jobId) {
        return Integer.parseInt(pcb.get(jobIndex(jobId))[9]);
    }
    public void setProgramCounter(String jobId, String programCounter) {
        pcb.get(jobIndex(jobId))[9] = programCounter;
    }

    // Registers
    public String getRegisters(String jobId) {
        return pcb.get(jobIndex(jobId))[10];
    }
    public void setRegisters(String jobId, String registers) {
        pcb.get(jobIndex(jobId))[10] = registers;
    }

    // Structure Times
    public String getStructureTimes(String jobId) {
        return pcb.get(jobIndex(jobId))[11];
    }
    public void setStructureTimes(String jobId, String structureTimes) {
        pcb.get(jobIndex(jobId))[11] = structureTimes;
    }

    // CPU ID
    public String getCpuId(String jobId) {
        return pcb.get(jobIndex(jobId))[12];
    }
    public void setCpuId(String jobId, int cpuId) {
        int jobNumber = jobIndex(jobId);
        pcb.get(jobNumber)[12] = Integer.toString(cpuId);
    }

    // Wait Start Time
    public String getWaitStartTime(String jobId) {
        int jobNumber = jobIndex(jobId);
        return pcb.get(jobNumber)[13];
    }
    public void setWaitStartTime(String jobId, String waitStartTime) {
        pcb.get(jobIndex(jobId))[13] = waitStartTime;
    }

    // Completion Start Time
    public String getCompletionStartTime(String jobId) {
        return pcb.get(jobIndex(jobId))[14];
    }
    public void setCompletionStartTime(String jobId, String completionStartTime) {
        pcb.get(jobIndex(jobId))[14] = completionStartTime;
    }



    public int getProcessSize(String jobId){
        return getEndIndex(jobId) - getStartIndex(jobId) + 1;
    }
}