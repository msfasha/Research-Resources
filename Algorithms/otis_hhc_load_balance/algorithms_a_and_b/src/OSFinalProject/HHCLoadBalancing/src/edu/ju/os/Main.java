package edu.ju.os;

import edu.ju.os.algorithm2.LBProcessor;
import edu.ju.os.group3.Timer;
import edu.ju.os.group3.bean.HyberCube;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;


public class Main {
    public static void main(String[] args) {
        int choice = 0;
        try{
            choice = Integer.parseInt(JOptionPane.showInputDialog("Select Your Choice:\n1)Experiment 1 (Execution Time of Algorithms A and B for constant dimension & variable load size)\n" + 
            "2)Experiment 2 (Execution Time of Algorithms A and B for constant load size & variable dimensions)\n" + 
            "3)Experiment 3 (Speed of Algorithms A and B for variable dimensions)\n"+
            "Otherwise) Exit"));
        }
        catch(Exception ex){
        }
        switch(choice){
        case 1:
            experiment1();
            break;
        case 2:
            experiment2();
            break;
        case 3:
            experiment3();
            break;
        }
        System.exit(0);
    }
    
    public static void experiment1(){
        
        int hhcDimension = 0;
        try{
            hhcDimension = Integer.parseInt(JOptionPane.showInputDialog("HHC Dimension:"));
            if(hhcDimension<=0)
                return;
        }
        catch(Exception ex){
            return;
        }
        System.out.println("Load Size\tAlgorithm A\tAlgorithm B");
        for(int s=1;s<=5;s++)
            System.out.println((int)Math.pow(10, s) + "\t" + executeAlgorithm1((int)Math.pow(10, s),hhcDimension)[0]+ "\t" + executeAlgorithm2((int)Math.pow(10, s),hhcDimension)[0]);
    }


    public static void experiment2(){
        
        int maxLoad = 0;
        try{
            maxLoad = Integer.parseInt(JOptionPane.showInputDialog("Enter Max Load/Node:"));
            if(maxLoad<=0)
                return;
        }
        catch(Exception ex){
            return;
        }
        System.out.println("Dimension\t\tAlgorithm A\tAlgorithm B");
        for(int d=1;d<=5;d++)
            System.out.println(d + "\t\t" + executeAlgorithm1(maxLoad,d)[0]+ "\t\t" + executeAlgorithm2(maxLoad,d)[0]);
    }

    public static void experiment3(){
        
        int maxLoad = 10;
        System.out.println("Dimension\t\tAlgorithm A\tAlgorithm B");
        for(int d=1;d<=8;d++)
            System.out.println(d + "\t\t" + executeAlgorithm1(maxLoad,d)[1]+ "\t\t" + executeAlgorithm2(maxLoad,d)[1]);
    }

    public static long[] executeAlgorithm1(int load,int d) {
        int count = (int)Math.pow(2,d-1)*6;
        int loadUnitSize = 1;
        List processors = new ArrayList();
        int max = load*count;
        
        for(int i=0;i<count;i++){
            int weight = 0;
            if(i==1)
                weight = max;
            processors.add(new edu.ju.os.algorithm1.LBProcessor(i,weight,loadUnitSize,d-1,new Timer(1)));
        }
        HyberCube cube = new HyberCube(processors,d-1,250*1024*1024);
        
        cube.run();
        outter:
        while(true){
            for(int i=0;i<cube.getCells().length;i++){
                processors = cube.getCells()[i].getProcessors();
                for(int j=0;j<processors.size();j++){
                    if(!((edu.ju.os.algorithm1.LBProcessor)processors.get(j)).isFinished())
                        continue outter;
                }
            }
            break;
        }
        
        long totalTime = 0;

        for(int i=0;i<cube.getCells().length;i++){
            processors = cube.getCells()[i].getProcessors();
            for(int j=0;j<processors.size();j++){
                if(totalTime<((edu.ju.os.algorithm1.LBProcessor)processors.get(j)).getTimer().getTime())
                    totalTime = ((edu.ju.os.algorithm1.LBProcessor)processors.get(j)).getTimer().getTime();
            }
        }

        
        return new long[]{totalTime,cube.getSpeed()};
    }


    public static long[] executeAlgorithm2(int load,int d) {
        int count = (int)Math.pow(2,d-1)*6;
        int loadUnitSize = 1;
        List processors = new ArrayList();
        int max = load*count;
        
        for(int i=0;i<count;i++){
            int weight = 0;
            if(i==0)
                weight = max;
            processors.add(new edu.ju.os.algorithm2.LBProcessor(i,weight,loadUnitSize,d-1,new Timer(1)));
        }
        HyberCube cube = new HyberCube(processors,d-1,250*1024*1024);
        
        cube.run();
        outter:
        while(true){
            for(int i=0;i<cube.getCells().length;i++){
                processors = cube.getCells()[i].getProcessors();
                for(int j=0;j<processors.size();j++){
                    if(!((edu.ju.os.algorithm2.LBProcessor)processors.get(j)).isFinished())
                        continue outter;
                }
            }
            break;
        }
        
        long totalTime = 0;

        for(int i=0;i<cube.getCells().length;i++){
            processors = cube.getCells()[i].getProcessors();
            for(int j=0;j<processors.size();j++){
                if(totalTime<((edu.ju.os.algorithm2.LBProcessor)processors.get(j)).getTimer().getTime())
                    totalTime = ((edu.ju.os.algorithm2.LBProcessor)processors.get(j)).getTimer().getTime();
            }
        }

        
        return new long[]{totalTime,cube.getSpeed()};
    }

}
