package com.mycompany.dodax.core;

import com.mycompany.dodax.model.IJob;
import java.util.List;
/**
 *
 * interface for the JobsRunnable class
 * each class implementing this class must also override the run() method
 */
public interface IJobsRunnable extends Runnable{
    public void setJobs(List<IJob> list);
    public void addJob(IJob job);
    public void removeFirstInstanceOfJob(IJob job);
    public void removeLastJob();
    public void stop();
    
}
