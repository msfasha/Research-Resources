/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBed;

import ShereenKhojaStemmer.ArabicStemmer;

/**
 *
 * @author Mohammad Fasha
 */
public class KhojaStemmer_Test {

    public static void main(String[] args) {
        ArabicStemmer Stemmer = new ArabicStemmer();
        String ArabicWord = "كتبا";

        System.out.println(Stemmer.StemWord(ArabicWord));
    }

}
