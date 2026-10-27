package edu.ju.os.group3;

public class Utility {

    public static int getHexaAddress(int address){
        return address%8;
    }


    public static int getCellAddress(int address){
        return address/8;
    }

    public static boolean isValidHexaAddress(int address){
        int no = getHexaAddress(address);
        return no!=3 && no<7 && no>=0;
    }

    public static boolean isHexaCoordinator(int address){
        int no = getHexaAddress(address);
        return no%4 == 0;
    }


    public static int getHexaNeighborAddress(int address,int bitIndex){
        int cellAddress = getCellAddress(address);
        address = getHexaAddress(address);
        int no = (int)Math.pow(2, bitIndex);
        no =  address ^ no;
        if(no == 3 || no == 7)
            no --;
        
        if(no == address)
            no--;
        
        return no+cellAddress*8;
    }


    public static int getNeighborAddress(int address,int bitIndex){
        int no = (int)Math.pow(2, bitIndex);
        no =  address ^ no;
        return no;
    }
    
    public static int getHexaCoordinatorAddress(int address){
        return address - (address%4);
    }
}
