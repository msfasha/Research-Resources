/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataProcessing;

import DB.DAC.DACSentence;
import DB.DAC.DACSentenceConcept;
import DB.DAC.DACDefConceptPattern;
import Form.InformationExctration;
import Stanford.StandfordBuckwalter;
import Utility.CapturedConceptStructure;
import Utility.Globals;
import Utility.OntologyInterface;
import Utility.ReasonerInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author Mohammad Fasha
 */
public class ConceptExtractor {

    DACSentenceConcept DACSentenceConcept;
    DACSentence DACSentence;
    DACDefConceptPattern DACDefConceptPattern;
    private final OntologyInterface OntologyInterface;
    private static ReasonerInterface ReasonerInterface;

    //these two arrays are class level, they apply for all sentences
    List<CapturedConceptStructure> AgentsBuffer = new ArrayList();
    List<CapturedConceptStructure> ObjectsBuffer = new ArrayList();

    public ConceptExtractor(OntologyInterface paraOntologyInterface) throws ClassNotFoundException, SQLException, OWLOntologyCreationException, Exception {
        DACSentence = new DACSentence();
        DACSentenceConcept = new DACSentenceConcept();
        DACDefConceptPattern = new DACDefConceptPattern();

        OntologyInterface = paraOntologyInterface;
        ReasonerInterface = new ReasonerInterface(OntologyInterface.OntologyFile);

        //class level arrays or memory buffers to resolve subject dropping and attached pronouns scenarios
        AgentsBuffer = new ArrayList();
        ObjectsBuffer = new ArrayList();
    }

    //this is the previous algorithm that we used first, it is pure sequential, the subject prodrop and objects pronouns won't be caught
    public List<CapturedConceptStructure> ExtractAllConcepts(String paraSentenceText, String paraSentenceExpandedPOS, int paraLineNumber) throws SQLException {
        try {
            //Get all POS patterns which are defined in database
            ResultSet conceptPatternRS = DACDefConceptPattern.GetAllRows();
            return ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, conceptPatternRS, paraLineNumber);

        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
            return null;
        }
    }

    public List<CapturedConceptStructure> ExtractAllFromSentenceByOrder(String paraSentenceText, String paraSentenceExpandedPOS, int paraLineNumber) throws SQLException {
        try {
            //this algorithm extracts all concepts in a given sentence
            //it concentrates on events and agents first, it handles the agent pro-drop
            //if an event is in the sentence and there is no agent for the sentence then 
            //we take the last character that matches the annotation signature
            List<CapturedConceptStructure> eventsArray = new ArrayList();
            List<CapturedConceptStructure> eventAgentArray = new ArrayList();
            List<CapturedConceptStructure> eventObjectArray = new ArrayList();
            List<CapturedConceptStructure> otherConceptsArray = new ArrayList();
            List<CapturedConceptStructure> allConceptsArray = new ArrayList();

            //this array is used to replace suffix objects with their designated objects
            List<CapturedConceptStructure> eventsWithAttachedObjectsCCSArray = new ArrayList();

            //1- get agents in sentence
            ResultSet rs = DACDefConceptPattern.GetPatternsByConceptId(Globals.RegexPattern_Agent);
            AgentsBuffer.addAll(ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, rs, paraLineNumber));

            //2- get all nouns in the sentence
            rs = DACDefConceptPattern.GetPatternsByConceptId(Globals.RegexPattern_Nouns);
            ObjectsBuffer.addAll(ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, rs, paraLineNumber));

            //3- get attached objects pronouns (SFX_OBJ) in the sentence, this specific array 
            //is more of linguistic that conceptual like the others
            rs = DACDefConceptPattern.GetPatternsByConceptId(Globals.RegexPattern_SFXOBJ);
            eventsWithAttachedObjectsCCSArray = ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, rs, paraLineNumber);
            //********************************************************************************************

            //get events in sentence
            rs = DACDefConceptPattern.GetPatternsByConceptId(Globals.RegexPattern_Event);
            eventsArray = ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, rs, paraLineNumber);

            //get eventAgents relations in sentence
            rs = DACDefConceptPattern.GetPatternsByConceptId(Globals.RegexPattern_ISTheAgentOfEvent);
            eventAgentArray = ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, rs, paraLineNumber);

            //get eventObjects relations in sentence
            rs = DACDefConceptPattern.GetPatternsByConceptId(Globals.RegexPattern_ISTheObjectOfEvent);
            eventObjectArray = ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, rs, paraLineNumber);

            rs = DACDefConceptPattern.GetPatternsOfRemainingConcepts();;
            otherConceptsArray = ExtractConceptsByPatternsList(paraSentenceText, paraSentenceExpandedPOS, rs, paraLineNumber);

            //use ontology to restrict to concrete objects
            //PruneArray(NounsArray, Globals.ConceptConcrete);
            ResolveSubjectDropping(eventsArray, eventAgentArray, true);
            ResolveObjectPronouns(eventsWithAttachedObjectsCCSArray, eventObjectArray, true);

            allConceptsArray.addAll(AgentsBuffer);
            allConceptsArray.addAll(eventsArray);
            allConceptsArray.addAll(eventAgentArray);
            allConceptsArray.addAll(eventObjectArray);
            allConceptsArray.addAll(otherConceptsArray);

            return allConceptsArray;
        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
            System.out.println(ex.toString());
            return null;
        }
    }

    private void ResolveSubjectDropping(List<CapturedConceptStructure> paraEventsArray, List<CapturedConceptStructure> paraEventAgentArray, boolean paraValidateSemantics) {

        try {
            //S U B J E C T   D R O P
            //Iterate all events and check if an agent was found
            //if not found, then get the last agent from AgentsArray
            for (CapturedConceptStructure event : paraEventsArray) {
                boolean agentFound = false;

                //Iterate over all extracted EventAgent relations 
                //and see if we have a relation for the event under examination
                for (CapturedConceptStructure eventAgent : paraEventAgentArray) {
                    if (event.ArgumentOneTokenId == eventAgent.ArgumentTwoTokenId) {
                        agentFound = true;
                        break;
                    }
                }
                //If no agent was found for the event i.e. a pro drop relation
                //then get the first matching character from the array
                if (!agentFound) {
                    CapturedConceptStructure matchingAgent = null;
                    matchingAgent = FindMatchingAgentByPOS(event.ArgumentOneExpandedPOS, event.ArgumentOneClass, paraValidateSemantics);

                    if (matchingAgent != null) {
                        CapturedConceptStructure ccs = new CapturedConceptStructure();

                        ccs.ArgumentOneClass = matchingAgent.ArgumentOneClass;
                        ccs.ArgumentOneValue = matchingAgent.ArgumentOneValue;
                        ccs.ArgumentOneValueStemmed = matchingAgent.ArgumentOneValueStemmed;
                        ccs.ArgumentOneValueBuckwalter = matchingAgent.ArgumentOneValueBuckwalter;
                        ccs.ArgumentOneExpandedPOS = matchingAgent.ArgumentOneExpandedPOS;

                        //the token id where the drop subject (character) was referenced
                        ccs.ArgumentOneTokenId = matchingAgent.ArgumentOneTokenId;

                        //although we are filling argument two of the new relation
                        //we are referencing argument one of event concept structure since
                        //event is a unary relation, we didn'n find the binary relation thats
                        //why we are building it
                        ccs.ArgumentTwoClass = event.ArgumentOneClass;
                        ccs.ArgumentTwoValue = event.ArgumentOneValue;
                        ccs.ArgumentTwoValueStemmed = event.ArgumentOneValueStemmed;
                        ccs.ArgumentTwoValueBuckwalter = event.ArgumentOneValueBuckwalter;
                        ccs.ArgumentTwoExpandedPOS = event.ArgumentOneExpandedPOS;

                        ccs.LineNumber = event.LineNumber;
                        ccs.ArgumentTwoTokenId = event.ArgumentOneTokenId;

                        ccs.PatternId = "Subject drop";
                        ccs.ConceptIdValidated = matchingAgent.ConceptIdValidated;
                        ccs.ConceptId = Globals.RegexPattern_ISTheAgentOfEvent;

                        paraEventAgentArray.add(ccs);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
        }
    }

    private void ResolveObjectPronouns(List<CapturedConceptStructure> paraEventsWithAttachedObjectsCCSArray,
            List<CapturedConceptStructure> paraEventObjectArray,
            boolean paraValidateSemantics) {

        try {
            //O B J E C T    P R O N O U N S
            for (CapturedConceptStructure object : paraEventsWithAttachedObjectsCCSArray) {

                String eventSuffix = object.ArgumentOneExpandedPOS.substring(object.ArgumentOneExpandedPOS.indexOf(Globals.RegexPattern_SFXOBJ), object.ArgumentOneExpandedPOS.length() - 2);//2 to skip both »>            

                CapturedConceptStructure matchingObject = null;
                matchingObject = FindMatchingObjectByPOS(eventSuffix, object.ArgumentOneClass, paraValidateSemantics);
                if (matchingObject != null) {
                    {
                        CapturedConceptStructure ccs = new CapturedConceptStructure();

                        ccs.ArgumentOneClass = matchingObject.ArgumentOneClass;
                        ccs.ArgumentOneExpandedPOS = matchingObject.ArgumentOneExpandedPOS;
                        ccs.ArgumentOneTokenId = matchingObject.ArgumentOneTokenId;
                        ccs.ArgumentOneValue = matchingObject.ArgumentOneValue;
                        ccs.ArgumentOneValueBuckwalter = matchingObject.ArgumentOneValueBuckwalter;
                        ccs.ArgumentOneValueStemmed = matchingObject.ArgumentOneValueStemmed;

                        ccs.ArgumentTwoClass = object.ArgumentOneClass;
                        ccs.ArgumentTwoExpandedPOS = object.ArgumentOneExpandedPOS;
                        ccs.ArgumentTwoTokenId = object.ArgumentOneTokenId;
                        ccs.ArgumentTwoValue = object.ArgumentOneValue;
                        ccs.ArgumentTwoValueBuckwalter = object.ArgumentOneValueBuckwalter;
                        ccs.ArgumentTwoValueStemmed = object.ArgumentOneValueStemmed;

                        ccs.LineNumber = object.LineNumber;
                        ccs.ConceptId = Globals.RegexPattern_ISTheObjectOfEvent;
                        ccs.ConceptIdValidated = matchingObject.ConceptIdValidated;
                        ccs.PatternId = "Object Pronoun";

                        paraEventObjectArray.add(ccs);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
        }
    }

    private CapturedConceptStructure FindMatchingAgentByPOS(String paraSignature, String paraEventType, boolean paraValidateSemantics) {
        try {
            //First, filter the signature of the Agent we are examining
            //because the verb might be inflected with gender 1 and a it also might include a prefix of gender 2
            //therefore, the Agent will be resolved to an incorrect agent
            int startIndex = paraSignature.indexOf("VB");
            startIndex = paraSignature.indexOf(Globals.RegularExpressionReadyTSegmentConcatinationChar, startIndex);
            int endIndex = paraSignature.indexOf(Globals.RegularExpressionReadySegmentCloseBracket, startIndex);
            paraSignature = paraSignature.substring(startIndex + 1, endIndex);

            CapturedConceptStructure matchingAgent = null;
            CapturedConceptStructure firstMatchingAgent = null;

            for (int i = AgentsBuffer.size() - 1; i >= 0; i--) {
                //we need the exact start location, nouns and proper nouns might have prefixes,
                //therefore, we move to the last segment by looking for the last » character
                //then from there we move to the first underscore to skip the name tag and extract
                //the other modifiers e.g. SNG_MSC, PLRL_FEM...etc.

                startIndex = AgentsBuffer.get(i).ArgumentOneExpandedPOS.indexOf("NN");
                startIndex = AgentsBuffer.get(i).ArgumentOneExpandedPOS.indexOf(Globals.RegularExpressionReadyTSegmentConcatinationChar, startIndex);
                endIndex = AgentsBuffer.get(i).ArgumentOneExpandedPOS.indexOf(Globals.RegularExpressionReadySegmentCloseBracket, startIndex);
                String characterSignature = AgentsBuffer.get(i).ArgumentOneExpandedPOS.substring(startIndex + 1, endIndex);

                //if the events POS signature contains the character POS signature 
                if (paraSignature.contains(characterSignature)) {
                    matchingAgent = new CapturedConceptStructure(AgentsBuffer.get(i));

                    if (firstMatchingAgent == null) {
                        firstMatchingAgent = new CapturedConceptStructure(AgentsBuffer.get(i));
                    }

                    if (paraValidateSemantics) {

                        System.out.println("Subject dropping: " + matchingAgent.ArgumentOneClass
                                + ": " + matchingAgent.ArgumentOneValue
                                + " " + Globals.RegexPattern_ISTheAgentOfEvent + " "
                                + paraEventType);

                        if (!matchingAgent.ArgumentOneClass.contentEquals("Thing") && !paraEventType.contentEquals("Thing")) {
                            if (ValidSemanticRelation(matchingAgent.ArgumentOneClass, Globals.RegexPattern_ISTheAgentOfEvent, paraEventType)) {
                                matchingAgent.ConceptIdValidated = "Validated Agent";
                                i = 0;// to exit the for loop
                            }
                        }
                    }
                }
            }

            if (matchingAgent != null) {
                if (matchingAgent.ConceptIdValidated == "Validated Agent") {
                    return matchingAgent;
                } else {
                    return firstMatchingAgent;
                }
            } else {
                return null;
            }

        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.getMessage());
            System.out.println(ex.toString());
            return null;
        }
    }

    private CapturedConceptStructure FindMatchingObjectByPOS(String paraSuffix, String paraEventType, boolean paraValidateSemantics) {
        try {
            //The start location is different than Agents array above, simply pass 8 characters to
            //skip the SFX_OBJ_ characters and directly get the gender and number POS
            int startIndex, endIndex;
            paraSuffix = paraSuffix.substring(8, paraSuffix.length());

            CapturedConceptStructure matchingObject = null;
            CapturedConceptStructure firstMatchingObject = null;

            for (int i = ObjectsBuffer.size() - 1; i >= 0; i--) {
                //we need the exact start location, nouns and proper nouns might have prefixes,
                //therefore, we move to the last segment by looking for the last » character
                //then from there we move to the first underscore to skip the name tag and extract
                //the other modifiers e.g. SNG_MSC, PLRL_FEM...etc.
                startIndex = ObjectsBuffer.get(i).ArgumentOneExpandedPOS.indexOf("NN");
                startIndex = ObjectsBuffer.get(i).ArgumentOneExpandedPOS.indexOf(Globals.RegularExpressionReadyTSegmentConcatinationChar, startIndex);
                endIndex = ObjectsBuffer.get(i).ArgumentOneExpandedPOS.indexOf(Globals.RegularExpressionReadySegmentCloseBracket, startIndex);
                String objectSignature = ObjectsBuffer.get(i).ArgumentOneExpandedPOS.substring(startIndex + 1, endIndex);

                //if the events POS signature contains the character POS signature 
                if (objectSignature.contains(paraSuffix)) {
                    matchingObject = new CapturedConceptStructure(ObjectsBuffer.get(i));

                    if (firstMatchingObject == null) {
                        firstMatchingObject = new CapturedConceptStructure(ObjectsBuffer.get(i));
                    }

                    if (paraValidateSemantics) {

                        System.out.println("Objects pronouns: " + matchingObject.ArgumentOneClass
                                + ": " + matchingObject.ArgumentOneValue
                                + " " + Globals.RegexPattern_ISTheObjectOfEvent + " "
                                + paraEventType);

                        if (!matchingObject.ArgumentOneClass.contentEquals("Thing") && !paraEventType.contentEquals("Thing")) {
                            if (ValidSemanticRelation(matchingObject.ArgumentOneClass, Globals.RegexPattern_ISTheObjectOfEvent, paraEventType)) {
                                matchingObject.ConceptIdValidated = "Validated Object";
                                i = 0;// to exit the for loop
                            }
                        }
                    }
                }
            }

            if (matchingObject != null) {
                if (matchingObject.ConceptIdValidated == "Validated Object") {
                    return matchingObject;
                } else {
                    return firstMatchingObject;
                }
            } else {
                return null;
            }

        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
            return null;
        }
    }

    private List<CapturedConceptStructure> ExtractConceptsByPatternsList(String paraSentenceText, String paraSentenceExpandedPOS, ResultSet paraPatternsRS, int paraLineNumber) throws SQLException {
        try {
            //the theme of this procedure is to run the passed pos version of text over all the defined regular expression pattern
            //all these rules are design to capture a unary pattern or a binary pattern, not more not less
            //therefore, all these patterns are defined with regular expression groups i.e. ( ), while each and every group
            //have to be defined with the order seperator i.e. # which is followed either by 1 or 2 i.e. (VB#1)
            //we select a seperator that is not used as a special character by regular expression engine i.e. #
            //this parameter is defined in the Utility.Global class

            //Initialize the concepts ArrayList
            List<CapturedConceptStructure> conceptsArray = new ArrayList<>();

            //Strip out digits and colons, we need to do this so that we can exract the correct
            //word later and attach it in the designated concept slot (without its index number)
            String textOnly = Utility.Globals.StripDigitsAndColons(paraSentenceText);

            StandfordBuckwalter stanfordBuckwalter = new StandfordBuckwalter();
            ShereenKhojaStemmer.ArabicStemmer aStemmer = new ShereenKhojaStemmer.ArabicStemmer();
            //Tokenize sentence, create an array representation for sentence tokens
            //for arabic, space character i.e. ascii 32 is suitable for splitting words
            //we shall use this array to extract the designated item by its index so that 
            //we can insert it into the designated concept slot
            String[] textTokenArray = textOnly.split(" ");
            String[] expandedPOSTokenArray = paraSentenceExpandedPOS.split(" ");

            paraPatternsRS.beforeFirst();
            while (paraPatternsRS.next()) {
                String patternRule = paraPatternsRS.getString("pos_pattern");
                //Remove position markers, the 1 2 numbering of arguments                    
                String patternRuleFiltered = Utility.Globals.StripDigitsAndHashSign(patternRule);

                //Compile the regular expression to extract the pattern (the pattern without word order)
                Pattern p = Pattern.compile(patternRuleFiltered);

                //paraSentenceExpandedPOS is already fildtered by B2A2 as RE ready
                Matcher m = p.matcher(paraSentenceExpandedPOS);

                while (m.find()) {

                    CapturedConceptStructure cms = new CapturedConceptStructure();
                    cms.ConceptId = paraPatternsRS.getString("concept_id");
                    cms.PatternId = String.valueOf(paraPatternsRS.getInt("id"));
                    cms.LineNumber = paraLineNumber;

                    //check if this pattern has one arguement or two arguements
                    if (m.groupCount() == 1) {
                        int tokenIndex;

                        //m.end not m.start, to count all the open brackets  untill the end of pattern match
                        //using the m.start created inconsitencies
                        tokenIndex = GetTokenIndexInMatchedText(paraSentenceExpandedPOS, m.end(1));
                        cms.ArgumentOneTokenId = tokenIndex;
                        cms.ArgumentOneValue = textTokenArray[tokenIndex - 1];
                        //this is the original expanded POS value of the concept
                        cms.ArgumentOneExpandedPOS = expandedPOSTokenArray[tokenIndex - 1];
                        cms.ParentClass = paraPatternsRS.getString("parent_class");
                        cms.ArgumentOneValueBuckwalter = stanfordBuckwalter.ArabicToBuckwalter(cms.ArgumentOneValue);
                        cms.ArgumentOneValueStemmed = aStemmer.StemWord(cms.ArgumentOneValue);

                        //Try to get the accurate class name from the supporting ontolgy
                        //if no value is found in the annotation, just use the original value
                        //that was set in the def_concept table
                        cms.ArgumentOneClass = OntologyInterface.getClassNameByAnnotationValue(cms.ArgumentOneValue);
                        if (cms.ArgumentOneClass == null) {
                            cms.ArgumentOneClass = paraPatternsRS.getString("argument_1_class");
                        }

                    } else if (m.groupCount() == 2) {//this patterm has two arguements

                        //in our patterns i.e. DL motivated, we only have either one group or two groups
                        //therefore, if deteremined which group is related to arguement one, then automatically 
                        //the other group will be for arguement two
                        //m.end() determines the index of the group ending position
                        int argumentOneIndex;
                        int argumentTwoIndex;

                        if (GetArguementOneGroupOrderInPatternRule(patternRule, 1) == 1) {
                            argumentOneIndex = GetTokenIndexInMatchedText(paraSentenceExpandedPOS, m.end(1));
                            argumentTwoIndex = GetTokenIndexInMatchedText(paraSentenceExpandedPOS, m.end(2));
                        } else {
                            argumentOneIndex = GetTokenIndexInMatchedText(paraSentenceExpandedPOS, m.end(2));
                            argumentTwoIndex = GetTokenIndexInMatchedText(paraSentenceExpandedPOS, m.end(1));
                        }

                        cms.ArgumentOneTokenId = argumentOneIndex;
                        cms.ArgumentOneValue = textTokenArray[argumentOneIndex - 1];
                        cms.ArgumentOneExpandedPOS = expandedPOSTokenArray[argumentOneIndex - 1];
                        cms.ArgumentOneValueBuckwalter = stanfordBuckwalter.ArabicToBuckwalter(cms.ArgumentOneValue);
                        cms.ArgumentOneValueStemmed = aStemmer.StemWord(cms.ArgumentOneValue);

                        //Try to get the accurate class name from the supporting ontolgy
                        //if no value is found in the annotation, just use the original value
                        //that was set in the def_concept table
                        cms.ArgumentOneClass = OntologyInterface.getClassNameByAnnotationValue(cms.ArgumentOneValue);
                        if (cms.ArgumentOneClass == null) {
                            cms.ArgumentOneClass = paraPatternsRS.getString("argument_1_class");
                        }

                        cms.ArgumentTwoTokenId = argumentTwoIndex;
                        cms.ArgumentTwoValue = textTokenArray[argumentTwoIndex - 1];
                        cms.ArgumentTwoExpandedPOS = expandedPOSTokenArray[argumentTwoIndex - 1];
                        cms.ArgumentTwoValueBuckwalter = stanfordBuckwalter.ArabicToBuckwalter(cms.ArgumentTwoValue);
                        cms.ArgumentTwoValueStemmed = aStemmer.StemWord(cms.ArgumentTwoValue);

                        //Try to get the accurate class name from the supporting ontolgy
                        //if no value is found in the annotation, just use the original value
                        //that was set in the def_concept table
                        cms.ArgumentTwoClass = OntologyInterface.getClassNameByAnnotationValue(cms.ArgumentTwoValue);
                        if (cms.ArgumentTwoClass == null) {
                            cms.ArgumentTwoClass = paraPatternsRS.getString("argument_2_class");
                        }
                    }
                    conceptsArray.add(cms);
                }
            }
            return conceptsArray;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
            return null;
        }
    }

    private int GetTokenIndexInMatchedText(String paraText, int paraEnd) {
        try {
            //the idea of this algorithm is to iterate the text string i.e. pos version of the string
            //and count the token open and close brackets, if we meet an open bracket we increase c1 counter
            //by 1, if we meet a close bracket, we decrement c1 counter by 1, once c1 becomes 0 after a 
            //substraction, we know that we have passed over a full token, so we can increase c2 by 1
            //we continue moving until we reach the match start point, therefore we would have counted all
            //the tokens during our way
            int c1 = 0;
            int c2 = 0;

            //@@@@System.out.println("Static counter is ::: " + ++Globals.static_counter);

            for (int i = 0; i < paraEnd; i++) {
                if (paraText.charAt(i) == Utility.Globals.RegularExpressionReadyTokenOpenBracket) {
                    c1 += 1;
                } else if (paraText.charAt(i) == Utility.Globals.RegularExpressionReadyTokenCloseBracket) {
                    c1 -= 1;
                    //if we met a closing bracked, and c1 became 0 i.e. all opens are close, then
                    //we know that we have passed through a complete token, so increment c2
                    if (c1 == 0) {
                        c2 += 1;
                    }
                }
            }

            //once we finish and reach the paraStart index, we have to check c1
            //if it is larger than zero, this means that we entered a new token
            //but we couldn't finish it as the start of capture doesn't cover all
            //the closing brakcets, nevertheless, we should increment c2 by one to
            //take this new token into consideration
            if (c1 >= 1) {
                c2 += 1;
            }

            return c2;

        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
            System.out.println(ex.toString());
            return 0;
        }
    }

    private int GetArguementOneGroupOrderInPatternRule(String paraPatternRule, int paraGroupId) {
        try {
            //the idea of this algorithm is to move forward a Group Id number of times while
            //passing through ItemOrderSeperator i.e #
            //once we reach the required number of times, we extract the designated order number
            int indexOfTokenOrder = -1;

            for (int i = 1; i <= paraGroupId; i++) {
                indexOfTokenOrder = paraPatternRule.indexOf(Utility.Globals.ItemOrderSeperator, indexOfTokenOrder);
                indexOfTokenOrder = indexOfTokenOrder + 1;//move forward to point on item
            }

            if (indexOfTokenOrder != -1) {
                return Integer.valueOf(paraPatternRule.substring(indexOfTokenOrder, indexOfTokenOrder + 1));
            } else {
                return -1;
            }
        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
            return 0;
        }
    }

    private void PruneArray(List<CapturedConceptStructure> paraArray, String paraParentClass) {
        try {
//        paraArray.stream().forEach((ccs
//                -> {
//            String childClass = OntologyInterface.getClassNameByAnnotationValue(ccs.ArgumentOneValue);
//            if (!ReasonerInterface.ISSuperClassOf(paraParentClass, childClass)) {
//                paraArray.remove(ccs);
//            }
//        }));
        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
        }
    }

    private boolean ValidSemanticRelation(String paraArgumentOneClass, String paraRelationID, String paraArgumentTwoClass) {
        return ReasonerInterface.HasInstances(paraArgumentOneClass + " and " + paraRelationID + " some " + paraArgumentTwoClass);
    }
}
