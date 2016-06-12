package com.mycompany.dodax.core.impl;

import com.mycompany.dodax.core.IJobManager;
import com.mycompany.dodax.core.IJobsRunnable;
import com.mycompany.dodax.model.IJob;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Class which implements IJobsRunnable and overrides the run() method
 * each JobsRunnable object is assigned a chunk of jobs by the corresponding JobManager
 */
public class JobsRunnable implements IJobsRunnable{

    private List<IJob> listOfJobs;
    private volatile boolean running; //set to false when an error occurs, signaling to stop execution
    private IJobManager jobManager; // JobManager is used to signal JobsRunnable to stop, and also assings jobs to it
    
    public JobsRunnable(){}
    
    public JobsRunnable(JobManager m) {
        jobManager = m;
        listOfJobs = new ArrayList<IJob>();
        running = true;
   }

    public List<IJob> getListOfJobs(){
        return listOfJobs;
    }
    
    @Override
    public void setJobs(List<IJob> list) {
        listOfJobs = list;
    }

    @Override
    public void addJob(IJob job) {
       listOfJobs.add(job);
    }
    
    @Override // Deletes the first instance of a job
    public void removeFirstInstanceOfJob(IJob job){
        for (IJob j: listOfJobs)
          if (job.equals(j)){
              listOfJobs.remove(j);
              return;
          }
    }
    
    public void removeAllJobs(){
        listOfJobs.clear();
    }

    @Override
    public void removeLastJob() {
        listOfJobs.remove(listOfJobs.size()-1);
    }

   @Override
    public void run() {
        boolean finnished = false; // flag which is set to true if all the jobs have been executed
        while (running && !finnished){
            try{
            for (IJob j: listOfJobs){
                Thread.sleep(500); 
                if (running) // if running is true, no exception has occured and the thread may execute the curent job
                    j.execute();
            }
            finnished = true;
            }catch(Exception e){ // exception occured so signal is sent for all the other threads to stop execution
                running = false;
                jobManager.stopExecution();
            }
        }
    }
    // called by JobManager to halt execution of further jobs
    @Override
    public void stop(){
        running = false;
    }
    
    public boolean getRunning(){
        return running;
    }
    
}
