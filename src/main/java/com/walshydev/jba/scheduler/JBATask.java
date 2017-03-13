package com.walshydev.jba.scheduler;

public abstract class JBATask implements Runnable {

    private String taskName;

    public JBATask(String taskName){
        this.taskName = taskName;
    }

    public JBATask(){
        this.taskName = "JBATask-" + System.currentTimeMillis();
    }

    public void delay(long delay){
        Scheduler.delayTask(this, delay);
    }

    public boolean repeat(long delay, long interval){
        return Scheduler.scheduleRepeating(this, taskName, delay, interval);
    }

    public boolean cancel(){
        return Scheduler.cancelTask(taskName);
    }
}
