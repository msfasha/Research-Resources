package TestBed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRegex1 {

    public static void main(String args[]) {

        // String to be scanned to find the pattern.
        String line = "NN VBP";
        //String pattern = "(.*)(\\d+)(.*)";
        String pattern = "(DTNN|NN) (VB.)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            System.out.println("Found value: " + m.group(1));
            System.out.println("Found value: " + m.group(2));            
        } else {
            System.out.println("NO MATCH");
        }
    }
}
