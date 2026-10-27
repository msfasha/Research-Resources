package edu.ju.os.group3;

import java.util.Date;

public class Timer {
    private Date start;
    private Date end;
    private int size;
    private int finished;
    
    public Timer(int size) {
        setSize(size);
        setFinished(0);
    }

    public void start() {
        start = new Date();
    }

    public Date getStart() {
        return start;
    }

    public synchronized void end() {
        setFinished(getFinished()+1);
        if(getFinished()==getSize())
            end = new Date();
    }
    
    public long getTime(){
        try{
            return getEnd().getTime()-getStart().getTime();
        }
        catch(Exception ex){
        }
        return -1;
    }

    public Date getEnd() {
        return end;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getFinished() {
        return finished;
    }
}
