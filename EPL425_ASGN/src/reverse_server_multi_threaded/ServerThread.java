package reverse_server_multi_threaded;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible to handle client connection.
 */
public class ServerThread extends Thread {
	private Socket socket;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);

			String requestMessage;

			do {
				requestMessage = reader.readLine();
				System.out.println("Request:"+requestMessage);				
				ClientFormatMessage clientFormatMessage = ClientFormatMessage.stringToObject(requestMessage);
				if (clientFormatMessage.getBody().getMsgBody().equals("BYE")) {
					System.out.println(
							"Connection with client " + clientFormatMessage.getHeader().getClientID() + " terminated..");
					break;
				}
				ClientFormatMessage serverFormatMessage = ClientFormatMessage.getInstance("WELCOME", clientFormatMessage.getHeader().getClientID(), 
						clientFormatMessage.getHeader().getClientIP(), ClientFormatMessage.calculatePayload(), clientFormatMessage.getHeader().getPort());			

				System.out.println("Client Request:" + requestMessage);
				writer.println(ClientFormatMessage.objectToString(serverFormatMessage));

			} while (true);

			socket.close();
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}