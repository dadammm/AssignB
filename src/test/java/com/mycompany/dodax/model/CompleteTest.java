package com.mycompany.dodax.model;

import com.mycompany.dodax.Factory.JobFactory;
import com.mycompany.dodax.core.impl.JobManager;
import com.mycompany.dodax.core.impl.JobsRunnable;
import com.mycompany.dodax.model.impl.JobAdd;
import com.mycompany.dodax.model.impl.JobDivide;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dam
 */
public class CompleteTest {
    
    // Testing if the methods of the JobDivide class work correctly
    @Test
    public void testDivide() {
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.add(1);
    list.add(1);
    JobDivide a = new JobDivide();
    a.setNumbersToDivide(list);
    a.execute(); // 1/1
    assertEquals(1,a.getResult());
    
    list.clear();
    list.add(40);
    list.add(10);
    list.add(2);
    a.setNumbersToDivide(list);
    a.execute(); // 40 / 10 / 2 = 5
    assertEquals(5,a.getResult());
    
    }
    //
    // Testing if the methods of the JobAdd class work correctly
    @Test
    public void testJobAdd() {
        JobAdd a = new JobAdd();
        a.setNumbersToAdd(JobFactory.getArrayList45());
        a.execute();
        assertEquals(45,a.getSum());
        
        a.setNumbersToAdd(JobFactory.getArrayList145());
        a.execute();
        assertEquals(145,a.getSum());
    }
    
    // Testing if the add and remove methods of the JobsRunnable class work
    @Test
    public void testJobsRunnable(){
        JobsRunnable t = new JobsRunnable();
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        JobAdd jobAdd = JobFactory.getJobAdd45();
        listOfJobs.add(jobAdd);
        listOfJobs.add(jobAdd);
        
        assertEquals(2,listOfJobs.size());
        
        t.setJobs(listOfJobs);
        t.removeFirstInstanceOfJob(jobAdd);
        assertEquals(1,t.getListOfJobs().size());
        
        t.addJob(getJobDivideByZero());
        assertEquals(2, t.getListOfJobs().size());
        
        t.removeAllJobs();
        assertEquals(0, t.getListOfJobs().size());
        
        jobAdd = JobFactory.getJobAdd45();
        t.addJob(jobAdd);////
        t.addJob(JobFactory.getJobAdd145());
        t.addJob(getJobDivideByZero());
        t.addJob(getJobDivide2());///
        t.addJob(jobAdd);///
        assertEquals(5, t.getListOfJobs().size());
        
        t.removeFirstInstanceOfJob(jobAdd);
        assertEquals(4, t.getListOfJobs().size());
        t.removeFirstInstanceOfJob(jobAdd);
        assertEquals(3, t.getListOfJobs().size());
        
        t.removeLastJob();
        assertEquals(2, t.getListOfJobs().size());
        assertThat(t.getListOfJobs().get(0), instanceOf(JobAdd.class));
        assertThat(t.getListOfJobs().get(1), instanceOf(JobDivide.class));
    }
    
    // Test in which two threads are used to carry out 8 non problematic jobs
    // each thread is assigned 4 jobs by the JobManagers assignJobsToThreads method
    @Test
    public void testExecute1() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        // add 8 JobAdds to the list of jobs which should be executed
        for (int i=0;i<8;++i)
            listOfJobs.add(JobFactory.getJobAdd45());
        
        // Using two threads to execute 8 jobs... so each thread should be doing 4 jobs
        JobManager manager = new JobManager(2,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(manager.getJobsRunnableList().size(),2);
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        // here running is true which means that no thread has thrown an exception
        assertEquals(true,runnableJobs.get(0).getRunning());
        assertEquals(true,runnableJobs.get(1).getRunning());
       
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(45, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        assertEquals(45, firstRunnablesJobs.get(3).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(45, secondRunnablesJobs.get(1).getResult());
        assertEquals(45, secondRunnablesJobs.get(2).getResult());
        assertEquals(45, secondRunnablesJobs.get(3).getResult());
    }
    
    // Test with problematic Job
    // two threads are used to carry out 10 jobs, so each thread is assigned 5
    // the second threads 2 very last jobs are problematic ones (division by zero)
    @Test
    public void testExecute2() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        // add 8 JobAdds to the list of jobs which should be executed
        for (int i=0;i<8;++i)
            listOfJobs.add(JobFactory.getJobAdd45());
        // Add two jobs which will throw an exception (division by zero)
        listOfJobs.add(getJobDivideByZero());
        listOfJobs.add(getJobDivideByZero());
        
        // using two threads to execute 10 jobs; so each thread should be doing 5 jobs
        // since the divide jobs are done last in the second thread, most of the add jobs should be executed
        JobManager manager = new JobManager(2,listOfJobs);
        assertEquals(10,listOfJobs.size());
        
        manager.assignJobsToThreads();
        List<JobsRunnable> l = manager.getJobsRunnableList();
        assertEquals(2,l.size());
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        // make sure the "running" flag has been set to false since division by zero takes place
        // which should cause all the threads to stop executing assigned jobs
        assertEquals(false,runnableJobs.get(0).getRunning());
        assertEquals(false,runnableJobs.get(1).getRunning());
        
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(45, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        assertEquals(45, firstRunnablesJobs.get(3).getResult());
        assertEquals(0, firstRunnablesJobs.get(4).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(45, secondRunnablesJobs.get(1).getResult());
        assertEquals(45, secondRunnablesJobs.get(2).getResult());
        assertEquals(0, secondRunnablesJobs.get(3).getResult());
        assertEquals(0, secondRunnablesJobs.get(4).getResult());
    }
    
    // Test with  problematic Jobs
    // two threads are used to carry out 10 jobs
    // the first threads two first jobs are problematic ones (division by zero)
    // the second problematic job should not be executed anyway, since the first problematic job
    // will cause the threads to stop execution
    @Test
    public void testExecute3() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        listOfJobs.add(getJobDivideByZero()); // get a problematic job
        listOfJobs.add(getJobDivideByZero());
        // then add 8 add jobs which run fine on their own
        for (int i=0;i<8;++i)
            listOfJobs.add(JobFactory.getJobAdd45());
        
        JobManager manager = new JobManager(2,listOfJobs);
        assertEquals(10,listOfJobs.size());
        
        manager.assignJobsToThreads();
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        assertEquals(2,runnableJobs.size());
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        runnableJobs = manager.getJobsRunnableList();
        // should be false, since problematic job throws error and threads stop running
        assertEquals(false,runnableJobs.get(0).getRunning());
        assertEquals(false,runnableJobs.get(1).getRunning());
        
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        assertEquals(0, firstRunnablesJobs.get(0).getResult());
        assertEquals(0, firstRunnablesJobs.get(1).getResult());
        assertEquals(0, firstRunnablesJobs.get(2).getResult());
        assertEquals(0, firstRunnablesJobs.get(3).getResult());
        assertEquals(0, firstRunnablesJobs.get(4).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(0, secondRunnablesJobs.get(1).getResult());
        assertEquals(0, secondRunnablesJobs.get(2).getResult());
        assertEquals(0, secondRunnablesJobs.get(3).getResult());
        assertEquals(0, secondRunnablesJobs.get(4).getResult());
    }
    
    
    // in this test case the numbers of jobs cannot be completely evenly distributed between threads
    // here, two threads are used to execute 9 jobs
    // hence, the first thread does 5 jobs, the other thread does 4 jobs
    @Test
    public void testExecute4() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        // add 9 jobs to the list of jobs to be executed
        for (int i=0;i<9;++i)
            listOfJobs.add(JobFactory.getJobAdd45());
        
        JobManager manager = new JobManager(2,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(2, manager.getJobsRunnableList().size());
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        // first thread does 5 jobs
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(45, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        assertEquals(45, firstRunnablesJobs.get(3).getResult());
        assertEquals(45, firstRunnablesJobs.get(4).getResult());
        
        // second thread does 4 jobs
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(45, secondRunnablesJobs.get(1).getResult());
        assertEquals(45, secondRunnablesJobs.get(2).getResult());
        assertEquals(45, secondRunnablesJobs.get(3).getResult());
    }
    
    // In this test case the numbers again cannot be completely evenly distributed between the threads
    // we are using 4 threads to execute 9 jobs
    @Test
     public void testExecute5() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        for (int i=0;i<9;++i)
            listOfJobs.add(JobFactory.getJobAdd45());
        
        JobManager manager = new JobManager(4,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(manager.getJobsRunnableList().size(),4);
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        // the first thread will be assigned 3 jobs and the rest of the threads will be doing 2 jobs
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(45, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(45, secondRunnablesJobs.get(1).getResult());
        
        JobsRunnable thirdRunnable = runnableJobs.get(2);
        List<IJob> thirdRunnablesJobs = thirdRunnable.getListOfJobs();
        assertEquals(45, thirdRunnablesJobs.get(0).getResult());
        assertEquals(45, thirdRunnablesJobs.get(1).getResult());
        
        JobsRunnable fourthRunnable = runnableJobs.get(3);
        List<IJob> fourthRunnablesJobs = fourthRunnable.getListOfJobs();
        assertEquals(45, fourthRunnablesJobs.get(0).getResult());
        assertEquals(45, fourthRunnablesJobs.get(1).getResult());
     }
     
     // In this test case the numbers again cannot be completely evenly distributed between the threads
     // we are using 4 threads to execute 11 jobs.. 
     // the first three threads will be doing 3 jobs, and the last thread will be doing 2 jobs
     @Test
     public void testExecute5b() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        for (int i=0;i<11;++i)
            listOfJobs.add(JobFactory.getJobAdd45());
        
        JobManager manager = new JobManager(4,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(manager.getJobsRunnableList().size(),4);
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(45, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(45, secondRunnablesJobs.get(1).getResult());
        assertEquals(45, secondRunnablesJobs.get(2).getResult());
        
        JobsRunnable thirdRunnable = runnableJobs.get(2);
        List<IJob> thirdRunnablesJobs = thirdRunnable.getListOfJobs();
        assertEquals(45, thirdRunnablesJobs.get(0).getResult());
        assertEquals(45, thirdRunnablesJobs.get(1).getResult());
        assertEquals(45, thirdRunnablesJobs.get(2).getResult());
        
        JobsRunnable fourthRunnable = runnableJobs.get(3);
        List<IJob> fourthRunnablesJobs = fourthRunnable.getListOfJobs();
        assertEquals(45, fourthRunnablesJobs.get(0).getResult());
        assertEquals(45, fourthRunnablesJobs.get(1).getResult());
     }
     
     
     // testing how 12 error free varying jobs are executed with 3 threads
     // each thread is assinged 4 jobs
     @Test
     public void testExecute6() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        
        JobManager manager = new JobManager(3,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(3, manager.getJobsRunnableList().size());
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
         List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        // eachh thread will be assigned 4 jobs
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(145, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        assertEquals(145, firstRunnablesJobs.get(3).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(145, secondRunnablesJobs.get(1).getResult());
        assertEquals(45, secondRunnablesJobs.get(2).getResult());
        assertEquals(145, secondRunnablesJobs.get(3).getResult());
        
        JobsRunnable thirdRunnable = runnableJobs.get(2);
        List<IJob> thirdRunnablesJobs = thirdRunnable.getListOfJobs();
        assertEquals(45, thirdRunnablesJobs.get(0).getResult());
        assertEquals(145, thirdRunnablesJobs.get(1).getResult());
        assertEquals(45, thirdRunnablesJobs.get(2).getResult());
        assertEquals(145, thirdRunnablesJobs.get(3).getResult());
    }
     
     
     // the last job of the first thread is the problematic job (divison by zero)
     // the first thread also has 1 more job to do than the other 2
     // First thread is assigned 5 jobs, while the other two threads are assigned 4 jobs
     // 3 threads to do 13 jobs
     @Test
     public void testExecute7() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(getJobDivideByZero()); // problematic job added to first thread
        
        JobManager manager = new JobManager(3,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(3, manager.getJobsRunnableList().size());
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        // first thread is assigned 5 jobs while the others are assigned 4 
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(145, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        assertEquals(145, firstRunnablesJobs.get(3).getResult());
        assertEquals(0, firstRunnablesJobs.get(4).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(145, secondRunnablesJobs.get(1).getResult());
        assertEquals(45, secondRunnablesJobs.get(2).getResult());
        assertEquals(145, secondRunnablesJobs.get(3).getResult());
        
        JobsRunnable thirdRunnable = runnableJobs.get(2);
        List<IJob> thirdRunnablesJobs = thirdRunnable.getListOfJobs();
        assertEquals(45, thirdRunnablesJobs.get(0).getResult());
        assertEquals(145, thirdRunnablesJobs.get(1).getResult());
        assertEquals(45, thirdRunnablesJobs.get(2).getResult());
        assertEquals(145, thirdRunnablesJobs.get(3).getResult());
        
        List<JobsRunnable> l = manager.getJobsRunnableList();
        // make sure the "running" flag has been set to false since division by zero takes place
        // which should cause all the threads to stop
        assertEquals(false,l.get(0).getRunning());
        assertEquals(false,l.get(1).getRunning());
        assertEquals(false,l.get(2).getRunning());
    }
     
     // 3 threads to execute 13 jobs; the third job of the second thread is a problmatic one
     @Test
     public void testExecute8() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(getJobDivideByZero());
        listOfJobs.add(JobFactory.getJobAdd45());
        
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        
        listOfJobs.add(JobFactory.getJobAdd145());
        
        JobManager manager = new JobManager(3,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(3, manager.getJobsRunnableList().size());
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println(e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        assertEquals(45, firstRunnablesJobs.get(0).getResult());
        assertEquals(145, firstRunnablesJobs.get(1).getResult());
        assertEquals(45, firstRunnablesJobs.get(2).getResult());
        assertEquals(0, firstRunnablesJobs.get(3).getResult());
        assertEquals(0, firstRunnablesJobs.get(4).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(45, secondRunnablesJobs.get(0).getResult());
        assertEquals(145, secondRunnablesJobs.get(1).getResult());
        assertEquals(0, secondRunnablesJobs.get(2).getResult());
        assertEquals(0, secondRunnablesJobs.get(3).getResult());
        
        JobsRunnable thirdRunnable = runnableJobs.get(2);
        List<IJob> thirdRunnablesJobs = thirdRunnable.getListOfJobs();
        assertEquals(145, thirdRunnablesJobs.get(0).getResult());
        assertEquals(45, thirdRunnablesJobs.get(1).getResult());
        assertEquals(145, thirdRunnablesJobs.get(2).getResult());
        assertEquals(0, thirdRunnablesJobs.get(3).getResult());
        
        // make sure the "running" flag has been set to false since division by zero takes place
        // which should cause all the threads to stop
        assertEquals(false,firstRunnable.getRunning());
        assertEquals(false,secondRunnable.getRunning());
        assertEquals(false,thirdRunnable.getRunning());
    }
     // Test in which the first job of the first thread is a problematic one
     // three threads are assigned to do 12 jobs, so each thread is assigned 4 jobs
     @Test
     public void testExecute9() {
        ArrayList<IJob> listOfJobs = new ArrayList<IJob>();
        listOfJobs.add(getJobDivideByZero());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd145());
        listOfJobs.add(JobFactory.getJobAdd45());
        listOfJobs.add(JobFactory.getJobAdd145());
         
        
        JobManager manager = new JobManager(3,listOfJobs);
        manager.assignJobsToThreads();
        assertEquals(3, manager.getJobsRunnableList().size());
        
        manager.executeJobs();
        List<Thread> tList = manager.getThreads();
        try{
        for (Thread te: tList)
            te.join();
        }catch(Exception e){ 
            System.out.println("In testing method "+e.getMessage());
        }
        
        List<JobsRunnable> runnableJobs = manager.getJobsRunnableList();
        JobsRunnable firstRunnable = runnableJobs.get(0);
        List<IJob> firstRunnablesJobs = firstRunnable.getListOfJobs();
        assertEquals(0, firstRunnablesJobs.get(0).getResult());
        assertEquals(0, firstRunnablesJobs.get(1).getResult());
        assertEquals(0, firstRunnablesJobs.get(2).getResult());
        assertEquals(0, firstRunnablesJobs.get(3).getResult());
        
        JobsRunnable secondRunnable = runnableJobs.get(1);
        List<IJob> secondRunnablesJobs = secondRunnable.getListOfJobs();
        assertEquals(145, secondRunnablesJobs.get(0).getResult());
        assertEquals(0, secondRunnablesJobs.get(1).getResult());
        assertEquals(0, secondRunnablesJobs.get(2).getResult());
        assertEquals(0, secondRunnablesJobs.get(3).getResult());
        
        JobsRunnable thirdRunnable = runnableJobs.get(2);
        List<IJob> thirdRunnablesJobs = thirdRunnable.getListOfJobs();
        assertEquals(145, thirdRunnablesJobs.get(0).getResult());
        assertEquals(0, thirdRunnablesJobs.get(1).getResult());
        assertEquals(0, thirdRunnablesJobs.get(2).getResult());
        assertEquals(0, thirdRunnablesJobs.get(3).getResult());
        
         //  all the threads should have stopped
        assertEquals(false,firstRunnable.getRunning());
        assertEquals(false,secondRunnable.getRunning());
        assertEquals(false,thirdRunnable.getRunning());
    }
     
     
     //returns a JobDivide Object; its execute method would be dividing by zero
     // this method is used to create problematic jobs
     public JobDivide getJobDivideByZero(){
         return new JobDivide(JobFactory.getArrayListDivByZero());
     }
     
     // returns an ArrayList of integers containing the integers: 64, 8, 4 , 2
     // 64/8/4/2 = 1
     public ArrayList<Integer> getArrayListDivide2(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(64);
        list.add(8);
        list.add(4);
        list.add(2);
        return list;
     }
     
     // creates an obJect of JobDivide which has a valid execution (no division by zero)
     public JobDivide getJobDivide2(){
         return new JobDivide(getArrayListDivide2());
     }
     //////////////////////////////////////////////////////////////
     //////////////////////////////////////////////////////////////
}

