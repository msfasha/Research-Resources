/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB.DAC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author technical.support
 */
public class DBConnection {

    Connection con;

    public DBConnection(int DBType)//1- SQL Server, 2- Microsoft Access
    {
        try {
            if (DBType == 1)//sql server
            {
                String url = "jdbc:sqlserver://localhost:1433" + ";databaseName=b2a2";
                String userName = "sa";
                String password = "sa";
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                con = DriverManager.getConnection(url, userName, password);
            } else //accessdb
            {
                String url = "jdbc:ucanaccess://d:/";
                // specify url, username, pasword - make sure these are valid 
                Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
                con = DriverManager.getConnection(url, "", "");
            }
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public Connection GetConnection() {
        return con;
    }
}

