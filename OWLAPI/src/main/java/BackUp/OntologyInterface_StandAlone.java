/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BackUp;

//import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import Utility.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;

/**
 *
 * @author Mohammad Fasha
 */
public class OntologyInterface_StandAlone {

    static FileInputStream in;
    OWLOntology ontology;
    OWLOntologyManager manager;
    OWLReasoner reasoner;
    OWLDataFactory df;
    IRI ontologyIRI;

    public OntologyInterface_StandAlone(String paraOntologyFile) throws OWLOntologyStorageException, OWLOntologyCreationException, Exception {

        manager = OWLManager.createOWLOntologyManager();
        File file = new File(paraOntologyFile);
        ontology = manager.loadOntologyFromOntologyDocument(file);
        df = manager.getOWLDataFactory();

        ontologyIRI = IRI.create("AraOnto");
        //ontologyIRI = manager.getOntologyDocumentIRI(ontology);
        System.out.println(" from: " + ontologyIRI);
        OWLReasoner reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
    }

    public void CreateReasoner() {
        // OWLReasonerFactory reasonerFactory = new PelletReasonerFactory();
        // reasoner = reasonerFactory.createNonBufferingReasoner((org.semanticweb.owlapi.model.OWLOntology) ontology);
    }

    public void PrintClasses() {
        for (OWLClass cls : ontology.getClassesInSignature()) {
            System.out.println(cls);
        }
    }

    public void ListAllAxioms() {
        ontology.axioms().forEach(System.out::println);
    }

    public void ListAllAxiomsMethodTwo() {
        System.out.println(ontology.axioms().toString());
    }

    public void ListAllAxiomsMethodThree() {
        for (Iterator iterator = ontology.axioms().iterator(); iterator.hasNext();) {
            Object next = iterator.next();
            System.out.println(next.toString());
        }
    }

    public void ListAllAxiomsMethodFour() {
        for (OWLAxiom ax : ontology.getAxioms()) {
            System.out.println(ontology.getOntologyID().getOntologyIRI() + ": " + ax);
        }
    }

    public void ConvertAxiomsToArray() {
        Object[] toArray = ontology.axioms().toArray();
    }

    public void PrintOntologyInfo() {
        String iri = "MyOnto";
        IRI documentIRI = IRI.create(iri);
        System.out.println("LanguageTransducer Loaded...");
        System.out.println("Document IRI: " + documentIRI);
        System.out.println("LanguageTransducer : " + ontology.getOntologyID());
        System.out.println("Format      : " + manager.getOntologyFormat(ontology));
    }

    public void GetFilteredAxiomsContains(String paraString) {
        List<OWLAxiom> filtered = ontology.axioms().filter(x -> x.toString().contains(paraString)).collect(Collectors.toList());
        //System.out.println(filtered);
        System.out.println(filtered.get(0));
    }

    public void GetAxiomsEquals(String paraString) {
        List<OWLAxiom> filtered = ontology.axioms().filter(x -> x.toString().equalsIgnoreCase(paraString)).collect(Collectors.toList());

        System.out.println(filtered);
    }

    public void GetAnnotations() {
        OWLAnnotationProperty property = df.getOWLAnnotationProperty(IRI.create("AraOntol#root"));

        System.out.println(property.getClass().toString());
        //df.get(property, documentIRI)
        //o.annotationAssertionAxioms().forEach(System.out::println);
    }

    public void GetOntologyAnnotations() {
        for (OWLAnnotation ann : ontology.getAnnotations()) {
            System.out.println(ann.getValue().toString());
        }
    }

    public void GetAnnotationAxiomBySubject() {
        IRI subject = IRI.create("AraOnto#break");
        for (OWLAnnotationAssertionAxiom oa : ontology.getAnnotationAssertionAxioms(subject)) {
            System.out.println(oa.getValue().toString());
            System.out.println(oa.getSubject().toString());
        }
    }

    public Set<CapturedConceptStructure> GetObjectProperties() {
        Set<OWLObjectProperty> propSet;

        Set<CapturedConceptStructure> resultSet = new HashSet<CapturedConceptStructure>();
        propSet = ontology.getObjectPropertiesInSignature();

        for (OWLObjectProperty p : propSet) {
            CapturedConceptStructure ct = new CapturedConceptStructure();

            for (OWLObjectPropertyDomainAxiom d : ontology.getObjectPropertyDomainAxioms(p)) {
                ct.ArgumentOneClass = d.getDomain().toString().substring(d.getDomain().toString().indexOf("#") + 1, d.getDomain().toString().length()).replaceAll("[^a-zA-Z]", "");
            }
            for (OWLObjectPropertyRangeAxiom r : ontology.getObjectPropertyRangeAxioms(p)) {
                ct.ArgumentTwoClass = r.getRange().toString().substring(r.getRange().toString().indexOf("#") + 1, r.getRange().toString().length()).replaceAll("[^a-zA-Z]", "");
            }

            ct.ConceptId = p.toStringID().substring(p.toStringID().indexOf("#") + 1, p.toStringID().length()).replaceAll("[^a-zA-Z]", "");
            ct.Category = ConceptCategoryDefinition.ObjectCategoryLabel;

            resultSet.add(ct);

        }
        return resultSet;
    }

    public Set<CapturedConceptStructure> GetClasses() {
        Set<OWLClass> classSet;

        classSet = ontology.getClassesInSignature(true);

        Set<CapturedConceptStructure> resultSet = new HashSet<CapturedConceptStructure>();

        for (OWLClass cls : classSet) {
            CapturedConceptStructure ct = new CapturedConceptStructure();
            String str = cls.getIRI().getShortForm();
            str = str.substring(str.indexOf("#") + 1, str.length());
            str = str.replaceAll("[^a-zA-Z]", "");
            ct.ConceptId = str;
            ct.Category = ConceptCategoryDefinition.ClassLabel;

            if (!cls.isTopEntity()) {
                ct.ParentClass = cls.getClass().getSuperclass().getSimpleName();
            }

            resultSet.add(ct);
        }

        return resultSet;

    }

    public List<CapturedConceptStructure> ParseOntology() {
        Set<CapturedConceptStructure> resultSet1 = GetObjectProperties();
        Set<CapturedConceptStructure> resultSet2 = GetClasses();

        List<CapturedConceptStructure> resultList = new ArrayList<>();
        resultList.addAll(resultSet1);
        resultList.addAll(resultSet2);

        return resultList;
    }

    //get the individual object that is annotated with a word similar to the one passed as parameter
    public OWLIndividual getIndividualByAnnotationString(String paraAnnotationString) {
        Set<OWLNamedIndividual> individuals;
        individuals = ontology.getIndividualsInSignature();

        for (OWLIndividual indv : individuals) {
            IRI subject = IRI.create(indv.toStringID());
            for (OWLAnnotationAssertionAxiom oa : ontology.getAnnotationAssertionAxioms(subject)) {
                if (oa.getValue().toString().contains(paraAnnotationString)) {
                    return indv;
                }
            }
        }

        return null;
    }

    //get the class object that is annotated with a word similar to the one passed as parameter
    public OWLClass getClassObjectByAnnotationString(String paraAnnotationString) {
        Set<OWLClass> classes;
        classes = ontology.getClassesInSignature();

        for (OWLClass cls : classes) {
            IRI subject = IRI.create(cls.toStringID());
            for (OWLAnnotationAssertionAxiom oa : ontology.getAnnotationAssertionAxioms(subject)) {
                if (oa.getValue().toString().contains(paraAnnotationString)) {
                    return cls;
                }
            }
        }

        return null;
    }

    //get the class name that is annotated with a word similar to the one passed as parameter
    public String getClassNameByAnnotationValue(String paraAnnotationString) {
        Set<OWLClass> classes;
        classes = ontology.getClassesInSignature();

        for (OWLClass cls : classes) {
            IRI subject = IRI.create(cls.toStringID());
            for (OWLAnnotationAssertionAxiom oa : ontology.getAnnotationAssertionAxioms(subject)) {
                if (oa.getValue().toString().contains(paraAnnotationString)) {
                    return cls.getIRI().getShortForm();
                }
            }
        }

        return null;
    }

    //pass OWLClass as name string
    public OWLIndividual CreateIndividualByClassName(String paraParentClassName, String paraIndividualName) throws OWLOntologyStorageException {

        OWLClass clsParent = df.getOWLClass(IRI.create(ontologyIRI + "#" + paraParentClassName));
        OWLIndividual individual = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + paraIndividualName));

        OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(clsParent, individual);
        AddAxiom addAxiom = new AddAxiom(ontology, classAssertion);
        manager.applyChange(addAxiom);
        //manager.saveOntology(ontology);

        return individual;
    }

    //pass OWLClass as object
    public OWLIndividual CreateIndividualByClassObject(OWLClass paraParentClass, String paraIndividualName) throws OWLOntologyStorageException {
        OWLIndividual individual = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + paraIndividualName));

        OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(paraParentClass, individual);
        AddAxiom addAxiom = new AddAxiom(ontology, classAssertion);
        manager.applyChange(addAxiom);
        //manager.saveOntology(ontology);

        return individual;
    }

    //pass classes as name strings vs. objects
    public void CreateObjectRelationByNameString(String paraObjectRelation, String paraArgumentOneValue, String paraArguementTwoValue) throws OWLOntologyStorageException {
        OWLObjectProperty objectRelation = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + paraObjectRelation));

        OWLIndividual individual1 = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + paraArgumentOneValue));
        OWLIndividual individual2 = df.getOWLNamedIndividual(IRI.create(ontologyIRI + "#" + paraArguementTwoValue));

        OWLObjectPropertyAssertionAxiom axiom1 = df
                .getOWLObjectPropertyAssertionAxiom(objectRelation, individual1, individual2);

        AddAxiom addAxiom1 = new AddAxiom(ontology, axiom1);
        // Now we apply the change using the manager.
        manager.applyChange(addAxiom1);

        //manager.saveOntology(ontology);
    }
    
    public void CreateArabicAnnotationAssertionAxiom(String paraName, String paraValue)
    {        
        IRI individualIRI = IRI.create(ontologyIRI + "#" + paraName);
        OWLAnnotationProperty URL = df.getOWLAnnotationProperty(IRI.create(ontologyIRI + "#ar"));
        OWLAnnotationValue value = df.getOWLLiteral(paraValue);
        OWLAnnotation owlAnnotation = df.getOWLAnnotation(URL, value);
        OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(individualIRI, owlAnnotation);
	manager.applyChange(new AddAxiom(ontology, ax));        
    }
    
    //pass classes as objects
    public void CreateObjectRelationByObjectValues(String paraObjectRelation, OWLIndividual paraArgumentOneValue, OWLIndividual paraArguementTwoValue) throws OWLOntologyStorageException {
        OWLObjectProperty objectRelation = df.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + paraObjectRelation));

        OWLObjectPropertyAssertionAxiom axiom1 = df
                .getOWLObjectPropertyAssertionAxiom(objectRelation, paraArgumentOneValue, paraArguementTwoValue);

        AddAxiom addAxiom1 = new AddAxiom(ontology, axiom1);
        // Now we apply the change using the manager.
        manager.applyChange(addAxiom1);

        //manager.saveOntology(ontology);
    }

    public void SaveOntology() throws OWLOntologyStorageException {
        manager.saveOntology(ontology);
    }
}
