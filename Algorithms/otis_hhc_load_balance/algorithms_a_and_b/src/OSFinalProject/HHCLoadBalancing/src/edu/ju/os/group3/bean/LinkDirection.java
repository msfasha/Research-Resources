package edu.ju.os.group3.bean;

import edu.ju.os.group3.Constants;

import java.util.Vector;

public class LinkDirection {
    private Vector buffer;
    private Processor receiver;
    private Processor sender;
    
    public LinkDirection(Processor sender,Processor receiver) {
        setSender(sender);
        setReceiver(receiver);
        initBuffer();
    }
    
    public void initBuffer(){
        if(buffer == null)
            buffer = new Vector();
        buffer.clear();
    }
    
    
    public synchronized void send(Message message){
        buffer.add(message);
        notifyAll();
    }
    
    public synchronized Message receive(int tag){
        Message message = null;
        while(true){
            if(tag == Constants.TAG_ANY){
                if(buffer.size()>0){
                    message = (Message)buffer.get(0);
                }
            }else{
                for(int i=0;i<buffer.size();i++)
                    if(((Message)buffer.get(i)).getTag() == tag){
                        message = (Message)buffer.get(i);
                        break;
                    }
            }
            if(message==null){
                try {
                    wait();
                } catch (Exception e) {
                }
            }
            else
                break;
        }
        
        buffer.remove(message);    
        
        notifyAll();
        
        return message;
    }

    public synchronized Message iReceive(int tag){
        Message message = null;

        for(int i=0;i<buffer.size();i++)
            if(((Message)buffer.get(i)).getTag() == tag){
                message = (Message)buffer.get(i);
                break;
            }

        if(message!=null)
            buffer.remove(message);    

        return message;
    }

    public void setReceiver(Processor receiver) {
        this.receiver = receiver;
    }

    public Processor getReceiver() {
        return receiver;
    }

    public void setSender(Processor sender) {
        this.sender = sender;
    }

    public Processor getSender() {
        return sender;
    }
}
