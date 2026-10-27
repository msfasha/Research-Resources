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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Test
{

    public static void main(String[] args)
    {
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
        List<String> filtered = strings.stream().filter(x -> !x.isEmpty()).collect(Collectors.toList());
        System.out.println("Filtered List: " + filtered);
        
        String mergedString = strings.stream().filter(string -> !string.isEmpty()).collect(Collectors.joining("* "));
        System.out.println("Merged String: " + mergedString);
    }

}
