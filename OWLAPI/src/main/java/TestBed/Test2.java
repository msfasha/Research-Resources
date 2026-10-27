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
import java.util.List;
import java.io.StringReader;


import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Test2 {

    static LexicalizedParser lp;

    public static void main(String[] args) {
        String parserModel = "D:\\PHD Software\\Arabic APIs\\stanford-parser-3.6.0-models\\edu\\stanford\\nlp\\models\\lexparser\\arabicFactored.ser.gz";

        lp = LexicalizedParser.loadModel(parserModel);
        System.out.println(ParseString("سنضربهم يقوة"));
    }

    public static String ParseString(String paraString) {

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
    
        // You can also use a TreePrint object to print trees and dependencies
        TreePrint tp;
        tp = new TreePrint("penn,typedDependenciesCollapsed");
        tp.printTree(parse,pw);
        
        return sw.toString();
    }
}
