import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Conexao {

	private boolean conectado = true;
	public static final String PERMITIR_ACESSO = "PERMITIR";
	public static final String NEGAR_ACESSO = "NAO_PERMITIR";
	
	public String realizarRequisicao(String mensagem) {
		String rBuf = "ERROR!";
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
			rBuf = rec.readLine();

			// Apresenta a resposta recebida
			//InetAddress ip = sock.getInetAddress();
			//int port = sock.getPort();
			//System.out.println("Cliente: " + ip + ":" + port + ": " + rBuf);

			// Encerra a conexao com o servidor
			sock.close();
		} catch (Exception e) {
			System.out.println("Ocorreu um erro. A requisicao nao foi finalziada.");
		}
		return rBuf;
	}
	
	public void conectar(Processo coordenador) {
		System.out.println("Servidor " + coordenador + " pronto para receber requisicoes.");
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
						//InetAddress ip = sock.getInetAddress();
						//int port = sock.getPort();
						//System.out.println("Servidor: " + ip + ":" + port + ": " + rBuf);
						System.out.println(rBuf);
						
						// Coloca a resposta em um buffer e envia para o cliente
						DataOutputStream d = new DataOutputStream(sock.getOutputStream());
						String sBuf = "Error!\n";
						
						if(coordenador.isRecursoEmUso())
							sBuf = NEGAR_ACESSO + "\n";
						else
							sBuf = PERMITIR_ACESSO + "\n";
						
						d.write(sBuf.getBytes("UTF-8"));
						sock.close();
					}
					
					listenSocket.close();
				} catch (Exception e) {
					System.out.println("Ocorreu um erro durante a requisicao.");
				}
			}
		}).start();
	}
	
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
						
						if(rBuf.equals(PERMITIR_ACESSO))
							sBuf = "Pode consumir!\n";
						else if(rBuf.equals(NEGAR_ACESSO))
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
