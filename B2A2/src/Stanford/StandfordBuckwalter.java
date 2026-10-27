/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Stanford;

/**
 *
 * @author technical.support
 */
import edu.stanford.nlp.international.arabic.Buckwalter;

public class StandfordBuckwalter {

    Buckwalter buckwalter;

    public StandfordBuckwalter() {
        buckwalter = new Buckwalter();
    }

    public String ArabicToBuckwalter(String paraArabicString) {
        return buckwalter.unicodeToBuckwalter(paraArabicString);
    }

    public String BuckwalterToArabic(String paraBuckwalterString) {
        return buckwalter.buckwalterToUnicode(paraBuckwalterString);
    }
}
