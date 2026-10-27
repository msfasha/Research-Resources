/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBed;

import Utility.Globals;
import Utility.OntologyInterface;
import Utility.ReasonerInterface;
import java.util.ArrayList;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author Mohammad Fasha
 */
public class OntologyAndReasoner {

    public static void main(String[] args) throws OWLOntologyCreationException, Exception {
        OntologyInterface o = new OntologyInterface(Globals.OntologyDefaultDirectory + "current.owl");
        ReasonerInterface r = new ReasonerInterface(Globals.OntologyDefaultDirectory + "current.owl");

        String className = o.getClassNameByAnnotationValue("زجاج");

        ArrayList<String> arr = (ArrayList<String>) r.GetSuperClasses(className);

        arr.stream().forEach((str) -> {
            System.out.println(str);
        });

        System.out.println(r.ISSuperClassOf("ConcreteConcept", className));
    }
}
