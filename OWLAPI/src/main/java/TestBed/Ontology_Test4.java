/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBed;

import Utility.Globals;
import Utility.OntologyInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author Mohammad Fasha
 */
public class Ontology_Test4 {

    public static void main(String[] args) {
        try {
            OntologyInterface oi = new OntologyInterface(Globals.OntologyDefaultDirectory + "current.owl");
            oi.getClassObjectByAnnotationString("حطم");
        } catch (OWLOntologyCreationException ex) {
            Logger.getLogger(Ontology_Test4.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Ontology_Test4.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
