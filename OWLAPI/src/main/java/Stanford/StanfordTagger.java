/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Stanford;

import Utility.Globals;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 *
 * @author Mohammad Fasha
 */
public class StanfordTagger {

    MaxentTagger tagger;

    public StanfordTagger() {
        tagger = new MaxentTagger(Globals.TaggersModel);       
    }

    public String TagString(String paraString) {
        return tagger.tagString(paraString);
    }
}
