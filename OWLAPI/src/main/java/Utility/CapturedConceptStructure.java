/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

/**
 *
 * @author Mohammad Fasha
 */
public class CapturedConceptStructure {

    public int LineNumber;
    public String PatternId;
    public String ConceptId;
    public String Category;
    public String ArgumentOneClass;
    public String ArgumentOneValue;
    public String ArgumentOneValueStemmed;
    public String ArgumentOneValueBuckwalter;
    public String ArgumentOneExpandedPOS;
    public int ArgumentOneTokenId;
    public String ArgumentTwoClass;
    public String ArgumentTwoValue;
    public String ArgumentTwoValueStemmed;
    public String ArgumentTwoValueBuckwalter;
    public String ArgumentTwoExpandedPOS;
    public int ArgumentTwoTokenId;
    public String ParentClass;
    public String ConceptIdValidated;

    public CapturedConceptStructure() {
        ConceptIdValidated = "Not Validated";
    }

    public CapturedConceptStructure(CapturedConceptStructure ccs) {
        this.LineNumber = ccs.LineNumber;
        this.PatternId = ccs.PatternId;
        this.ConceptId = ccs.ConceptId;
        this.Category = ccs.Category;
        this.ArgumentOneClass = ccs.ArgumentOneClass;
        this.ArgumentOneValue = ccs.ArgumentOneValue;
        this.ArgumentOneValueStemmed = ccs.ArgumentOneValueStemmed;
        this.ArgumentOneValueBuckwalter = ccs.ArgumentOneValueBuckwalter;
        this.ArgumentOneExpandedPOS = ccs.ArgumentOneExpandedPOS;
        this.ArgumentOneTokenId = ccs.ArgumentOneTokenId;
        this.ArgumentTwoClass = ccs.ArgumentTwoClass;
        this.ArgumentTwoValue = ccs.ArgumentTwoValue;
        this.ArgumentTwoValueStemmed = ccs.ArgumentTwoValueStemmed;
        this.ArgumentTwoValueBuckwalter = ccs.ArgumentTwoValueBuckwalter;
        this.ArgumentTwoExpandedPOS = ccs.ArgumentTwoExpandedPOS;
        this.ArgumentTwoTokenId = ccs.ArgumentTwoTokenId;
        this.ParentClass = ccs.ParentClass;
        this.ConceptIdValidated = ccs.ConceptIdValidated;
    }
}
