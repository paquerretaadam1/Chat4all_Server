package models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class Server {
	// Final attributes
	private final int PORT = 2022;
	private final int BACKLOG = 20;
	private final String LOCALHOST = "127.0.0.1";
	
	//Attributes
	private ServerSocket server;
	private Socket cliente;
	private ArrayList<Socket> clientes = new ArrayList<>();
	private DataOutputStream salida;
	private DataInputStream entrada;
	private String mensajeRecibido;
    
	/*
	 * Main method
	 */
	public static void main(String[] args) {
		Server server = new Server();
		//server.start();
		
		//server.dataBaseConexion();
		server.registrarse();
	}
	
	private void start() {
		try {
			InetAddress ip = InetAddress.getByName(LOCALHOST);		
			
			this.server = new ServerSocket(this.PORT, this.BACKLOG, ip);
			System.out.println("Servidor iniciado...");
			Thread.sleep(1000);
			System.out.println("Escuchando...");
			while (true) {
				if (clientes.size() != BACKLOG) {
					this.cliente = this.server.accept();
					this.clientes.add(new Socket(this.cliente.getInetAddress(),this.cliente.getLocalPort()));
					System.out.println(this.clientes.get(0));
				}
				else {
					System.out.println("El servidor no acepta mas clientes");
				}
			}
				
		} 
		catch (NumberFormatException e) {
			e.printStackTrace();
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void dataBaseConexion() {
		try {
			Connection c = DriverManager.getConnection("jdbc:mysql://172.20.6.106:3306/chat4all", "root", "root106");
			Statement s = c.createStatement();
			//ResultSet rs = s.executeQuery("SELECT User_ID, User_Name, User_Password, User_Email FROM userslog");
			
			System.out.println("Succed");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void registrarse() {
		Connection c = null;
		try {
			c = DriverManager.getConnection("jdbc:mysql://172.20.6.106:3306/chat4all", "root", "root106");
			CallableStatement cst = c.prepareCall("{call RegisterUser (?,?,?,?)}");
			cst.setString(1, "root");
			cst.setString(2, "root106");
			cst.setString(3, "root@chat4all.com");
		    cst.registerOutParameter(4, java.sql.Types.VARCHAR);
			cst.execute();
			String resul = cst.getString(4);
			System.out.println(resul);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		finally {
			 try {
	                c.close();
	            } catch (SQLException ex) {
	                System.out.println("Error: " + ex.getMessage());
	            }
		}
	}
	
	
}
