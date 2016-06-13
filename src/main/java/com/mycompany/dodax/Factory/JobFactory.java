/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dodax.Factory;

import com.mycompany.dodax.model.impl.JobAdd;
import com.mycompany.dodax.model.impl.JobDivide;
import java.util.ArrayList;

/**
 *
 * @author dam
 */
public class JobFactory {
    // returns an Object of JobAdd with its arrayList containing numbers that add up to 45 
     public static JobAdd getJobAdd45(){
         return new JobAdd(getArrayList45());
     }
     
     // return an arraylist, whose members add up to 45 when added together
     // 10+11+12+...+19 = 145
     public static ArrayList<Integer> getArrayList145(){
	ArrayList<Integer> list = new ArrayList<Integer>();
	for (int j=10;j<20;++j)
            list.add(j);
        return list;	
    }
     
     ///////////////////////////////////////////////////////////////
     // HELPER METHODS
     //////////////////////////////////////////////////////////////
     
     // return an arraylist, whose members add up to 45 when added together
     // 1+2+3+...+9 = 45
     public static ArrayList<Integer> getArrayList45(){
         ArrayList<Integer> list = new ArrayList<Integer>();
            for (int j=0;j<10;++j)
                list.add(j);
         return list;
     }
     
     
     
     // returns an Object of JobAdd with its arrayList containing numbers that add up to 145 
     public static JobAdd getJobAdd145(){
         return new JobAdd(getArrayList145());
     } 
    //////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////
    //returns an arraylist of integers containing two zeroes. 
     public static ArrayList<Integer> getArrayListDivByZero(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(0);
        list.add(0);
        return list;
     }
     
     //returns a JobDivide Object; its execute method would be dividing by zero
     // this method is used to create problematic jobs
     public static JobDivide getJobDivideByZero(){
         return new JobDivide(JobFactory.getArrayListDivByZero());
     }
     
     // returns an ArrayList of integers containing the integers: 64, 8, 4 , 2
     // 64/8/4/2 = 1
     public static ArrayList<Integer> getArrayListDivide2(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(64);
        list.add(8);
        list.add(4);
        list.add(2);
        return list;
     }
     
     // creates an obJect of JobDivide which has a valid execution (no division by zero)
     public static JobDivide getJobDivide2(){
         return new JobDivide(getArrayListDivide2());
     }
     //////////////////////////////////////////////////////////////
     //////////////////////////////////////////////////////////////
}
