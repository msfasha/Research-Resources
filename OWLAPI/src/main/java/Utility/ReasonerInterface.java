/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import DataProcessing.ConceptExtractor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

/**
 *
 * @author Mohammad Fasha
 */
public class ReasonerInterface {

    private final OWLReasoner reasoner;
    private final OWLOntologyManager manager;
    File file;
    OWLOntology ontology;
    ShortFormProvider shortFormProvider;
    private final BidirectionalShortFormProvider bidiShortFormProvider;

    public ReasonerInterface(String paraOntologyFile) throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        file = new File(paraOntologyFile);
        ontology = manager.loadOntologyFromOntologyDocument(file);
        shortFormProvider = new SimpleShortFormProvider();

        reasoner = new Reasoner.ReasonerFactory().createReasoner(ontology);
        Set<OWLOntology> importsClosure = ontology.getImportsClosure();
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure, shortFormProvider);

    }

    public ReasonerInterface() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean ISSuperClassOf(String paraParentClass, String paraChildClass) {
        boolean found = false;
        List<String> arr = GetSuperClasses(paraChildClass);

        for (String str : arr) {
            if (str.equalsIgnoreCase(paraParentClass)) {
                found = true;
            }
        }
        return found;
    }

    public List<String> GetSuperClasses(String paraClassExpression) {
        StringBuilder sb = new StringBuilder();
        sb.append("QUERY:   ");
        sb.append(paraClassExpression);
        sb.append(System.getProperty("line.separator"));

        Set<OWLClass> superClasses = getSuperClasses(paraClassExpression, false);

        ArrayList<String> arr = new ArrayList();

        superClasses.stream().forEach((owlClass) -> {
            arr.add(shortFormProvider.getShortForm(owlClass));
        });
        return arr;
    }

    public List<String> GetInstances(String paraClassExpression) {
        StringBuilder sb = new StringBuilder();
        sb.append("QUERY:   ");
        sb.append(paraClassExpression);
        sb.append(System.getProperty("line.separator"));

        Set<OWLNamedIndividual> instances = GetInstances(sb.toString(), false);

        ArrayList<String> arr = new ArrayList();

        instances.stream().forEach((owlClass) -> {
            arr.add(shortFormProvider.getShortForm(owlClass));
        });
        return arr;
    }

    private Set<OWLClass> getSuperClasses(String classExpressionString, boolean direct) {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }

        OWLClassExpression classExpression = parseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(classExpression, direct);
        return superClasses.getFlattened();
    }

    public Set<OWLNamedIndividual> GetInstances(String classExpressionString, boolean direct) {
        try{
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression,
                direct);
        return individuals.getFlattened();
        } catch (Exception ex) {
            Logger.getLogger(ConceptExtractor.class.getName()).log(Level.SEVERE, null, ex.toString());
            return null;
        }
    }

     public boolean HasInstances(String classExpressionString) {
        if (GetInstances(classExpressionString, false).size() > 0) {
            return true;
        } else {
            return false;
        }
    }
     
    private OWLClassExpression parseClassExpression(String classExpressionString) {
        OWLDataFactory dataFactory = ontology.getOWLOntologyManager()
                .getOWLDataFactory();
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                dataFactory, classExpressionString);
        parser.setDefaultOntology(ontology);
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        return parser.parseClassExpression();
    }
}
