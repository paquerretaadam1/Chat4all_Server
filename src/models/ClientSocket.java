package models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientSocket extends Thread{
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket clientSocket;
	private ServerPrueba server;
	private int clientId;
	private String[] datos;
 
	public ClientSocket(Socket clientSocket, ServerPrueba server, int clientId) throws Exception {
		super();
		this.clientSocket = clientSocket;
		this.server = server;
		this.clientId = clientId;
		this.dis = new DataInputStream(clientSocket.getInputStream());
		this.dos = new DataOutputStream(clientSocket.getOutputStream());
	}
 
	public void close() throws IOException {
		dis.close();
		dos.close();
		clientSocket.close();
	}
 
	public void sendMsg(String message) throws IOException {
		dos.writeUTF(message);
	}
 
	public String register() {
		Connection c = null;
		String resul = "";
		try {
			c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/chat4all", "root", "root");
			CallableStatement cst = c.prepareCall("{call RegisterUser (?,?,?,?)}");
			cst.setString(1, datos[1]);
			cst.setString(2, datos[2]);
			cst.setString(3, datos[3]);
		    cst.registerOutParameter(4, java.sql.Types.VARCHAR);
			cst.execute();
			resul = cst.getString(4);
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
		 return resul;
	}
	public String login() {
		return "";
	}
	@Override
	public void run() {
		boolean start = false;
		try {
			while(!start) {
				String datos = dis.readUTF();
				this.datos = datos.split(",");
				String resul = "";
				if(this.datos[0].equals("PAX50")) {
					resul = register();
				}
				else {
					resul = login();
				}
				dos.writeUTF(resul);
			}
			
			while (true) {
				server.sendToAll(dis.readUTF());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
