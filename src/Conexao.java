import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class Conexao {

	private boolean conectado = true;
	public static final String PERMITIR_ACESSO = "PERMITIR";
	public static final String NEGAR_ACESSO = "NAO_PERMITIR";
	private Socket sock;
	private ServerSocket listenSocket;
	
	public String realizarRequisicao(String mensagem) {
		String rBuf = "ERROR!";
		try {
			Socket sock = new Socket("localhost", 8000);

			DataOutputStream d = new DataOutputStream(sock.getOutputStream());
			d.write(mensagem.getBytes("UTF-8"));

			InputStreamReader s = new InputStreamReader(sock.getInputStream());
			BufferedReader rec = new BufferedReader(s);

			rBuf = rec.readLine();

			sock.close();
		} catch (Exception e) {
			System.out.println("Ocorreu um erro. A requisicao nao foi finalizada.");
		}
		return rBuf;
	}
	
	public void conectar(Processo coordenador) {
		System.out.println("Servidor " + coordenador + " pronto para receber requisicoes.");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					listenSocket = new ServerSocket(8000);
					while(conectado) {
						sock = listenSocket.accept();

						InputStreamReader s = new InputStreamReader(sock.getInputStream());
						BufferedReader rec = new BufferedReader(s);

						String rBuf = rec.readLine();
						System.out.println(rBuf);
						
						DataOutputStream d = new DataOutputStream(sock.getOutputStream());
						String sBuf = "Error!\n";
						
						if(coordenador.isRecursoEmUso())
							sBuf = NEGAR_ACESSO + "\n";
						else
							sBuf = PERMITIR_ACESSO + "\n";
						
						d.write(sBuf.getBytes("UTF-8"));
					}
					System.out.println("Conexao encerrada.");
				} catch (IOException e) {
					System.out.println("Conexao encerrada.");
				}
			}
		}).start();
	}
	
	public void encerrarConexao() {
		conectado = false;
		try {
			sock.close();
		} catch (IOException | NullPointerException e) {
			System.out.println("Erro ao encerrar a conexao: ");
			e.printStackTrace();
		}
		try {
			listenSocket.close();
		} catch (IOException | NullPointerException e) {
			System.out.println("Erro ao encerrar a conexao: ");
			e.printStackTrace();
		}
	}	
}