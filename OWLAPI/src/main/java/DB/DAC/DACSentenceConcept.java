/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB.DAC;

import DB.SentenceConcept;
import Utility.EnumRecordMovementDirection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mohammad Fasha
 */
public class DACSentenceConcept {

    DBConnection dbConnection;
    Connection con;
    String query;
    Statement getRowsReadOnlyStm;
    PreparedStatement stmt;
    ResultSet MainRS;
    int currentSentenceId;

    public DACSentenceConcept() throws ClassNotFoundException, SQLException {
        dbConnection = new DBConnection(1);//1 SQl 2 Access
        con = dbConnection.GetConnection();

        getRowsReadOnlyStm = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        stmt = con.prepareStatement("insert into sentence_concept "
                + "(sentence_id, concept_sequence_number, concept_id, concept_pattern_id, argument_1,argument_1_concept_id, argument_2, argument_2_concept_id, inference_type, concept_composite_level, note, negation)"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?)");
    }

    public void InitializeRowSet(int paraSentenceId) throws SQLException {
        currentSentenceId = paraSentenceId;
        if (MainRS != null) {
            if (!MainRS.isClosed()) {
                MainRS.close();
            }
            MainRS = null;
        }

        Statement statement = con.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);

        String sql = ("select * from sentence_concept where sentence_id = " + paraSentenceId);

        MainRS = statement.executeQuery(sql);
    }

    public void DeleteAllRows() throws SQLException {
        PreparedStatement delStmt = con.prepareStatement("delete from sentence_concept");
        delStmt.executeUpdate();
    }
    
    public void DeleteCurrentRow(int paraId) throws SQLException {
        PreparedStatement delStmt = con.prepareStatement("delete from sentence_concept where id = " + paraId);
        delStmt.executeUpdate();
    }

    public ResultSet GetAllRowsByStoryId(int paraStoryId) throws SQLException {
        query = "SELECT sentence_concept.* FROM sentence_concept WHERE sentence_id in (select sentence_id from sentence where story_id= " + paraStoryId + ") order by sentence_id;";

        return getRowsReadOnlyStm.executeQuery(query);
    }

    public void InsertRow(int paraSentenceIid, int paraConceptSequenceNumber,
            String paraConceptId, int paraConceptPatternId,
            String paraArgument1, String paraArgument1ConceptId, String paraArgument2, String paraArgument2ConceptId, String paraInferenceType,
            String paraCompositeLevel, String paraNote, boolean paraNegation) throws SQLException {
        stmt.setInt(1, paraSentenceIid);
        stmt.setInt(2, paraConceptSequenceNumber);
        stmt.setString(3, paraConceptId);
        stmt.setInt(4, paraConceptPatternId);
        stmt.setString(5, paraArgument1);
        stmt.setString(6, paraArgument1ConceptId);
        stmt.setString(7, paraArgument2);
        stmt.setString(8, paraArgument2ConceptId);
        stmt.setString(9, paraInferenceType);
        stmt.setString(10, paraCompositeLevel);
        stmt.setString(11, paraCompositeLevel);
        stmt.setBoolean(12, paraNegation);

        stmt.executeUpdate();
    }

    public SentenceConcept MoveRecord(EnumRecordMovementDirection paraDirection) throws SQLException {
//All validations for empty resultset are performed using the next() method
//therefore, all movements are created taking into consideration this step and
//how to adjust it to compensate for its effects
        switch (paraDirection) {
            case First:
                //MainRS.first();
                MainRS.beforeFirst();
                break;
            case Next://the only situation where we move to new record position
                if (MainRS.isLast()) {
                    MainRS.moveToInsertRow();
                    return GetNewRecord();
                } else {
                    //MainRS.next();//else just move to the next record        
                }
                break;
            case Previous:
                if (MainRS.isFirst()) {
                    MainRS.beforeFirst();
//Do not move
                } else {
                    MainRS.previous();//else just move to the previous record        
                    MainRS.previous();
                }
                break;
            case Last:
                MainRS.last();
                MainRS.previous();
                break;
            default:
                break;
        }

        return GetRecord();
    }

    public SentenceConcept GetRecord() {
        try {
            if (isResultSetEmpty()) {
                return GetNewRecord();
            } else {
                return GetCurrentRecord();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DACSentenceConcept.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public SentenceConcept GetCurrentRecord() throws SQLException {
        SentenceConcept sc = new SentenceConcept();

        sc.setId(MainRS.getInt("id"));
        sc.setSentenceId(MainRS.getInt("sentence_id"));
        sc.setConceptSequenceNumber(MainRS.getInt("concept_sequence_number"));
        sc.setConceptId(MainRS.getString("concept_id"));
        sc.setConceptPatternId(MainRS.getInt("concept_pattern_id"));
        sc.setArgument1(MainRS.getString("argument_1"));
        sc.setArgument1ConceptId(MainRS.getString("argument_1_concept_id"));
        sc.setArgument2(MainRS.getString("argument_2"));
        sc.setArgument2ConceptId(MainRS.getString("argument_2_concept_id"));
        sc.setImplicit(MainRS.getBoolean("implicit"));
        sc.setConceptCompositeLevel(MainRS.getString("concept_composite_level"));
        sc.setNote(MainRS.getString("note"));
        sc.setNegation(MainRS.getBoolean("negation"));

        return sc;
    }

    private SentenceConcept GetNewRecord() throws SQLException {
        SentenceConcept sc = new SentenceConcept();

        sc.setId(-1);
        sc.setSentenceId(currentSentenceId);
        sc.setConceptSequenceNumber(GetNextSequenceNumber());
        sc.setConceptPatternId(0);
        sc.setImplicit(false);
        sc.setConceptCompositeLevel("Atomic");
        sc.setNegation(false);

        //all other values are initilized to null        
        return sc;
    }

    public void SaveRecord(SentenceConcept paraSentenceConcept) throws SQLException {

        if (paraSentenceConcept.getId() == -1)//new record
        {
            MainRS.moveToInsertRow();
        }

        MainRS.updateInt("sentence_id", paraSentenceConcept.getSentenceId());
        MainRS.updateInt("concept_sequence_number", paraSentenceConcept.getConceptSequenceNumber());
        MainRS.updateString("concept_id", paraSentenceConcept.getConceptId());
        MainRS.updateInt("concept_pattern_id", paraSentenceConcept.getConceptPatternId());
        MainRS.updateString("argument_1", paraSentenceConcept.getArgument1());
        MainRS.updateString("argument_1_concept_id", paraSentenceConcept.getArgument1ConceptId());
        MainRS.updateString("argument_2", paraSentenceConcept.getArgument2());
        MainRS.updateString("argument_2_concept_id", paraSentenceConcept.getArgument2ConceptId());
        MainRS.updateBoolean("implicit", paraSentenceConcept.getImplicit());
        MainRS.updateString("concept_composite_level", paraSentenceConcept.getConceptCompositeLevel());
        MainRS.updateString("note", paraSentenceConcept.getNote());
        MainRS.updateBoolean("negation", paraSentenceConcept.getNegation());

        if (paraSentenceConcept.getId() == -1)//new record
        {
            MainRS.insertRow();
            MainRS.last();
        } else {
            MainRS.updateRow();
        }
    }

    private int GetNextSequenceNumber() throws SQLException {
        String sql;
        sql = "select count(concept_sequence_number) from sentence_concept where sentence_id = " + currentSentenceId;

        int result;
        PreparedStatement statement;
        statement = con.prepareStatement(sql);

        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            result = rs.getInt(1);
        } else {
            result = 0;
        }
        return result + 1;
    }

    private boolean isResultSetEmpty() throws SQLException {
        return !MainRS.next();
    }

    public void Dispose() {
        try {
            if (con != null) {
                con.close();
            }

            if (MainRS != null) {
                if (!MainRS.isClosed()) {
                    MainRS.close();
                }
                MainRS = null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DACSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
