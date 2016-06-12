package com.mycompany.dodax.model.impl;

import com.mycompany.dodax.model.IJob;
import java.util.ArrayList;
/**
 *
 * Class created for test purposes
 * This class implements IJob, and the execute method adds the numbers of an arrayList containing integers
 * and stores that value in the sum variable
 */
public class JobAdd implements IJob{
    private ArrayList<Integer> arrayList; // contains the numbers to add
    private int sum;
    
    public JobAdd(){}
    
    public JobAdd(ArrayList<Integer> l){
        this.arrayList = l;
    }
    
    @Override
    public void execute() {
        sum =0;
        for (Integer n: arrayList)
            sum+=n;
    }
    @Override
    public int getResult(){
        return sum;
    }
    
    public int getSum(){
        return sum;
    }
    
    public void setNumbersToAdd(ArrayList<Integer> arrayList){
        this.arrayList = arrayList;
    }
    
}
