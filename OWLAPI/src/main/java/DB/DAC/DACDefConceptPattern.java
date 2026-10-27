/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB.DAC;

import Utility.Globals;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Mohammad Fasha
 */
public class DACDefConceptPattern {

    DBConnection dbConnection;
    Connection con;
    String query;
    Statement stmt;

    public DACDefConceptPattern() throws ClassNotFoundException, SQLException {
        dbConnection = new DBConnection(1);//1 SQl 2 Access
        con = dbConnection.GetConnection();
    }

    public ResultSet GetAllRows() throws SQLException {
        stmt = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        query = "select * from def_concept_pattern where disabled = 0";

        return stmt.executeQuery(query);
    }

    public ResultSet GetPatternsByConceptId(String paraConceptId) throws SQLException {
        stmt = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        query = "select * from def_concept_pattern where disabled = 0 and concept_id = '" + paraConceptId + "'";

        return stmt.executeQuery(query);
    }

    public ResultSet GetPatternsOfRemainingConcepts() throws SQLException {
        stmt = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        query = "select * from def_concept_pattern where disabled = 0 and concept_id not in ('"
                + Globals.RegexPattern_Event + "','"
                + Globals.RegexPattern_Agent + "','"
                + Globals.RegexPattern_ISTheAgentOfEvent + "','"
                + Globals.RegexPattern_ISTheObjectOfEvent + "','"
                + Globals.RegexPattern_SFXOBJ + "','"
                + Globals.RegexPattern_Nouns + "')";

        return stmt.executeQuery(query);
    }

}
