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
@Table(name = "def_tag")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DefTag.findAll", query = "SELECT d FROM DefTag d"),
    @NamedQuery(name = "DefTag.findById", query = "SELECT d FROM DefTag d WHERE d.id = :id"),
    @NamedQuery(name = "DefTag.findByTagOrder", query = "SELECT d FROM DefTag d WHERE d.tagOrder = :tagOrder"),
    @NamedQuery(name = "DefTag.findByTag", query = "SELECT d FROM DefTag d WHERE d.tag = :tag"),
    @NamedQuery(name = "DefTag.findByShortName", query = "SELECT d FROM DefTag d WHERE d.shortName = :shortName"),
    @NamedQuery(name = "DefTag.findByEnglishDescription", query = "SELECT d FROM DefTag d WHERE d.englishDescription = :englishDescription"),
    @NamedQuery(name = "DefTag.findByArabicDescription", query = "SELECT d FROM DefTag d WHERE d.arabicDescription = :arabicDescription"),
    @NamedQuery(name = "DefTag.findByTagCategory", query = "SELECT d FROM DefTag d WHERE d.tagCategory = :tagCategory"),
    @NamedQuery(name = "DefTag.findByArabicClassification", query = "SELECT d FROM DefTag d WHERE d.arabicClassification = :arabicClassification"),
    @NamedQuery(name = "DefTag.findByPennStd", query = "SELECT d FROM DefTag d WHERE d.pennStd = :pennStd"),
    @NamedQuery(name = "DefTag.findByTooltip", query = "SELECT d FROM DefTag d WHERE d.tooltip = :tooltip")})
public class DefTag implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "tag_order")
    private Integer tagOrder;
    @Basic(optional = false)
    @Column(name = "tag")
    private String tag;
    @Column(name = "short_name")
    private String shortName;
    @Basic(optional = false)
    @Column(name = "english_description")
    private String englishDescription;
    @Basic(optional = false)
    @Column(name = "arabic_description")
    private String arabicDescription;
    @Basic(optional = false)
    @Column(name = "tag_category")
    private String tagCategory;
    @Column(name = "arabic_classification")
    private String arabicClassification;
    @Basic(optional = false)
    @Column(name = "PENN_STD")
    private boolean pennStd;
    @Column(name = "tooltip")
    private String tooltip;

    public DefTag() {
    }

    public DefTag(Integer id) {
        this.id = id;
    }

    public DefTag(Integer id, String tag, String englishDescription, String arabicDescription, String tagCategory, boolean pennStd) {
        this.id = id;
        this.tag = tag;
        this.englishDescription = englishDescription;
        this.arabicDescription = arabicDescription;
        this.tagCategory = tagCategory;
        this.pennStd = pennStd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTagOrder() {
        return tagOrder;
    }

    public void setTagOrder(Integer tagOrder) {
        this.tagOrder = tagOrder;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }

    public void setEnglishDescription(String englishDescription) {
        this.englishDescription = englishDescription;
    }

    public String getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(String arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    public String getTagCategory() {
        return tagCategory;
    }

    public void setTagCategory(String tagCategory) {
        this.tagCategory = tagCategory;
    }

    public String getArabicClassification() {
        return arabicClassification;
    }

    public void setArabicClassification(String arabicClassification) {
        this.arabicClassification = arabicClassification;
    }

    public boolean getPennStd() {
        return pennStd;
    }

    public void setPennStd(boolean pennStd) {
        this.pennStd = pennStd;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
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
        if (!(object instanceof DefTag)) {
            return false;
        }
        DefTag other = (DefTag) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DAC.DefTag[ id=" + id + " ]";
    }
    
}
