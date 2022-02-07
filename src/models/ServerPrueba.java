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
import java.util.List;



public class ServerPrueba implements Runnable {
 
	private int port = 8422;
	private List<ClientSocket> clients;
	private ServerSocket server;
	private static int clientId = 1;
 
	public static void main(String[] args) throws Exception {
		new Server();
	}
 
	public ServerPrueba() throws IOException {
		server = new ServerSocket (10086); // Un puerto de 1024-65535
		clients = new ArrayList<ClientSocket>();
		new Thread(this).start();
		System.out.println("server starting");
	}
 
	public void sendToAll(String message) throws IOException {
		for (ClientSocket client : clients) {
			client.sendMsg(message);
		}
	}
 
	@Override
	public void run() {
		try {
			while (true) {
				 // Espera a que el cliente se conecte
				Socket client = server.accept();
				 // Inicializa y ejecuta el cliente conectado
				ClientSocket clientSocket = new ClientSocket(client, this, clientId);
				clientId++;
				clientSocket.start();
				 // Agregar a la lista de conexiones del cliente
				clients.add(clientSocket);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
