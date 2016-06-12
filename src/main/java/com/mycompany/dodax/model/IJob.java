package com.mycompany.dodax.model;

public interface IJob {
    public void execute();
    public int getResult(); // method needed for test purposes (to see if the execute method has worked correctly)
}
