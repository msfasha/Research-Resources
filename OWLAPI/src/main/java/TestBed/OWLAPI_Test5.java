package TestBed;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OWLAPI_Test5
{
        public static void main(String[] args)
{
        File file = new File("D:\\OneDrive\\Thesis\\PHD Research\\My Ontologies\\current.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;

        try {
            ontology = manager.loadOntologyFromOntologyDocument(file);

            Set<OWLClass> classes;
            Set<OWLObjectProperty> prop;
            Set<OWLDataProperty> dataProp;
            Set<OWLNamedIndividual> individuals;

            classes = ontology.getClassesInSignature();
            prop = ontology.getObjectPropertiesInSignature();
            dataProp = ontology.getDataPropertiesInSignature();
            individuals = ontology.getIndividualsInSignature();
            
            //configurator = new OWLAPIOntologyConfigurator(this);            

            for (OWLObjectPropertyDomainAxiom op : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {                        
                            for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
                                 System.out.println("\t\t +: " + oop.getIRI().getShortForm());
                            }
                            //System.out.println("\t\t +: " + op.getProperty().getNamedProperty().getIRI().getShortForm());                       
                    }
            
            System.out.println("Classes");
            System.out.println("--------------------------------");
            for (OWLClass cls : classes) {
                System.out.println("+: " + cls.getIRI().getShortForm());

                System.out.println(" \tObject Property Domain");
                for (OWLObjectPropertyDomainAxiom op : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {                        
                        if (op.getDomain().equals(cls)) {   
                            for(OWLObjectProperty oop : op.getObjectPropertiesInSignature()){
                                 System.out.println("\t\t +: " + oop.getIRI().getShortForm());
                            }
                            //System.out.println("\t\t +: " + op.getProperty().getNamedProperty().getIRI().getShortForm());
                        }
                    }

                    System.out.println(" \tData Property Domain");
                    for (OWLDataPropertyDomainAxiom dp : ontology.getAxioms(AxiomType.DATA_PROPERTY_DOMAIN)) {
                        if (dp.getDomain().equals(cls)) {   
                            for(OWLDataProperty odp : dp.getDataPropertiesInSignature()){
                                 System.out.println("\t\t +: " + odp.getIRI().getShortForm());
                            }
                            //System.out.println("\t\t +:" + dp.getProperty());
                        }
                    }
            }
            
            System.out.println("Individuals...");
            for (OWLIndividual indv : individuals) {    
                System.out.println("+: " + indv.toStringID());                                            
                for (OWLAnnotationProperty ap: indv.getAnnotationPropertiesInSignature())
                {                    
                    System.out.println("+: " + ap.getIRI());
                }
            }
            
           

        } catch (OWLOntologyCreationException ex) {
        }
}}