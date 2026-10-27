/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mohammad Fasha
 */
@Entity
@Table(name = "sentence_concept")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SentenceConcept.findAll", query = "SELECT s FROM SentenceConcept s"),
    @NamedQuery(name = "SentenceConcept.findById", query = "SELECT s FROM SentenceConcept s WHERE s.id = :id"),
    @NamedQuery(name = "SentenceConcept.findBySentenceId", query = "SELECT s FROM SentenceConcept s WHERE s.sentenceId = :sentenceId"),
    @NamedQuery(name = "SentenceConcept.findByConceptSequenceNumber", query = "SELECT s FROM SentenceConcept s WHERE s.conceptSequenceNumber = :conceptSequenceNumber"),
    @NamedQuery(name = "SentenceConcept.findByConceptId", query = "SELECT s FROM SentenceConcept s WHERE s.conceptId = :conceptId"),
    @NamedQuery(name = "SentenceConcept.findByConceptPatternId", query = "SELECT s FROM SentenceConcept s WHERE s.conceptPatternId = :conceptPatternId"),
    @NamedQuery(name = "SentenceConcept.findByArgument1", query = "SELECT s FROM SentenceConcept s WHERE s.argument1 = :argument1"),
    @NamedQuery(name = "SentenceConcept.findByArgument1ConceptId", query = "SELECT s FROM SentenceConcept s WHERE s.argument1ConceptId = :argument1ConceptId"),
    @NamedQuery(name = "SentenceConcept.findByArgument2", query = "SELECT s FROM SentenceConcept s WHERE s.argument2 = :argument2"),
    @NamedQuery(name = "SentenceConcept.findByArgument2ConceptId", query = "SELECT s FROM SentenceConcept s WHERE s.argument2ConceptId = :argument2ConceptId"),
    @NamedQuery(name = "SentenceConcept.findByImplicit", query = "SELECT s FROM SentenceConcept s WHERE s.implicit = :implicit"),
    @NamedQuery(name = "SentenceConcept.findByConceptCompositeLevel", query = "SELECT s FROM SentenceConcept s WHERE s.conceptCompositeLevel = :conceptCompositeLevel"),
    @NamedQuery(name = "SentenceConcept.findByNote", query = "SELECT s FROM SentenceConcept s WHERE s.note = :note"),
    @NamedQuery(name = "SentenceConcept.findByNegation", query = "SELECT s FROM SentenceConcept s WHERE s.negation = :negation")})
public class SentenceConcept implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "sentence_id")
    private int sentenceId;
    @Basic(optional = false)
    @Column(name = "concept_sequence_number")
    private int conceptSequenceNumber;
    @Basic(optional = false)
    @Column(name = "concept_id")
    private String conceptId;
    @Column(name = "concept_pattern_id")
    private Integer conceptPatternId;
    @Column(name = "argument_1")
    private String argument1;
    @Column(name = "argument_1_concept_id")
    private String argument1ConceptId;
    @Column(name = "argument_2")
    private String argument2;
    @Column(name = "argument_2_concept_id")
    private String argument2ConceptId;
    @Column(name = "implicit")
    private Boolean implicit;
    @Column(name = "concept_composite_level")
    private String conceptCompositeLevel;
    @Column(name = "note")
    private String note;
    @Basic(optional = false)
    @Column(name = "negation")
    private boolean negation;

    public SentenceConcept() {
    }

    public SentenceConcept(Integer id) {
        this.id = id;
    }

    public SentenceConcept(Integer id, int sentenceId, int conceptSequenceNumber, String conceptId, boolean negation) {
        this.id = id;
        this.sentenceId = sentenceId;
        this.conceptSequenceNumber = conceptSequenceNumber;
        this.conceptId = conceptId;
        this.negation = negation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSentenceId() {
        return sentenceId;
    }

    public void setSentenceId(int sentenceId) {
        this.sentenceId = sentenceId;
    }

    public int getConceptSequenceNumber() {
        return conceptSequenceNumber;
    }

    public void setConceptSequenceNumber(int conceptSequenceNumber) {
        this.conceptSequenceNumber = conceptSequenceNumber;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public Integer getConceptPatternId() {
        return conceptPatternId;
    }

    public void setConceptPatternId(Integer conceptPatternId) {
        this.conceptPatternId = conceptPatternId;
    }

    public String getArgument1() {
        return argument1;
    }

    public void setArgument1(String argument1) {
        this.argument1 = argument1;
    }

    public String getArgument1ConceptId() {
        return argument1ConceptId;
    }

    public void setArgument1ConceptId(String argument1ConceptId) {
        this.argument1ConceptId = argument1ConceptId;
    }

    public String getArgument2() {
        return argument2;
    }

    public void setArgument2(String argument2) {
        this.argument2 = argument2;
    }

    public String getArgument2ConceptId() {
        return argument2ConceptId;
    }

    public void setArgument2ConceptId(String argument2ConceptId) {
        this.argument2ConceptId = argument2ConceptId;
    }

    public Boolean getImplicit() {
        return implicit;
    }

    public void setImplicit(Boolean implicit) {
        this.implicit = implicit;
    }

    public String getConceptCompositeLevel() {
        return conceptCompositeLevel;
    }

    public void setConceptCompositeLevel(String conceptCompositeLevel) {
        this.conceptCompositeLevel = conceptCompositeLevel;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean getNegation() {
        return negation;
    }

    public void setNegation(boolean negation) {
        this.negation = negation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SentenceConcept)) {
            return false;
        }
        SentenceConcept other = (SentenceConcept) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DAC.SentenceConcept[ id=" + id + " ]";
    }

}
