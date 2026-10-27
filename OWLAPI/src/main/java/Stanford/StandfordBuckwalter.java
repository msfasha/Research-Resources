/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Stanford;

/**
 *
 * @author Mohammad Fasha
 */
import edu.stanford.nlp.international.arabic.Buckwalter;

public class StandfordBuckwalter {

    Buckwalter buckwalter;

    public StandfordBuckwalter() {
        buckwalter = new Buckwalter();
    }

    public String ArabicToBuckwalter(String paraArabicString) {
        String str = buckwalter.unicodeToBuckwalter(paraArabicString);

        //We witnessed an error in the OWL ontology population, that is 
        //when the Buckwalter transliteration contains the { character or the ئا
        //the ontology will fail to load in Protege
        str = str.replace("}", "2");

        return str;
    }

    public String BuckwalterToArabic(String paraBuckwalterString) {
        String str = buckwalter.buckwalterToUnicode(paraBuckwalterString);
        str = str.replace("2", "ئ");

        return str;
    }
}
