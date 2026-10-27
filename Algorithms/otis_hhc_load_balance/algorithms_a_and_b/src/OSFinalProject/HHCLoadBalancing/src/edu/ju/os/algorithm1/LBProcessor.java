package edu.ju.os.algorithm1;

import edu.ju.os.group3.Constants;
import edu.ju.os.group3.Timer;
import edu.ju.os.group3.Utility;
import edu.ju.os.group3.bean.Message;
import edu.ju.os.group3.bean.Processor;

public class LBProcessor extends Processor{

    private int dimension;
    private boolean finished;
    private Timer timer;

    public LBProcessor(int address,int weight,int loadUnitSize,int dimension,Timer timer) {
        super(address,weight,loadUnitSize);
        setTimer(timer);
        setDimension(dimension);
    }
    
    public void run(){
        getTimer().start();
        if(Utility.isHexaCoordinator(getAddress())){
            //System.out.println("Coordinator");
            balanceCoordinator();
        }
        else{
            //System.out.println("Corner");
            balanceCorner();
        }
        //printRed(""+getAddress());
        balanceFullHexa();
        balanceCube();
        finish();

    }
    
    


    public void balanceCoordinator(){
        int neighborAddress1 = Utility.getHexaNeighborAddress(getAddress(), 0);
        int neighborAddress2 = Utility.getHexaNeighborAddress(getAddress(), 1);

       int weight = getWeight();
        Message message = null;
        while(message == null){
            message = iReceive(neighborAddress1, Constants.TAG_HEXA_COORDINATOR_BALANCE);
            if(message!=null)
                break;
            message = iReceive(neighborAddress2, Constants.TAG_HEXA_COORDINATOR_BALANCE);
        }
        int weights[] = (int[])message.getContent();
        int totalWeight = weight + weights[0] + weights[1];
        int avgWeight = totalWeight/3;
        int reminder = totalWeight%3;
        int deltas[] = {0,0};
        boolean loadedCoordinator = getWeight()>=avgWeight;
        if(loadedCoordinator){
            int myAvg = avgWeight;
            if(reminder>1){
                reminder--;
                myAvg ++;
            }
            if(reminder==1){
                if(weights[0]>weights[1])
                    deltas[1] = 1;
                else
                    deltas[0] = 1;
            }
            
            deltas[0] += avgWeight - weights[0]; 
            deltas[1] += avgWeight - weights[1]; 
            if(deltas[1]<0){
                deltas[1] = 0;
                myAvg--;
            }
            if(deltas[0]<0){
                deltas[0] = 0;
                myAvg--;
            }
        }
        else{
            if(reminder==1){
                if(weights[0]>weights[1])
                    deltas[0] = 1;
                else
                    deltas[1] = 1;
            }

            if(reminder==2){
                deltas[0] = 1;
                deltas[1] = 1;
            }
            deltas[0] = weights[0] - deltas[0] - avgWeight; 
            deltas[1] = weights[1] - deltas[1] - avgWeight; 
        }

        if(loadedCoordinator){
            send(message.getSenderAddress(), Constants.TAG_TRANSFER_WEIGHT, cutLoad(deltas[0]));

            send(message.getSenderAddress() + (message.getSenderAddress()%2==1?1:-1), Constants.TAG_TRANSFER_WEIGHT, cutLoad(deltas[1]));
        }
        else{
            send(message.getSenderAddress(), Constants.TAG_HEXA_COORDINATOR_BALANCE_REPLY, new Integer(Math.abs(deltas[0])));
            send(message.getSenderAddress() + (message.getSenderAddress()%2==1?1:-1), Constants.TAG_HEXA_COORDINATOR_BALANCE_REPLY, new Integer(Math.abs(deltas[1])));

            message = receive(message.getSenderAddress(), Constants.TAG_TRANSFER_WEIGHT);
            appendLoad((byte[])message.getContent());

            message = receive(message.getSenderAddress() + (message.getSenderAddress()%2==1?1:-1), Constants.TAG_TRANSFER_WEIGHT);
            appendLoad((byte[])message.getContent());
        }
        /****************************************************************************************/
    }

    
    public void balanceCorner(){
        int neighborAddress = getAddress() + (getAddress()%2==1?1:-1);
        int coordinatorAddress = Utility.getHexaCoordinatorAddress(getAddress());
        int weight = getWeight();
        send(neighborAddress,Constants.TAG_HEXA_BALANCE,new Integer(weight));
        Message message = receive(neighborAddress, Constants.TAG_HEXA_BALANCE);
        int delta = balanceHexaProcessor(getAddress()>message.getSenderAddress(),weight,((Integer)message.getContent()).intValue());


        if(delta>0){
            send(neighborAddress, Constants.TAG_TRANSFER_WEIGHT, cutLoad(delta));
            
        }
        else{
            if(delta<0 || (delta==0&&getAddress()%2==0)){
                send(coordinatorAddress, Constants.TAG_HEXA_COORDINATOR_BALANCE, new int[]{weight-delta,((Integer)message.getContent()).intValue()+delta});         
                if(delta!=0){
                    message = receive(neighborAddress, Constants.TAG_TRANSFER_WEIGHT);
                    appendLoad((byte[])message.getContent());
                }
            }
        }

        delta  = 0;
        
        message = null;
        boolean needsLoad = false;
        message = receive(coordinatorAddress,Constants.TAG_ANY);
        needsLoad = message.getTag() == Constants.TAG_HEXA_COORDINATOR_BALANCE_REPLY;
        /*
        while(message == null){
            message = iReceive(coordinatorAddress, Constants.TAG_HEXA_COORDINATOR_BALANCE_REPLY);
            if(message!=null){
                needsLoad = true;
                break;
            }
            message = iReceive(coordinatorAddress, Constants.TAG_TRANSFER_WEIGHT);
        }
*/
        
        if(needsLoad){
            send(coordinatorAddress, Constants.TAG_TRANSFER_WEIGHT, cutLoad(((Integer)message.getContent()).intValue()));
        }
        else
            appendLoad((byte[])message.getContent());
        /****************************************************************************************/
    }
    
    public void balanceFullHexa(){
        int weight = getWeight();
        int neighborAddress = Utility.getHexaNeighborAddress(getAddress(), 2);
        send(neighborAddress, Constants.TAG_HEXA_BALANCE, new Integer(getWeight()));
        Message message = receive(neighborAddress, Constants.TAG_HEXA_BALANCE);
        int delta = balanceHexaProcessor(getAddress()>message.getSenderAddress(),weight,((Integer)message.getContent()).intValue());
        if(delta>0){
            send(neighborAddress, Constants.TAG_TRANSFER_WEIGHT, cutLoad(delta));
        }
        else{
            if(delta<0){
                message = receive(neighborAddress, Constants.TAG_TRANSFER_WEIGHT);
                appendLoad((byte[])message.getContent());
            }
        }
    }

    public int balanceHexaProcessor(boolean bigger,int myWeight,int weight){
        int totalWeight = (myWeight + weight);
        int avgWeight = totalWeight/2;
        int reminder = totalWeight%2;
        if(bigger&&myWeight>avgWeight)
            avgWeight += reminder;
        if(!bigger&&weight<=avgWeight)
            avgWeight += reminder;

        return myWeight-avgWeight;
    }
    
    public void balanceCube(){
        //printRed(getAddress() + " Started");
        for(int bitIndex=0;bitIndex<getDimension();bitIndex++){
            //printRed(getAddress() + ":" + bitIndex);
            int neighborAddress = Utility.getNeighborAddress(getAddress(),bitIndex+3);
            int weight = getWeight();
            send(neighborAddress, Constants.TAG_CUBE_BALANCE, new Integer(weight));
            Message message = receive(neighborAddress, Constants.TAG_CUBE_BALANCE);
            int delta = balanceHexaProcessor(getAddress()>neighborAddress, weight, ((Integer)message.getContent()).intValue());
            if(delta>0){
                send(neighborAddress, Constants.TAG_TRANSFER_WEIGHT,cutLoad(delta));
            }
            else{
                if(delta<0){
                    message = receive(neighborAddress, Constants.TAG_TRANSFER_WEIGHT);
                    appendLoad((byte[])message.getContent());
                }
            }
        }
        //printRed(getAddress() + " Finished");
    }
    
    public static void printRed(String msg){
        System.err.println(msg);
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }

    public void finish() {
        timer.end();
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer() {
        return timer;
    }
    
    public int getSteps(){
        if(Utility.isHexaCoordinator(getAddress()))
            return super.getSteps()+3;

        return super.getSteps();
    }
}
