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
public class TestArabicChar {

public static void main(String[] args)
{
    String pre = "اهدت";
pre = pre + " ";
String mid1 = "اليوم\\NNP";

    String full = pre + mid1 ;

    javax.swing.JOptionPane.showMessageDialog (

null, full);

    }

}
