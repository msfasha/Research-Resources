package edu.ju.os.group3.bean;

public class Message {
    private int senderAddress;
    private int receiverAddress;
    private Object content;
    private int tag;
    public Message(int senderAddress,int receiverAddress,int tag,Object content) {
        setSenderAddress(senderAddress);
        setReceiverAddress(receiverAddress);
        setTag(tag);
        setContent(content);
    }

    public void setSenderAddress(int senderAddress) {
        this.senderAddress = senderAddress;
    }

    public int getSenderAddress() {
        return senderAddress;
    }

    public void setReceiverAddress(int receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getReceiverAddress() {
        return receiverAddress;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Object getContent() {
        return content;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

}
