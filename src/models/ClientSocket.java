package models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class ClientSocket extends Thread{
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket clientSocket;
	private ServerPrueba server;
	private int clientId;
	private String[] datos;
	
	private String user;
	private String pwd; 
	
	private final byte[] UserNameEnc = Base64.getDecoder().decode("cm9vdA==");
	private final byte[] PassEnc = Base64.getDecoder().decode("cm9vdDEwNg==");
 
	public ClientSocket(Socket clientSocket, ServerPrueba server, int clientId) throws Exception {
		super();
		this.clientSocket = clientSocket;
		this.server = server;
		this.clientId = clientId;
		this.dis = new DataInputStream(clientSocket.getInputStream());
		this.dos = new DataOutputStream(clientSocket.getOutputStream());
		this.user = new String(UserNameEnc, StandardCharsets.UTF_8);
		this.pwd = new String(PassEnc, StandardCharsets.UTF_8);
	}
 
	public void close() throws IOException {
		dis.close();
		dos.close();
		clientSocket.close();
	}
 
	public void sendMsg(String message) throws IOException {
		dos.writeUTF(message);
		System.out.println(message);
	}
 
	public String register() {
		Connection c = null;
		String resul = "";
		try {
			c = DriverManager.getConnection("jdbc:mysql://172.20.6.106:3306/chat4all", user, pwd);
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
		Connection c = null;
		String resul = "";
		try {
			c = DriverManager.getConnection("jdbc:mysql://172.20.6.106:3306/chat4all", "root", "root106");
			CallableStatement cst = c.prepareCall("{call LoginUser (?,?,?)}");
			cst.setString(1, datos[1]);
			cst.setString(2, datos[2]);			
		    cst.registerOutParameter(3, java.sql.Types.VARCHAR);
			cst.execute();
			resul = cst.getString(3);
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
	
	@Override
	public void run() {
		try {					
			while (true) {
				String entrada = dis.readUTF();
				System.out.println("recibido");
				datos = entrada.split(",");				
				switch(datos[0]) {
				case "PAX51":
					dos.writeUTF(login());
					System.out.println("enviado");
					break;
				case "PAX50":
					dos.writeUTF(register());
					break;
				default:
					server.sendToAll(entrada);
					break;
					
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
