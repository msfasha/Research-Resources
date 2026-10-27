/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBed;

import Utility.Globals;
import Utility.ReasonerInterface;
import java.util.ArrayList;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author Mohammad Fasha
 */
public class TestDLQueryInterface {
    public static void main(String[] args) throws OWLOntologyCreationException
    {
        ReasonerInterface dl = new ReasonerInterface(Globals.OntologyDefaultDirectory+ "current.owl");
        ArrayList<String> arr = (ArrayList<String>) dl.GetSuperClasses("Glass");
        
        for (String str:arr)
        {
            System.out.println(str);
        }
       
                
        System.out.println(dl.ISSuperClassOf("ConcreteConcept", "Glass"));
    }
}
