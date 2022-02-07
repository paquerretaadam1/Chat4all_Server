package models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocket extends Thread{
	private DataInputStream dis;
	private DataOutputStream dos;
	private Socket clientSocket;
	private ServerPrueba server;
	private int clientId;
 
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
 
	@Override
	public void run() {
		try {
			while (true) {
				server.sendToAll(dis.readUTF());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
