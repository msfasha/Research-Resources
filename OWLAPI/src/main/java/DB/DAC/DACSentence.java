/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB.DAC;

import DB.Sentence;
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
public class DACSentence {

    DBConnection dbConnection;
    Connection con;
    String query;
    Statement getRowsReadOnlyStm;
    Statement getRowsUpdatableStm;
    PreparedStatement insertRowStmt;
    ResultSet MainRS;

    public DACSentence() throws ClassNotFoundException, SQLException {
        dbConnection = new DBConnection(1);//1 SQl 2 Access
        con = dbConnection.GetConnection();

        getRowsReadOnlyStm = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);

        insertRowStmt = con.prepareStatement("insert into sentence (id, story_id, line_number, sentence_text, sentence_expanded_pos, regular_expression_ready_pos, sentence_owl_pos, parse_tree, corrected_parse_tree) values (?,?,?,?,?,?,?,?,?)");

        getRowsUpdatableStm = con.createStatement(
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
    }

    public void InitializeRowSet() throws SQLException {
        if (MainRS != null) {
            if (!MainRS.isClosed()) {
                MainRS.close();
            }
            MainRS = null;
        }

        String sql = ("select * from sentence");

        MainRS = getRowsUpdatableStm.executeQuery(sql);
        MainRS.first();
    }

    public ResultSet GetAllRows() throws SQLException {

        query = "select * from sentence";

        return getRowsReadOnlyStm.executeQuery(query);
    }

    public ResultSet GetAllRowsByStoryId(int story_id) throws SQLException {

        query = "select * from sentence where story_id =" + story_id;

        return getRowsReadOnlyStm.executeQuery(query);
    }

    public void InsertRow(int paraId, int paraStoryID, int paraLineNumber, String paraSentenceText, String paraSentenceExpandedPOS, String paraParseTree) throws SQLException {
        insertRowStmt.setInt(1, paraId);
        insertRowStmt.setInt(2, paraStoryID);
        insertRowStmt.setInt(3, paraLineNumber);
        insertRowStmt.setString(4, paraSentenceText);
        insertRowStmt.setString(5, paraSentenceExpandedPOS);
        insertRowStmt.setString(6, paraSentenceExpandedPOS);//Regular Expression Ready POS        
        insertRowStmt.setString(7, paraSentenceExpandedPOS);//Sentence OWL, just make a copy        
        insertRowStmt.setString(8, paraParseTree);
        insertRowStmt.setString(9, paraParseTree);//Corrected ParseTree Field, just make a copy

        insertRowStmt.executeUpdate();
    }

    public void DeleteAllRows() throws SQLException {
        PreparedStatement delStmt = con.prepareStatement("delete from sentence");
        delStmt.executeUpdate();
    }

    public int GetLastStoryId() throws SQLException {
        String sql;
        sql = "select max(story_id) from sentence";

        int result;
        PreparedStatement statement;
        statement = con.prepareStatement(sql);

        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            result = rs.getInt(1);
        } else {
            result = 0;
        }
        return result;
    }

    
    public int GetLastId() throws SQLException {
        String sql;
        sql = "select max(id) from sentence";

        int result;
        PreparedStatement statement;
        statement = con.prepareStatement(sql);

        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            result = rs.getInt(1);
        } else {
            result = 0;
        }
        return result;
    }
    public Sentence MoveRecord(EnumRecordMovementDirection paraDirection) throws SQLException {
        if (isResultSetEmpty()) {
            return null;//Empty RS, return null            
        }

        switch (paraDirection) {
            case First:
                MainRS.first();
                break;
            case Next:
                if (MainRS.isLast()) {
                    MainRS.first();//if last record and RS not empty then move to first
                } else {
                    MainRS.next();//else just move to the next record        
                }
                break;
            case Previous:
                if (MainRS.isFirst()) {
                    MainRS.last();//if first record and RS not empty then move to last - like a closed loop
                } else {
                    MainRS.previous();//else just move to the previous record        
                }
                break;
            case Last:
                MainRS.last();
                break;
            default:
                break;
        }

        return GetCurrentRecord();
    }

    public Sentence GetCurrentRecord() throws SQLException {

        if (MainRS.isFirst() & MainRS.isLast()) {
            return null;
        }

        Sentence s = new Sentence();

        s.setId(MainRS.getInt("id"));
        s.setStoryId(MainRS.getInt("story_id"));
        s.setLineNumber(MainRS.getInt("Line_number"));
        s.setSentenceText(MainRS.getString("sentence_text"));
        s.setSentenceExpandedPos(MainRS.getString("sentence_expanded_pos"));
        s.setSentenceOwlPos(MainRS.getString("sentence_owl_pos"));
        s.setParseTree(MainRS.getString("parse_tree"));
        s.setCorrectedParseTree(MainRS.getString("corrected_parse_tree"));

        return s;
    }

    public void SaveRecord(Sentence paraSentence) throws SQLException {

        MainRS.updateString("sentence_text", paraSentence.getSentenceText());
        MainRS.updateString("sentence_expanded_pos", paraSentence.getSentenceExpandedPos());
        MainRS.updateString("sentence_owl_pos", paraSentence.getSentenceOwlPos());

        MainRS.updateString("parse_tree", paraSentence.getParseTree());
        MainRS.updateString("corrected_parse_tree", paraSentence.getCorrectedParseTree());

        
        //prepare a version of expanded_pos which is more suitable for regular expressions
        //that is replace regular expression known control characters with neutal ones        
        MainRS.updateString("regular_expression_ready_pos",
                Utility.Globals.MakeRegularExpressionReady(
                        Utility.Globals.StripDigitsAndColons(paraSentence.getSentenceExpandedPos())));

        MainRS.updateRow();
    }

    public boolean isResultSetEmpty() throws SQLException {
        return (!MainRS.isBeforeFirst() && MainRS.getRow() == 0);
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
