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

public class ClientSocket extends Thread{
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket clientSocket;
	private ServerPrueba server;
	private String[] datos;
	private final String SEPARADOR = ",&.";
	
	private String user;
	private String pwd; 
	
	private final byte[] UserNameEnc = Base64.getDecoder().decode("cm9vdA==");
	private final byte[] PassEnc = Base64.getDecoder().decode("cm9vdDEwNg==");
 
	public ClientSocket(Socket clientSocket, ServerPrueba server) throws Exception {
		super();
		this.clientSocket = clientSocket;
		this.server = server;
		this.dis = new DataInputStream(clientSocket.getInputStream());
		this.dos = new DataOutputStream(clientSocket.getOutputStream());
		this.user = new String(UserNameEnc, StandardCharsets.UTF_8);
		this.pwd = new String(PassEnc, StandardCharsets.UTF_8);
	}
	
	public int getPort() {
		return this.clientSocket.getPort();
	}
	public void close() throws IOException {
		dis.close();
		dos.close();
		clientSocket.close();
	}
 
	public void sendMsg(String message) throws IOException {
		dos.writeUTF(message);
		System.out.println(clientSocket.getInetAddress());
	}
	
	
	public String register() {
		Connection c = null;
		String resul = "";
		try {
			c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/chat4all", user, user);
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
	public String login() {
		Connection c = null;
		String resul = "";
		try {
			c = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/chat4all", user, user);
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
					server.manageLogin(msg1, this.getPort());
					break;
				case "PAX50":
					String msg2 = register();
					server.manageLogin(msg2, this.getPort());					
					break;
				case "PAX53":
					
					break;
				case "PAX98":
					server.justTalk(this);
					break;
				case "PAX99":
					close();
					dis = null;
					break;
				default:
					server.sendToAll(entrada);
					break;
					
				}
				
			}
		} catch (IOException e) {
			System.out.println("Conexion terminada");
		}
	}

}
