/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB.DAC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Mohammad Fasha
 */
public class DACOntology {

    DBConnection dbConnection;
    Connection con;
    PreparedStatement insertRowStmt;
    PreparedStatement deleteRowStm;
    Statement stmt;
    ResultSet resultSet;

    public DACOntology() throws ClassNotFoundException, SQLException {
        dbConnection = new DBConnection(1);//1 SQl 2 Access
        con = dbConnection.GetConnection();

        insertRowStmt = con.prepareStatement("insert into ontology (concept_id, domain,"
                + " range, subclass_of,exist_in_ontology, comment) values (?,?,?,?,?,?)");

        deleteRowStm = con.prepareStatement("delete from ontology where concept_id = ?");

        stmt = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
    }

    public void InsertRow(String paraConceptId, String paraDomain, String paraRange, String paraSubclassOf, String paraComment) throws SQLException {
        insertRowStmt.setString(1, paraConceptId);
        insertRowStmt.setString(2, paraDomain);
        insertRowStmt.setString(3, paraRange);
        insertRowStmt.setString(4, paraSubclassOf);
        insertRowStmt.setBoolean(5, true);
        insertRowStmt.setString(6, paraComment);

        insertRowStmt.executeUpdate();
    }

    public void DeleteRow(String paraConceptId) throws SQLException {

        deleteRowStm.setString(1, paraConceptId);

        deleteRowStm.executeUpdate();
    }

    public boolean ConceptExist(String paraConceptId) throws SQLException {

        String query = "select * from ontology where concept_id  = '" + paraConceptId + "'";
        resultSet = stmt.executeQuery(query);

        return resultSet.next();
    }

    public void SetAllConceptsAsOutOfOntology() throws SQLException {
        String query = "update ontology set exist_in_ontology = 'False'";
        int executeUpdate;
        executeUpdate = stmt.executeUpdate(query);
    }

}
