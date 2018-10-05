import java.io.*;
import java.net.*;

class ServidorTCP {

	private boolean conectado = true;
	
	public void conectar() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Cria um socket TCP para pedidos de conexao na porta 8000
					ServerSocket listenSocket = new ServerSocket(8000);
					while(conectado) {
						// Aguarda ate que um cliente peca por uma conexao
						Socket sock = listenSocket.accept();

						// Prepara um buffer para receber dados de um cliente
						InputStreamReader s = new InputStreamReader(sock.getInputStream());
						BufferedReader rec = new BufferedReader(s);

						// Le os dados enviados pela aplicacao cliente
						String rBuf = rec.readLine();

						// Apresenta os dados recebidos
						InetAddress ip = sock.getInetAddress();
						int port = sock.getPort();
						System.out.println(ip + ":" + port + ": " + rBuf);

						// Coloca a resposta em um buffer e envia para o cliente
						DataOutputStream d = new DataOutputStream(sock.getOutputStream());
						String sBuf = "Error!\n";
						
						if(rBuf.equals("livre"))
							sBuf = "Pode consumir!\n";
						else if(rBuf.equals("ocupado"))
							sBuf = "Nao pode consumir!\n";
						
						d.write(sBuf.getBytes("UTF-8"));
						sock.close();
					}
					
					listenSocket.close();
				} catch (Exception e) {}
			}
		}).start();
	}
	
	public void encerrarConexao() {
		conectado = false;
	}
}