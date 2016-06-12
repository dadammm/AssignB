package com.mycompany.dodax.core;

import com.mycompany.dodax.model.IJob;
import java.util.ArrayList;
/**
 *
 * interface for the JobManager class
 */
public interface IJobManager {
    public void executeJobs();
    public void setNumThreads(int n);
    public void setJobs(ArrayList<IJob> jobs);
    public void assignJobsToThreads();
    public void stopExecution();
    
}
