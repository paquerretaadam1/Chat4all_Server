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
	
	private String[] datos;
    
	/*
	 * Main method
	 */
	public static void main(String[] args) {
		Server server = new Server();
		server.start();
		
		//server.dataBaseConexion();
		//server.registrarse();
	}
	
	private  void start() {
		try {
			InetAddress ip = InetAddress.getByName(LOCALHOST);		
			
			this.server = new ServerSocket(this.PORT, this.BACKLOG, ip);
			
			System.out.println("Servidor iniciado...");
			Thread.sleep(1000);
			System.out.println("Escuchando...");
			while (true) {
				if (clientes.size() != BACKLOG) {
					System.out.println("Succed0");
					this.cliente = this.server.accept();
					this.clientes.add(new Socket(this.cliente.getInetAddress(),this.cliente.getLocalPort()));
					System.out.println("Succed1");
					entrada = new DataInputStream(cliente.getInputStream());
		            salida = new DataOutputStream(cliente.getOutputStream());
		            System.out.println("Succed2");
					mensajeRecibido = entrada.readUTF();
					this.datos = mensajeRecibido.split(",");
					System.out.println("Succed3");
					String resul = this.procesar();
					salida.writeUTF(resul);
					//this.clientes.removeIf(client -> client.getInetAddress().equals(this.cliente.getInetAddress()));
					this.cliente.close();
					this.server.close();
					System.out.println("Succed4");
				}
				else {
					System.out.println("El servidor no acepta mas clientes");
				}
			}
				
		} 
		catch (NumberFormatException e) {
			System.out.println("not Succed 0");
			e.printStackTrace();
		} 
		catch (UnknownHostException e) {
			System.out.println("not Succed 1");
			e.printStackTrace();
		} 
		catch (IOException e) {
			System.out.println("not Succed 2");
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			System.out.println("not Succed 3");
			e.printStackTrace();
		}
	}
	private String procesar() {
		String resul;
		switch (datos[0]) {
			case "PAX50":
				resul = this.registrarse();
				break;
			default:
				resul = null;
			}
		return resul;
	}
	
	private String registrarse() {
		Connection c = null;
		String resul = "";
		try {
			c = DriverManager.getConnection("jdbc:mysql://172.20.6.106:3306/chat4all", "root", "root106");
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
	
	
}
