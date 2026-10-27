/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 *
 * @author technical.support
 */
public class Globals {

    public static final char TokenOpenBracket = 40; // (
    public static final char TokenCloseBracket = 41; //)
    public static final char TokenConcatinationChar = 43; //+
    public static final char RegularExpressionReadyTokenOpenBracket = 60; // <
    public static final char RegularExpressionReadyTokenCloseBracket = 62;// >

    public static final char SegmentOpenBracket = 123; // {
    public static final char SegmentCloseBracket = 125;// }
    public static final char RegularExpressionReadySegmentOpenBracket = 171; // «
    public static final char RegularExpressionReadySegmentCloseBracket = 187;// »
    public static final char RegularExpressionReadyTSegmentConcatinationChar = 95;// _
    public static final char ItemOrderSeperator = 35;// #

    public static final String RegexPattern_Event = "Event";
    public static final String RegexPattern_ISTheAgentOfEvent = "ISTheAgentOfEvent";
    public static final String RegexPattern_Agent = "Agent";    

    //linguistic patterns, resolved with the assistance of DL
    public static final String RegexPattern_SFXOBJ = "SFX_OBJ";
    public static final String RegexPattern_Nouns = "Nouns";

    public static final String ConceptConcrete = "ConcreteConcept";

    public static final String OntologyDefaultDirectory = "D:\\OneDrive\\Thesis\\PHD Research\\Ontology\\";

    public static final String CorpusFilesPath = "D:\\OneDrive\\Thesis\\PHD Research\\Children Stories";
    public static final String TaggersModel = "D:\\PHD Software\\models\\arabic.tagger";
    public static final String ParserPath = "D:\\PHD Software\\Arabic APIs\\stanford-parser-3.6.0-models\\edu\\stanford\\nlp\\models\\lexparser\\arabicFactored.ser.gz";

    public static String StripTextKeepPOSTags(String paraString) {
        paraString = paraString.replace("/", "x");
        paraString = paraString.replaceAll("[^a-zA-Z]", "");
        paraString = paraString.replace("x", " ");
        return paraString;
    }

    public static String StripPOSTagsKeepText(String paraString) {
        paraString = paraString.replace("/", "x");
        paraString = paraString.replaceAll("[0-9a-zA-Z+_]", "");//0..9 and + are for my custom tags, not PENN
        paraString = paraString.replace("x", " ");
        return paraString;
    }

    public static String StripDigitsAndHashSign(String paraString) {
        paraString = paraString.replaceAll("(#\\d)", "");
        return paraString;
    }

    public static String StripDigitsAndColons(String paraString) {
        //this one for POS tags numbers, DACSentence.SaveRecord
        paraString = paraString.replaceAll("(:\\d\\d?)", "");

        //this one for text tokens numbers, ConceptExtractor.ExtractConceptsByPOS()
        paraString = paraString.replaceAll("(\\d?\\d:)", "");
        return paraString;
    }

    public static String StripNonDigitsOrColons(String paraString) {
        paraString = paraString.replaceAll("[^:\\d]", "");
        return paraString;
    }

    public static String StripNonDigits(String paraString) {
        paraString = paraString.replaceAll("\\D+", "");
        return paraString;
    }

    public static String MyStringTrim(String paraString) {
        paraString = paraString.replaceAll("^\\s+", "");
        paraString = paraString.replaceAll("\\s+$", "");
        return paraString;
    }

    public static String MakeRegularExpressionReady(String paraString) {
        paraString = paraString.replace(Utility.Globals.TokenOpenBracket, Utility.Globals.RegularExpressionReadyTokenOpenBracket);
        paraString = paraString.replace(Utility.Globals.TokenCloseBracket, Utility.Globals.RegularExpressionReadyTokenCloseBracket);
        paraString = paraString.replace(Utility.Globals.SegmentOpenBracket, Utility.Globals.RegularExpressionReadySegmentOpenBracket);
        paraString = paraString.replace(Utility.Globals.SegmentCloseBracket, Utility.Globals.RegularExpressionReadySegmentCloseBracket);
        paraString = paraString.replace(Utility.Globals.TokenConcatinationChar, Utility.Globals.RegularExpressionReadyTSegmentConcatinationChar);
        return paraString;
    }
}
