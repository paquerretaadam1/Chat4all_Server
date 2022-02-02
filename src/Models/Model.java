package Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Model {
	public static void main(String[] args) {
		dataBaseConexion();
	}
	private static void dataBaseConexion() {
		try {
			Connection c = DriverManager.getConnection("jdbc:mysql://172.20.6.106:3306/chat4all", "root", "root106");
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT User_ID, User_Name, User_Password, User_Email FROM userslog");
			System.out.println("Succed");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
