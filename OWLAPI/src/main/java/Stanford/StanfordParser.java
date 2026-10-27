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
import Utility.Globals;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StanfordParser {

    LexicalizedParser lp;

    public StanfordParser() {        
        lp = LexicalizedParser.loadModel(Globals.ParserPath);
    }

    public String ParseString(String paraString) {               
        
        TokenizerFactory<CoreLabel> tokenizerFactory
                = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
        Tokenizer<CoreLabel> tok
                = tokenizerFactory.getTokenizer(new StringReader(paraString));
        List<CoreLabel> rawWords2 = tok.tokenize();
        Tree parse = lp.apply(rawWords2);
        
        //parse.pennPrint();
        //System.out.println(parse.toString());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
    
        //You can also use a TreePrint object to print trees and dependencies
        TreePrint tp;
        tp = new TreePrint("penn,typedDependenciesCollapsed");
        tp.printTree(parse,pw);
        
        return sw.toString();
    }
}
