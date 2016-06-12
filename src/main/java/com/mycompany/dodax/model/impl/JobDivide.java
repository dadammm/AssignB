package com.mycompany.dodax.model.impl;

import com.mycompany.dodax.model.IJob;
import java.util.ArrayList;
/**
 *
 * Class created for test purposes
 * This class implements IJob, and the execute method divides the numbers of an arrayList containing integers
 * and stores that value in the result variable
 * This class is often used in test cases to generate Exceptions (division by zero)
 */
public class JobDivide implements IJob{
    private ArrayList<Integer> arrayList;
    private int result;
    
    public JobDivide(){}
    
    public JobDivide(ArrayList<Integer> arrayList){
        this.arrayList = arrayList;
    }
    
    @Override
    public void execute() {
        //result = 1;
        for(int i=0;i<arrayList.size()-1;++i){
            result = arrayList.get(i)/arrayList.get(i+1);
        }
    }
    
    @Override
    public int getResult(){
        return result;
    }
    
    public void setNumbersToDivide(ArrayList<Integer> arrayList){
        this.arrayList = arrayList;
    }
}
