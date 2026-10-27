package edu.ju.os.group3.bean;

import edu.ju.os.group3.Constants;

import java.io.ByteArrayOutputStream;

import java.util.Hashtable;
import java.util.Vector;

public class Processor extends Thread{
    private int address;
    //private int weight;
    private int loadUnitSize;
    private byte[] load;
    private Hashtable links;
    
    private int steps;

    public Processor(int address,int weight,int loadUnitSize) {
        setAddress(address);
        //setWeight(weight);
        setSteps(0);
        setLoadUnitSize(loadUnitSize);
        initLoad(weight);
        initLinks();
    }
    
    private void initLinks(){
        if(links == null)
            links = new Hashtable();
        links.clear();
    }
    
    private void initLoad(int weight){
        load = new byte[getTotalSize(weight)];
    }
    
    public void appendLoad(byte[]newLoad){
        int size = load.length + newLoad.length;
        byte data[] = new byte[size];
        System.arraycopy(load, 0, data, 0, load.length);
        System.arraycopy(newLoad, 0, data, load.length, newLoad.length);
        load = null;
        System.gc();
        newLoad = null;
        System.gc();
        load = data;
    }

    public byte[] cutLoad(int size){
        size *= getLoadUnitSize();
        if(size<0)
            System.out.println(size);
        byte data[] = new byte[size];
        byte reminder[] = new byte[load.length-size];
        System.arraycopy(load, 0, data, 0, size);
        System.arraycopy(load, size, reminder, 0, reminder.length);
        load = null;
        System.gc();
        load = reminder;
        return data;
    }
    
    public void addLink(int neighborAddress,Link link){
        links.put(String.valueOf(neighborAddress),link);
    }
    
    public Link getLink(int neighborAddress){
        return (Link)links.get(String.valueOf(neighborAddress));
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
    
    public int getHexaAddress(){
        return getAddress()%8;
    }
    

    /*public void setWeight(int weight) {
        this.weight = weight;
    }*/

    public int getWeight() {
        return load.length/getLoadUnitSize();
    }
    
    public Message receive(int address,int tag){
        Message message = getLink(address).receive(getAddress(),tag,address);
        setSteps(steps+1);
        return message;
    }

    public Message iReceive(int address,int tag){
        Message message = getLink(address).iReceive(getAddress(),tag,address);
        if(message != null)
            setSteps(steps+1);
        return message;
    }

    public void send(int address,int tag,Object message){
        getLink(address).send(new Message(getAddress(),address,tag,message));
        setSteps(steps+1);
    }
    
    public int getLinksCount(){
        return links.size();
    }
    
    public void print(){
        System.out.println(getAddress() + ":" + getWeight());
    }

    public void setLoadUnitSize(int loadUnitSize) {
        this.loadUnitSize = loadUnitSize;
    }

    public int getLoadUnitSize() {
        return loadUnitSize;
    }
    
    public int getTotalSize(int weight){
        return getLoadUnitSize()*weight;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getSteps() {
        return steps;
    }
    
    public long getSpeed(long linkSpeed){
        return getSteps()*linkSpeed;
    }
}
