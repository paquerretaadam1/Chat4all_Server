package models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;

/**
 * Project Name: Chat4All
 * Description: The Server Of A Simple Shared Chat
 * 				It works with:
 * 					- MySQL
 * 					- Apache2
 * @author Xabier Ruiz, Ander Gaona, Pedro Aquerreta (PAX)
 */
public class ClientSocket extends Thread{
	// Attributes
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket clientSocket;
	private Server server;
	private String[] datos;
	private final String SEPARADOR = ",&.";
	
	
	// User and password of our database
	private String user;
	private String pwd; 
	
	// User and password of our database decoded from Base64
	private final byte[] UserNameEnc = Base64.getDecoder().decode("cm9vdA==");
	private final byte[] PassEnc = Base64.getDecoder().decode("cm9vdDEwNg==");
 
	public ClientSocket(Socket clientSocket, Server server) throws Exception {
		super();
		this.clientSocket = clientSocket;
		this.server = server;
		this.dis = new DataInputStream(clientSocket.getInputStream());
		this.dos = new DataOutputStream(clientSocket.getOutputStream());
		this.user = new String(UserNameEnc, StandardCharsets.UTF_8);
		this.pwd = new String(PassEnc, StandardCharsets.UTF_8);
	}
	
	/**
	 * Getter for the port
	 * 
	 */
	public int getPort() {
		return this.clientSocket.getPort();
	}
	
	/**
	 * Closes the connection
	 */
	public void close() throws IOException {
		dis.close();
		dos.close();
		clientSocket.close();
	}
 
	/**
	 * Send the message in this socket
	 * @param message Is the message you want to send
	 */
	public void sendMsg(String message) throws IOException {
		dos.writeUTF(message);
		System.out.println(clientSocket.getInetAddress());
	}
	
	/**
	 * Calls a stored procedure that checks if a new user can register
	 * @return If the user can join or not
	 */
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
		} catch (SQLException e) {
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
	
	/**
	 * Calls a stored procedure that checks if a user can login
	 * @return If the user can join or not
	 */
	public String login() {
		Connection c = null;
		String resul = "";
		try {
			c = DriverManager.getConnection("jdbc:mysql://172.20.6.106:3306/chat4all", user, pwd);
			CallableStatement cst = c.prepareCall("{call LoginUser (?,?,?)}");
			cst.setString(1, datos[1]);
			cst.setString(2, datos[2]);			
		    cst.registerOutParameter(3, java.sql.Types.VARCHAR);
			cst.execute();
			resul = cst.getString(3);
		} catch (SQLException e) {
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
	
	/**
	 * Send the client the result from login and register
	 */
	public void loginRegister(String message) throws IOException {
		dos.writeUTF(message);
	}
	
	@Override
	public void run() {
		try {					
			while (dis != null) {
				String entrada = dis.readUTF();
				datos = entrada.split(SEPARADOR);				
				switch(datos[0]) {
				case "PAX51":
					String msg1 = login();
					loginRegister(msg1);
					break;
				case "PAX50":
					String msg2 = register();
					loginRegister(msg2);					
					break;
				case "PAX99":
					close();
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
