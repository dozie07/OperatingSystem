import java.util.*;
import java.util.concurrent.locks.*;

public class Queue{
    private LinkedList<String> list = new LinkedList<>();
    public Queue(){
    }
    public void enqueue(String job){
        list.addLast(job);
    }
    private static Lock lock1 = new ReentrantLock();
    public String dequeue(){
        lock1.lock();
        String element = list.poll();
        lock1.unlock();
        return element;
    }
    public boolean hasJobs(){
        return !list.isEmpty();
    }

    public void addJobTo(int i, String job){

        list.add(i, job);
    }

    public void removeJobFrom(int i){

        list.remove(i);
    }

    public void addJobFrom(Queue q){
        while(q.hasJobs())
            list.addLast(q.dequeue());
    }
    public String checkFirstJob(){

        return list.peek();
    }

    public String checkJob(int index){

        return list.get(index);
    }

    public int size(){

        return list.size();
    }

    public void printQueue(){
        for(int i = 0; i < list.size(); i++){
            System.out.print(list.get(i) + " ");
        }
        System.out.println();
    }

    public void replace(int i, String job){
        list.add(i, job);
        list.remove(i+1);
    }
}
