/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB.DAC;

import DB.DefTag;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mohammad Fasha
 */
public class DACDefTag {

    DBConnection dbConnection;
    Connection con;
    String query;
    Statement getRowsReadOnlyStm;

    public DACDefTag() throws ClassNotFoundException, SQLException {
        dbConnection = new DBConnection(1);//1 SQl 2 Access
        con = dbConnection.GetConnection();

        getRowsReadOnlyStm = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
    }

    public ArrayList<DefTag> GetAllRowsArrayOrderedByTagOrder() throws SQLException {

        query = "select * from def_tag order by tag_order";

        ResultSet rs = getRowsReadOnlyStm.executeQuery(query);

        ArrayList<DefTag> defTagArray = new ArrayList();

        while (rs.next()) {
            DefTag dt = new DefTag();

            dt.setId(rs.getInt(1));
            dt.setTagOrder(rs.getInt(2));
            dt.setTag(rs.getString(3));
            dt.setShortName(rs.getString(4));
            dt.setEnglishDescription(rs.getString(5));
            dt.setArabicDescription(rs.getString(6));
            dt.setTagCategory(rs.getString(7));
            dt.setArabicClassification(rs.getString(8));
            dt.setTooltip(rs.getString(9));

            defTagArray.add(dt);
        }
        return defTagArray;
    }

    public ArrayList<DefTag> GetAllRowsArrayOrderedByTagCategoryAndShortName() throws SQLException {

        query = "select * from def_tag order by tag_category,short_name";

        ResultSet rs = getRowsReadOnlyStm.executeQuery(query);

        ArrayList<DefTag> defTagArray = new ArrayList();

        while (rs.next()) {
            DefTag dt = new DefTag();

            dt.setId(rs.getInt(1));
            dt.setTagOrder(rs.getInt(2));
            dt.setTag(rs.getString(3));
            dt.setShortName(rs.getString(4));
            dt.setEnglishDescription(rs.getString(5));
            dt.setArabicDescription(rs.getString(6));
            dt.setTagCategory(rs.getString(7));
            dt.setArabicClassification(rs.getString(8));
            dt.setTooltip(rs.getString(9));

            defTagArray.add(dt);
        }
        return defTagArray;
    }

    public void Dispose() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DACSentence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
