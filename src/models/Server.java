package models;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Project Name: Chat4All
 * Description: The Server Of A Simple Shared Chat
 * 				It works with:
 * 					- MySQL
 * 					- Apache2
 * @author Xabier Ruiz, Ander Gaona, Pedro Aquerreta (PAX)
 */
public class Server implements Runnable {
 
	private int port = 2022;
	private List<ClientSocket> clients;
	private ServerSocket server;
	private TreeMap<String, String> usersConectados;
	
	public static void main(String[] args) throws Exception {
		new Server();
	}
 
	public Server() throws IOException {
		server = new ServerSocket (port); // A port between 1024-65535
		clients = new ArrayList<ClientSocket>();
		new Thread(this).start();
		System.out.println("server starting");
	}
 
	/**
	 * Call the method that send messages for every client in the list
	 */
	public void sendToAll(String message) throws IOException {
		for (ClientSocket client : clients) {
			client.sendMsg(message);
		}
	}
	
	/**
	 * TODO
	 * Adds Users that are online
	 */
	public void addUser(String user, String apodo) {
		if (!isIn(user)){
			
		}
	}
	
	/**
	 * @return if the user is online or not
	 */
	public boolean isIn(String user) {		
		return usersConectados.containsKey(user);
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				 // Espera a que el cliente se conecte
				Socket client = server.accept();
				 // Inicializa y ejecuta el cliente conectado
				ClientSocket clientSocket = new ClientSocket(client, this);
				clientSocket.start();
				 // Agregar a la lista de conexiones del cliente
				clients.add(clientSocket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
