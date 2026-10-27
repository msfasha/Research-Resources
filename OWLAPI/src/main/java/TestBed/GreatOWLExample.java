/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBed;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

public class GreatOWLExample
{
    public static void main(String[] args)
    throws org.semanticweb.owlapi.model.OWLOntologyStorageException, org.semanticweb.owlapi.model.OWLOntologyCreationException, Exception
    {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory df = manager.getOWLDataFactory();
        OWLOntology o = manager.createOntology();

       //------------------------------------------------------------------

        OWLClass clsA = df.getOWLClass( IRI.create("#A") );
        OWLClass clsB = df.getOWLClass( IRI.create("#B") );
        OWLClass clsC = df.getOWLClass( IRI.create("#C") );

        OWLObjectProperty hasPart = df.getOWLObjectProperty( IRI.create("#hasPart"));
        OWLObjectPropertyDomainAxiom domainAxiom = df.getOWLObjectPropertyDomainAxiom(hasPart, clsA);
        OWLObjectPropertyRangeAxiom   rangeAxiom = df.getOWLObjectPropertyRangeAxiom( hasPart, clsB);

        manager.addAxiom(o, domainAxiom);
        manager.addAxiom(o,  rangeAxiom);

       //------------------------------------------------------------------

        OWLNamedIndividual a1 = df.getOWLNamedIndividual( IRI.create("a1") );
        OWLNamedIndividual b1 = df.getOWLNamedIndividual( IRI.create("b1") );
        OWLNamedIndividual c1 = df.getOWLNamedIndividual( IRI.create("c1") );
        OWLNamedIndividual c2 = df.getOWLNamedIndividual( IRI.create("c2") );

        manager.addAxiom(o, df.getOWLClassAssertionAxiom(clsA, a1));
        manager.addAxiom(o, df.getOWLClassAssertionAxiom(clsB, b1));
        manager.addAxiom(o, df.getOWLClassAssertionAxiom(clsC, c1));
        manager.addAxiom(o, df.getOWLClassAssertionAxiom(clsC, c2));
        
        manager.addAxiom(o, df.getOWLObjectPropertyAssertionAxiom(hasPart, c1, c2));   // ObjectProperty '#hasPart' should only work for objects from Domain 'clsA' and Range 'clsB'

       //------------------------------------------------------------------

        manager.saveOntology(o, IRI.create("file:/tmp/data.owl"));
    }
}