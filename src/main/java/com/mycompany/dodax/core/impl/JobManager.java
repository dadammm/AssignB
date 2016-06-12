package com.mycompany.dodax.core.impl;

import com.mycompany.dodax.core.IJobManager;
import com.mycompany.dodax.model.IJob;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Class which holds and manages the jobs to run, along with the threads and 
 * JobsRunnables associated with them
 */
public class JobManager implements IJobManager{

    private int numberOfThreads;
    private List<Thread> threadList;
    private List<JobsRunnable> jobsRunnableList;
    private ArrayList<IJob> jobList;
    
    // constructor takes the number of threads to be used, along with the list of jobs
    public JobManager(int numThreads, ArrayList<IJob> jobList){
        // ERROR HANDLING
        if (numThreads>jobList.size() || numThreads <1)
            return;
        numberOfThreads = numThreads;
        this.jobList = jobList;
    }
    
    @Override
    public void executeJobs() {
        for(Thread t: threadList)
            t.start();
    }
   
    @Override
    public void setNumThreads(int n) {
        numberOfThreads = n;
    }

    @Override
    public void setJobs(ArrayList<IJob> jobs) {
        jobList = jobs;
    }

    // method to evenly assign the jobs between threads 
    // e.g: 10 jobs, 2 threads -> each thread 5 jobs
    // e.g: 11 jobs, 2 threads -> thread 1 does 6 jobs, thread 2 does 5 jobs
    @Override
    public void assignJobsToThreads() {
        threadList = new ArrayList<Thread>();
        jobsRunnableList = new ArrayList<JobsRunnable>();
        
        int mod = jobList.size()%numberOfThreads;
        int divisor = jobList.size()/numberOfThreads;
        int j=0;
        for (int i=0;i<numberOfThreads;++i){
            JobsRunnable temp = new JobsRunnable(this);
            for(j =divisor*i;j<(divisor*(i+1));++j){
                temp.addJob(jobList.get(j));
            }
            Thread t = new Thread(temp);
            jobsRunnableList.add(temp);
            threadList.add(t);
        }
        for (int i = 0;j<jobList.size();++j,++i){
            JobsRunnable temp = jobsRunnableList.get(i);
            temp.addJob(jobList.get(j));
        }
    }

    // methods to stop threads from executing further jobs
    // called when an exception occurs
    @Override
    public void stopExecution() {
        for (JobsRunnable j: jobsRunnableList)
            j.stop();
    }
    
    public ArrayList<IJob> getJobs(){
        return jobList;
    }
    
    public List<Thread> getThreads(){
        return threadList;
    }
    
    public List<JobsRunnable> getJobsRunnableList(){
        return jobsRunnableList;
    }
    
}
