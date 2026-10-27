package edu.ju.os.group3.bean;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class HexaCell{

    //public static final int BIT_INDEX[][]={{2,1,0},{1,2,0},{0,1,2}};    
    //public static final int WEIGHTS[]={19,31,12,24,50,86};
        //{19,31,12,24,50,86};//70,50,30,60,20,40};    
    private int address;
    private List processors;

    
    public HexaCell(int address,List allProcessors) {
        setAddress(address);
        apply(allProcessors);
    }
    
    public void apply(List allProcessors){
        processors = new ArrayList();
        for(int i=0;i<6;i++){
            Processor p = (Processor)allProcessors.get(0);
            processors.add(p);
            int index = i;
            if(i>=3)
                index++;
            p.setAddress(getAddress()*8 + index);
            allProcessors.remove(0);
        }
        
        new Link((Processor)processors.get(0),(Processor)processors.get(1));
        new Link((Processor)processors.get(0),(Processor)processors.get(2));
        new Link((Processor)processors.get(0),(Processor)processors.get(3));

        new Link((Processor)processors.get(1),(Processor)processors.get(2));
        new Link((Processor)processors.get(1),(Processor)processors.get(4));
        
        new Link((Processor)processors.get(2),(Processor)processors.get(5));

        new Link((Processor)processors.get(3),(Processor)processors.get(4));
        new Link((Processor)processors.get(3),(Processor)processors.get(5));

        new Link((Processor)processors.get(4),(Processor)processors.get(5));

        
    }
    
    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
    
    public List getProcessors() {
        return processors;
    }
    
    
    public void print(){
        for(int i=0;i<6;i++){
            Processor processor = (Processor)getProcessors().get(i);
            System.out.println(processor.getAddress() + "\t" + processor.getWeight());// + ":" + getProcessor(address).getError());
        }
    }
    
    public void run(){
        for(int i=0;i<getProcessors().size();i++){
            ((Processor)getProcessors().get(i)).start();
        }
    }
    
}
