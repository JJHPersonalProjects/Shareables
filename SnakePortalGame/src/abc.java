
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jabbar
 */
class abc {
    public abc() throws SQLException{
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/MyDataBase","Jabbar","123456" );
        PreparedStatement st = con.prepareStatement("insert into UNTITLED(Score,Name)values(?,?)");
        st.setInt(1, 101);
        st.setString(2,"Jack");
        int a = st.executeUpdate();
        if(a>0){
            System.out.println("Row Update");
        }
    }
    
}
