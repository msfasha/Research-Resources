/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBed;

import ArabicWordNet.AWN;
import java.util.List;

/**
 *
 * @author Mohammad Fasha
 */
public class ArabicWordNet_Test {

    public static void main(String[] args) {
        try {
            AWN awn = new AWN("D:\\OneDrive\\Thesis\\PHD Research\\AWN Database\\awn.xml", false);

//            List<String> value = awn.Get_All_Form_Value();            
//            for (String s:value)                
//            System.out.println(s);
            String str = awn.Get_Name_Of_Item_From_Item_Id("$Ahd");
            if (str == null) {
                System.out.println("null");
            } else {
                System.out.println("str value is : " + str);
            }

            List<String> ItemID = awn.Get_Item_Id_From_Name("أكل");
            for (String s : ItemID) {
                System.out.println("Itemd Id: " + s);
                System.out.println(awn.Get_Name_Of_Item_From_Item_Id(s));
            }

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

}
