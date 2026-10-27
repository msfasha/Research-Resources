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
@Table(name = "sentence")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sentence.findAll", query = "SELECT s FROM Sentence s"),
    @NamedQuery(name = "Sentence.findById", query = "SELECT s FROM Sentence s WHERE s.id = :id"),
    @NamedQuery(name = "Sentence.findByStoryId", query = "SELECT s FROM Sentence s WHERE s.storyId = :storyId"),
    @NamedQuery(name = "Sentence.findByLineNumber", query = "SELECT s FROM Sentence s WHERE s.lineNumber = :lineNumber"),
    @NamedQuery(name = "Sentence.findBySentenceText", query = "SELECT s FROM Sentence s WHERE s.sentenceText = :sentenceText"),
    @NamedQuery(name = "Sentence.findBySentenceExpandedPos", query = "SELECT s FROM Sentence s WHERE s.sentenceExpandedPos = :sentenceExpandedPos"),
    @NamedQuery(name = "Sentence.findByRegularExpressionReadyPos", query = "SELECT s FROM Sentence s WHERE s.regularExpressionReadyPos = :regularExpressionReadyPos"),
    @NamedQuery(name = "Sentence.findBySentenceOwlPos", query = "SELECT s FROM Sentence s WHERE s.sentenceOwlPos = :sentenceOwlPos"),
    @NamedQuery(name = "Sentence.findByParseTree", query = "SELECT s FROM Sentence s WHERE s.parseTree = :parseTree"),
    @NamedQuery(name = "Sentence.findByCorrectedParseTree", query = "SELECT s FROM Sentence s WHERE s.correctedParseTree = :correctedParseTree")})
public class Sentence implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "story_id")
    private Integer storyId;
    @Column(name = "line_number")
    private Integer lineNumber;
    @Column(name = "sentence_text")
    private String sentenceText;
    @Column(name = "sentence_expanded_pos")
    private String sentenceExpandedPos;
    @Column(name = "regular_expression_ready_pos")
    private String regularExpressionReadyPos;
    @Column(name = "sentence_owl_pos")
    private String sentenceOwlPos;
    @Column(name = "parse_tree")
    private String parseTree;
    @Column(name = "corrected_parse_tree")
    private String correctedParseTree;

    public Sentence() {
    }

    public Sentence(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getSentenceText() {
        return sentenceText;
    }

    public void setSentenceText(String sentenceText) {
        this.sentenceText = sentenceText;
    }

    public String getSentenceExpandedPos() {
        return sentenceExpandedPos;
    }

    public void setSentenceExpandedPos(String sentenceExpandedPos) {
        this.sentenceExpandedPos = sentenceExpandedPos;
    }

    public String getRegularExpressionReadyPos() {
        return regularExpressionReadyPos;
    }

    public void setRegularExpressionReadyPos(String regularExpressionReadyPos) {
        this.regularExpressionReadyPos = regularExpressionReadyPos;
    }

    public String getSentenceOwlPos() {
        return sentenceOwlPos;
    }

    public void setSentenceOwlPos(String sentenceOwlPos) {
        this.sentenceOwlPos = sentenceOwlPos;
    }

    public String getParseTree() {
        return parseTree;
    }

    public void setParseTree(String parseTree) {
        this.parseTree = parseTree;
    }

    public String getCorrectedParseTree() {
        return correctedParseTree;
    }

    public void setCorrectedParseTree(String correctedParseTree) {
        this.correctedParseTree = correctedParseTree;
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
        if (!(object instanceof Sentence)) {
            return false;
        }
        Sentence other = (Sentence) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DB.Sentence[ id=" + id + " ]";
    }
    
}
