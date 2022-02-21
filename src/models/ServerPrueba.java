package models;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;



public class ServerPrueba implements Runnable {
 
	private int port = 2022;
	private List<ClientSocket> clients;
	private ServerSocket server;
	private List<String> usersConectados;
	public static void main(String[] args) throws Exception {
		new ServerPrueba();
	}
 
	public ServerPrueba() throws IOException {
		server = new ServerSocket (port); // Un puerto de 1024-65535
		clients = new ArrayList<ClientSocket>();
		new Thread(this).start();
		System.out.println("server starting");
	}
 
	public void sendToAll(String message) throws IOException {
		for (ClientSocket client : clients) {
			client.sendMsg(message);
		}
	}
	public void manageLogin(String message, int port) throws IOException {
		for (ClientSocket client : clients) {
			if (client.getPort() == port) {
				client.loginRegister(message);
			}			
		}
	}
	public void showClients() {
		for (ClientSocket client : clients) {
			System.out.println(client.getPort());
		}
	}
	
	public void añadirUser(String user, String apodo) {
		if (!estaDentro(user)){
			usersConectados.add(user);			
		}
	}
	
	public boolean estaDentro(String user) {		
		return usersConectados.contains(user);
	}
	
	public void justTalk(ClientSocket cs) {
		clients.remove(cs);
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
