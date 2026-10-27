package edu.ju.os.group3.bean;

import java.util.Vector;

public class Link {
    private LinkDirection direction1;
    private LinkDirection direction2;
    private Processor processor1;
    private Processor processor2;
    
    public Link(Processor processor1,Processor processor2) {
        setProcessor1(processor1);
        setProcessor2(processor2);
        initDirections();
        getProcessor1().addLink(getProcessor2().getAddress(), this);
        getProcessor2().addLink(getProcessor1().getAddress(), this);
    }
    
    private void initDirections(){
        if(direction1 == null)
            direction1 = new LinkDirection(getProcessor1(),getProcessor2());
        if(direction2 == null)
            direction2 = new LinkDirection(getProcessor2(),getProcessor1());
    }
    
    
    public void send(Message message){
        if(message.getSenderAddress()==getDirection1().getSender().getAddress() && message.getReceiverAddress()==getDirection1().getReceiver().getAddress())
            getDirection1().send(message);
        if(message.getSenderAddress()==getDirection2().getSender().getAddress() && message.getReceiverAddress()==getDirection2().getReceiver().getAddress())
            getDirection2().send(message);
    }
    
    public Message receive(int receiverAddress,int tag,int senderAddress){
        if(senderAddress==getDirection1().getSender().getAddress() && receiverAddress==getDirection1().getReceiver().getAddress())
            return getDirection1().receive(tag);
        if(senderAddress==getDirection2().getSender().getAddress() && receiverAddress==getDirection2().getReceiver().getAddress())
            return getDirection2().receive(tag);
        return null;
    }
    
    public Message iReceive(int receiverAddress,int tag,int senderAddress){
        if(senderAddress==getDirection1().getSender().getAddress() && receiverAddress==getDirection1().getReceiver().getAddress())
            return getDirection1().iReceive(tag);
        if(senderAddress==getDirection2().getSender().getAddress() && receiverAddress==getDirection2().getReceiver().getAddress())
            return getDirection2().iReceive(tag);
        return null;
    }

    private void setDirection1(LinkDirection direction1) {
        this.direction1 = direction1;
    }

    private LinkDirection getDirection1() {
        return direction1;
    }

    private void setDirection2(LinkDirection direction2) {
        this.direction2 = direction2;
    }

    private LinkDirection getDirection2() {
        return direction2;
    }

    private void setProcessor1(Processor processor1) {
        this.processor1 = processor1;
    }

    private Processor getProcessor1() {
        return processor1;
    }

    private void setProcessor2(Processor processor2) {
        this.processor2 = processor2;
    }

    private Processor getProcessor2() {
        return processor2;
    }
    
    public boolean isNeighborAddress(int address){
        return getProcessor1().getAddress() == address || getProcessor2().getAddress() == address;
    }
}
