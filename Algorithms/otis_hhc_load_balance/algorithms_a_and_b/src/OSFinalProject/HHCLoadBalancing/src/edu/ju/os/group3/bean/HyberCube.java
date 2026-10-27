package edu.ju.os.group3.bean;

import edu.ju.os.group3.Utility;

import java.util.List;
import java.util.Vector;

public class HyberCube{
    
    private int dimension;
    private HexaCell cells[];
    
    private long linkSpeed;
    
    public HyberCube(List processors,int dimension,long linkSpeed) {
        setDimension(dimension);
        setLinkSpeed(linkSpeed);
        initCells(processors);
    }
    
    private void initCells(List processors){
        cells = new HexaCell[getSize()];
        for(int i=0;i<cells.length;i++)
            
            cells[i] = new HexaCell(i,processors);    

        for(int i=0;i<cells.length;i++){
            int[] neighbors = getNeighbors(i);
            for(int j=0;j<neighbors.length;j++){
                if(neighbors[j]>i)
                    connect(cells[i],cells[neighbors[j]]);
            }
        }
        
    }
    
    public int[] getNeighbors(int address){
        int neighbors[] = new int[getDimension()];
        for(int i=0;i<neighbors.length;i++){
            neighbors[i] = Utility.getNeighborAddress(address,i);
        }
        return neighbors;
    }
    
    private void connect(HexaCell cell1,HexaCell cell2){
        for(int i=0;i<6;i++)
            new Link((Processor)cell1.getProcessors().get(i),(Processor)cell2.getProcessors().get(i));
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }
    
    public int getSize(){
        return (int)Math.pow(2, getDimension());
    }


    public HexaCell[] getCells() {
        return cells;
    }
    
    
    public void print(){
        HexaCell cells[] = getCells();
        for(int i=0;i<cells.length;i++){
            cells[i].print();
        }
    }
    
    public void run(){
        HexaCell cells[] = getCells();
        for(int i=0;i<cells.length;i++)
            cells[i].run();
    }

    public void setLinkSpeed(long linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public long getLinkSpeed() {
        return linkSpeed;
    }
    
    public long getSpeed(){
        long speed = 0;
        for(int i=0;i<getCells().length;i++){
            List processors = getCells()[i].getProcessors();
            for(int j=0;j<processors.size();j++){
                long processorSpeed = ((Processor)processors.get(j)).getSpeed(getLinkSpeed());
                if(processorSpeed>speed)
                    speed = processorSpeed;
            }
        }
        return speed;
    }
}
