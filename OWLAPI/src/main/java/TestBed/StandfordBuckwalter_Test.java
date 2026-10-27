/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBed;

/**
 *
 * @author Mohammad Fasha
 */
import Stanford.*;
import edu.stanford.nlp.international.arabic.Buckwalter;

public class StandfordBuckwalter_Test {
    public StandfordBuckwalter_Test()
    {
    }
    public static void main (String args[])
    {
        Buckwalter buckwalter = new Buckwalter();
        String str = buckwalter.unicodeToBuckwalter("نَقَلَ ");
        //String str = buckwalter.apply("akala");
        System.out.println(str);
    }
}
