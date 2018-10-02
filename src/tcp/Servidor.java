package tcp;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Servidor {

	public static void main(String[] args) {
		List<PrintStream> clientes = new ArrayList<PrintStream>();

		try {
			ServerSocket servidor = new ServerSocket(9999);
			System.out.println("Servidor lendo a porta 9999");

			Socket socket = servidor.accept();

			new Thread() {
				public void run() {
					System.out.println("Cliente conectou: " + socket.getInetAddress().getHostName());

					try {
						PrintStream escritaCliente = new PrintStream(socket.getOutputStream());
						clientes.add(escritaCliente);

						Scanner leitura = new Scanner(socket.getInputStream());

						while (leitura.hasNext()) {
							String texto = leitura.nextLine();
							System.err.println(texto);

							for (PrintStream cliente : clientes) {
								cliente.println(texto);
								// cliente.flush();
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
