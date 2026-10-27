/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataProcessing;

import Stanford.StanfordTagger;
import Stanford.StanfordParser;
import DB.DAC.DACSentence;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mohammad Fasha
 */
public class UploadTextFileIntoDB {

    public String UploadTextFile(String paraFile, boolean paraDeleteOldRecords) {
        String result = "File upload successfull...";
        try {
            BufferedReader reader;

            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(paraFile), "UTF-8"));

            DACSentence sentencesDAC = new DACSentence();
            StanfordTagger stanfordTagger = new StanfordTagger();
            StanfordParser stanfordParser = new StanfordParser();

            int id;
            int storyId;            
            if (paraDeleteOldRecords) {
                sentencesDAC.DeleteAllRows();
                id = 0;
                storyId = 0;
            } else {
                id = sentencesDAC.GetLastId() + 1;
                storyId = sentencesDAC.GetLastStoryId() + 1;
            }

            int c;
            String sentenceText = "";
            String POS;
            String ParseTree;

            char character;
            int storyLineNumber = 0;

            while ((c = reader.read()) != -1) {

                //special cleanup case for a character that appears as empty or null in java
                //probably a zero-width no-break space.
                if (c == 65279) {
                    continue;
                }

                character = (char) c;
                switch (character) {
                    case '#':
                        storyId += 1;
                        sentenceText = "";
                        storyLineNumber = 0; //reset sentence number
                        break;

                    //characters that cause end of line
                    case '\r':
                    case '\n':
                        break;
                    case '.': //.
                    case '؟': //?
                    case '،':
                    case '؛': //1548  
                    case '!':
                        //case 1567: //?

                        //The following code shall run if any of the above characters is found                        
                        sentenceText = sentenceText + (char) c;
                        sentenceText = sentenceText.trim();

                        //Some cleanup tasks
                        sentenceText = sentenceText.replaceAll("أ", "ا");
                        sentenceText = sentenceText.replaceAll("ة", "ه");
                        sentenceText = sentenceText.replaceAll("آ", "ا");
                        sentenceText = sentenceText.replaceAll("لآ", "لا");
                        sentenceText = sentenceText.replaceAll("لأ", "لا");

                        POS = stanfordTagger.TagString(sentenceText);
                        
                        //shutdown Stanford parser because of memory issue
                        ParseTree="";
                        //ParseTree = stanfordParser.ParseString(sentenceText);

                        //make sure to trim any spaces
                        sentenceText = Utility.Globals.MyStringTrim(sentenceText);
                        POS = Utility.Globals.StripTextKeepPOSTags(POS);
                        POS = Utility.Globals.MyStringTrim(POS);
                        ParseTree = Utility.Globals.MyStringTrim(ParseTree);

                        //Itimize sentence text
                        String[] textArray = sentenceText.split(" ");
                        String itimizedSentenceText = "";
                        for (int i = 0; i < textArray.length; i++) {
                            itimizedSentenceText = itimizedSentenceText + textArray[i] + ":" + i + " ";
                        }

                        //Itimize POS
                        String[] posArray = POS.split(" ");
                        String itimizedPOS = "";
                        for (int i = 0; i < posArray.length; i++) {
                            itimizedPOS = itimizedPOS + i + ":({" + posArray[i] + "})" + " ";
                        }

                        sentencesDAC.InsertRow(++id, storyId, ++storyLineNumber, itimizedSentenceText, itimizedPOS, ParseTree);
                        sentenceText = "";
                        break;
                    default:
                        sentenceText = sentenceText + (char) c;
                        break;
                }
            }
            reader.close();
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(UploadTextFileIntoDB.class.getName()).log(Level.SEVERE, null, ex);
            result = "File upload failed...";
        }
        return result;
    }
}
