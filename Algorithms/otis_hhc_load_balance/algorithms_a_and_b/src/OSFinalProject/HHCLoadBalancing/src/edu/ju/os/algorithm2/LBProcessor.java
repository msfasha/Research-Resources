package edu.ju.os.algorithm2;

import edu.ju.os.group3.Constants;
import edu.ju.os.group3.Timer;
import edu.ju.os.group3.Utility;
import edu.ju.os.group3.bean.Message;
import edu.ju.os.group3.bean.Processor;

public class LBProcessor extends Processor {

    private int dimension;
    private boolean finished;
    private Timer timer;

    public LBProcessor(int address, int weight,int loadUnitSize, int dimension,Timer timer) {
        super(address, weight, loadUnitSize);
        setTimer(timer);
        setDimension(dimension);
    }

    public void run() {
        getTimer().start();
        int deltas[] = null;
        if (Utility.isHexaCoordinator(getAddress()))
            deltas = balanceCoordinator();
        else
            deltas = balanceCorner();
        printArray(deltas);
        balanceTriangle(deltas);
        balanceFullHexa();
        balanceCube();
        finish();
    }

    public void computeDeltas(int delta0, int delta1, int delta2, int deltas0[], int deltas1[], int deltas2[]) {
        if (delta0 > 0) {
            if (delta1 < 0) {
                if (delta0 >= Math.abs(delta1)) {
                    deltas1[0] = delta1;
                    delta0 += delta1;
                    delta1 = 0;

                } else {
                    deltas1[0] = -delta0;
                    delta1 += delta0;
                    delta0 = 0;
                }
            }

            if (delta2 < 0) {
                if (delta0 >= Math.abs(delta2)) {
                    deltas2[0] = delta2;
                    delta0 += delta2;
                    delta2 = 0;

                } else {
                    deltas2[0] = -delta0;
                    delta2 += delta0;
                    delta0 = 0;
                }
            }
            
            deltas0[0] = -deltas1[0];
            deltas0[1] = -deltas2[0];
        }
        /********************************************************************************************************************************/
        if (delta1 > 0) {
            if (delta0 < 0) {
                if (delta1 >= Math.abs(delta0)) {
                    deltas0[0] = delta0;
                    delta1 += delta0;
                    delta0 = 0;
                } else {
                    deltas0[0] = -delta1;
                    delta0 += delta1;
                    delta1 = 0;
                }
            }

            if (delta2 < 0) {
                if (delta1 >= Math.abs(delta2)) {
                    deltas2[1] = delta2;
                    delta1 += delta2;
                    delta2 = 0;

                } else {
                    deltas2[1] = -delta1;
                    delta2 += delta1;
                    delta1 = 0;
                }
            }
            deltas1[0] = -deltas0[0];
            deltas1[1] = -deltas2[1];
        }
        /********************************************************************************************************************************/
        if (delta2 > 0) {
            if (delta0 < 0) {
                if (delta2 >= Math.abs(delta0)) {
                    deltas0[1] = delta0;
                    delta2 += delta0;
                    delta0 = 0;
                } else {
                    deltas0[1] = -delta2;
                    delta0 += delta2;
                    delta2 = 0;
                }
            }

            if (delta1 < 0) {
                if (delta2 >= Math.abs(delta1)) {
                    deltas1[1] = delta1;
                    delta2 += delta1;
                    delta1 = 0;

                } else {
                    deltas1[1] = -delta2;
                    delta1 += delta2;
                    delta2 = 0;
                }
            }

            deltas2[0] = -deltas0[1];
            deltas2[1] = -deltas1[1];
        }

    }
    
    public void balanceTriangle(int deltas[]){
        int neighborAddress1 = 0;
        int neighborAddress2 = 0;
        
        if(Utility.isHexaCoordinator(getAddress())){
            neighborAddress1 = getAddress() + 1;
            neighborAddress2 = getAddress() + 2;
        }
        else{
            if(getAddress()%2==1){
                neighborAddress1 = getAddress()-1;
                neighborAddress2 = getAddress()+1;
            }
            else{
                neighborAddress1 = getAddress()-2;
                neighborAddress2 = getAddress()-1;
            }
        }
        
        balanceEdge(deltas[0],neighborAddress1);
        balanceEdge(deltas[1],neighborAddress2);        
    }
    
    private void balanceEdge(int delta,int neighborAddress){
        if(delta<0){
            
            Message message = receive(neighborAddress, Constants.TAG_TRANSFER_WEIGHT);
            appendLoad((byte[])message.getContent());
        }
        
        if(delta>0){
            byte[] load = cutLoad(delta);
            send(neighborAddress, Constants.TAG_TRANSFER_WEIGHT,load);
        }
        //setWeight(getWeight() - delta);

    }


    public int[] balanceCoordinator() {
        int neighborAddress1 = Utility.getHexaNeighborAddress(getAddress(), 0);
        int neighborAddress2 = Utility.getHexaNeighborAddress(getAddress(), 1);
        int weight0 = getWeight();
        Message message = receive(neighborAddress1, Constants.TAG_HEXA_BALANCE);
        int weight1 = ((Integer)message.getContent()).intValue();
        message = receive(neighborAddress2, Constants.TAG_HEXA_BALANCE);
        int weight2 = ((Integer)message.getContent()).intValue();

        int totalWeight = weight0 + weight1 + weight2;
        int avgWeight = totalWeight / 3;
        int reminder = totalWeight % 3;

        int avg0 = avgWeight;
        if(weight0 > avgWeight && reminder > 0){
            avg0++;
            reminder--;
        }
        int avg1 = avgWeight;

        if(weight1 > avgWeight && reminder > 0){
            avg1++;
            reminder--;
        }

        int avg2 = avgWeight;

        if(weight2 > avgWeight && reminder > 0){
            avg2++;
            reminder--;
        }

        if(reminder > 0){
            if(avg0==avgWeight)
                avg0++;
            else
                if(avg1==avgWeight)
                    avg1++;
                else
                    avg2++;
            reminder--;
        }


        int delta0 = weight0 - avg0;
        int delta1 = weight1 - avg1;
        int delta2 = weight2 - avg2;
        int deltas0[] = { 0, 0 };
        int deltas1[] = { 0, 0 };
        int deltas2[] = { 0, 0 };

        computeDeltas(delta0,delta1,delta2,deltas0,deltas1,deltas2);
        
        send(neighborAddress1, Constants.TAG_HEXA_BALANCE, deltas1);
        send(neighborAddress2, Constants.TAG_HEXA_BALANCE, deltas2);
        /****************************************************************************************/
        return deltas0;
    }


    public int[] balanceCorner() {
        int coordinatorAddress = Utility.getHexaCoordinatorAddress(getAddress());
        int weight = getWeight();
        send(coordinatorAddress, Constants.TAG_HEXA_BALANCE, new Integer(weight));
        Message message = receive(coordinatorAddress, Constants.TAG_HEXA_BALANCE);
        return (int[])message.getContent();
    }

    public void balanceFullHexa() {
        int weight = getWeight();
        int neighborAddress = Utility.getHexaNeighborAddress(getAddress(), 2);
        send(neighborAddress, Constants.TAG_HEXA_BALANCE, new Integer(getWeight()));
        Message message = receive(neighborAddress, Constants.TAG_HEXA_BALANCE);
        int delta =
            balanceHexaProcessor(getAddress() > message.getSenderAddress(), weight, ((Integer)message.getContent()).intValue());
        if (delta > 0) {
            send(neighborAddress, Constants.TAG_TRANSFER_WEIGHT, cutLoad(delta));
        } else {
            if (delta < 0) {
                message = receive(neighborAddress, Constants.TAG_TRANSFER_WEIGHT);
                appendLoad(((byte[])message.getContent()));
            }
        }
    }

    public int balanceHexaProcessor(boolean bigger, int myWeight, int weight) {
        int totalWeight = (myWeight + weight);
        int avgWeight = totalWeight / 2;
        int reminder = totalWeight % 2;
        if (bigger && myWeight > avgWeight)
            avgWeight += reminder;
        if (!bigger && weight <= avgWeight)
            avgWeight += reminder;

        return myWeight - avgWeight;
    }

    public void balanceCube() {
        //printRed(getAddress() + " Started");
        for (int bitIndex = 0; bitIndex < getDimension(); bitIndex++) {
            //printRed(getAddress() + ":" + bitIndex);
            int neighborAddress = Utility.getNeighborAddress(getAddress(), bitIndex + 3);
            int weight = getWeight();
            send(neighborAddress, Constants.TAG_CUBE_BALANCE, new Integer(weight));
            Message message = receive(neighborAddress, Constants.TAG_CUBE_BALANCE);
            int delta =
                balanceHexaProcessor(getAddress() > neighborAddress, weight, ((Integer)message.getContent()).intValue());
            if (delta > 0) {
                send(neighborAddress, Constants.TAG_TRANSFER_WEIGHT, cutLoad(delta));
            } else {
                if (delta < 0) {
                    message = receive(neighborAddress, Constants.TAG_TRANSFER_WEIGHT);
                    appendLoad((byte[])message.getContent());
                }
            }
        }
        //printRed(getAddress() + " Finished");
    }

    public static void printRed(String msg) {
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
    
    public void printArray(int[] deltas){
        //System.out.println(getAddress() + ":" + deltas[0] + ":" + deltas[1]);
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer() {
        return timer;
    }
}
