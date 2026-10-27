package TestBed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex2 {

    public static void main(String args[]) {

        // String to be scanned to find the pattern.
        String line = "0:<«RP_WA_SEQ»«RB_TIM»> 1:<«VBP_SNG_MSC_ndV»> 2:<«NN_SNG_FEM»> 3:<«RP_WA_SEQ»«VBP_SNG_stV»> 4:<«IN_CNFRM»> 5:<«RP_DMND_NEG»> 6:<«VBP_SNG_MSC»> 7:<«NN»> 8:<«DT_SNG_MSC_NR»> 9:<«VBG_NN_SNG_MSC»> 10:<«JJ»>";
        //String pattern = "(.*)(\\d+)(.*)";
        String patternRule = "(VB#2)(?:\\w+)(?:.){7,8}(NN#1)";
        String patternRuleFiltered = "(VB)(?:\\w+)(?:.){7,8}(NN)";

        // Create a Pattern object
        Pattern r = Pattern.compile(patternRuleFiltered);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        if (m.find()) {

            for (int i = 0; i <= m.groupCount(); i++) {
                System.out.println("___________________________________________");
                System.out.println("Group Id " + i);
                System.out.println("Found value: " + m.group(i));
                System.out.println("Group " + i + " start in matched text : " + m.start(i));
                System.out.println("Group Index in pattern: " + patternRuleFiltered.indexOf(m.group(i)));
                System.out.println("Argument Order in Pattern Rule: " + GetArguementOrderOfAGroup(patternRule, i));
                System.out.println("Item Index in Matched Text: " + GetTokenIndexInMatchedText(line, m.start(i)));
            }
        } else {
            System.out.println("No Match");
        }
    }

    private static int GetTokenIndexInMatchedText(String paraText, int paraStart) {
        //the idea of this algorith is to iterate the text string i.e. pos version of the string
        //an count the token open and close brackets, if we meet an open bracket we increase c1 counter
        //by 1, if we meet a close bracket, we decrement c1 counter by 1, once c1 becomes 0 after a 
        //substraction, we know that we have passed over a full token, so we can increase c2 by 1
        //we continue moving until we reach the match start point, therefore we would have counted all
        //the tokens during our way
        int c1 = 0;
        int c2 = 0;

        for (int i = 0; i <= paraStart; i++) {
            if (paraText.charAt(i) == Utility.Globals.RegularExpressionReadyTokenOpenBracket) {
                c1 += 1;
            } else if (paraText.charAt(i) == Utility.Globals.RegularExpressionReadyTokenCloseBracket) {
                c1 -= 1;
                //if we met a closing bracked, and c1 became 0 i.e. all opens are close, then
                //then we know that we have passed through a complete token, so increment c2
                if (c1 == 0) {
                    c2 += 1;
                }
            }
        }

        //once we finish and reach the paraStart index, we have to check c1
        //if it is larger than zero, this means that we entered a new token
        //but we coudln't finish it as the start of capture doesn't cover all
        //the closing brakcets, nevertheless, we should increment c2 by one to
        //take this new token into consideration
        if (c1 >= 1) {
            c2 += 1;
        }

        return c2;
    }
    
     private static int GetArguementOrderOfAGroup(String paraPatternRule, int paraGroupId) {
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
    }

    private static int GetItemIndexInMatchedText1(String paraText, int paraStart) {
        int searchStartIndex = paraStart;
        int itemOrder = -1;

        while (searchStartIndex >= 0) {
            if (paraText.charAt(searchStartIndex) == 32)//a space
            {
                searchStartIndex = searchStartIndex + 1;//move on step forwards
                break;
            } else {
                searchStartIndex = searchStartIndex - 1;
            }
        }

        //this code checks when the pointer keeps moving back until it reaches the begining 
        //of the string --> which indicates that this item is the first item
        if (searchStartIndex < 0) {
            searchStartIndex = 0;
        }

        //capture the character from the searchStartIndex until before the semi colon
        itemOrder = Integer.valueOf(paraText.substring(searchStartIndex, paraText.indexOf(":", searchStartIndex)));

        return itemOrder;
    }   
    
        private int GetGroupNumberforAnArgument(String paraPatternRule, int paraArgumentNumber) {        
        int argumentIndexLocation =  paraPatternRule.indexOf(paraArgumentNumber + Utility.Globals.ItemOrderSeperator);
        
        int arguementGroupNumber = 0;

        for (int i = 1; i <= argumentIndexLocation; i++) {
            if (paraPatternRule.charAt(i) == Utility.Globals.ItemOrderSeperator)
            {
               arguementGroupNumber+=1; 
            }
        }       
            return arguementGroupNumber;
    }

}
