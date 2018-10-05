import java.io.*;
import java.net.*;

class ClienteTCP {
	
	public void realizarRequisicao(String mensagem) {
		try {
			// Cria um socket TCP para conexao com localhost:8000
			Socket sock = new Socket("localhost", 8000);

			// Coloca os dados em um buffer e envia para o servidor
			DataOutputStream d = new DataOutputStream(sock.getOutputStream());
			//String sBuf = "Mensagem de teste!\n";
			d.write(mensagem.getBytes("UTF-8"));

			// Prepara um buffer para receber a resposta do servidor
			InputStreamReader s = new InputStreamReader(sock.getInputStream());
			BufferedReader rec = new BufferedReader(s);

			// Le os dados enviados pela aplicacao servidora
			String rBuf = rec.readLine();

			// Apresenta a resposta recebida
			InetAddress ip = sock.getInetAddress();
			int port = sock.getPort();
			System.out.println(ip + ":" + port + ": " + rBuf);

			// Encerra a conexao com o servidor
			sock.close();
		} catch (Exception e) {
		}
	}
}
