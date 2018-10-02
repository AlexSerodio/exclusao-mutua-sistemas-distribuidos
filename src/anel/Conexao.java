package anel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class Conexao {

	private final int PORTA = 8000;
	
	public void ativarServidor() {
	    try {
		    // Cria um socket TCP para pedidos de conexão na porta 8000
		    ServerSocket servidor = new ServerSocket(PORTA);
	
		    // Aguarda até que um cliente peça por uma conexão
		    Socket socketServidor = servidor.accept();
	
		    // Prepara um buffer para receber dados de um cliente
		    InputStreamReader s = new InputStreamReader(socketServidor.getInputStream());
		    BufferedReader requisicao = new BufferedReader(s);
	
		    // Lê os dados enviados pela aplicação cliente
		    String mensagem = requisicao.readLine();
	
		    // Apresenta os dados recebidos
		    InetAddress ipResposta = socketServidor.getInetAddress();
		    int portaResposta = socketServidor.getPort();
		    System.out.println(ipResposta + ":" + portaResposta + ": " + mensagem);
	
		    // Coloca a resposta em um buffer e envia para o cliente
		    DataOutputStream dados = new DataOutputStream(socketServidor.getOutputStream());
		    String resposta = "Ok!\n";
		    dados.write(resposta.getBytes("UTF-8"));
	
		    // Encerra a conexão com o cliente
		    socketServidor.close();
		    servidor.close();
	    } catch (Exception e) {}
	}
	
	public String requisitarRecurso(String ipServidor, String mensagem) {
		try {
			// Cria um socket TCP para conexão com ip:porta (localhost:8000)
			Socket cliente = new Socket(ipServidor, PORTA);

		    // Coloca os dados em um buffer e envia para o servidor
		    DataOutputStream dados = new DataOutputStream(cliente.getOutputStream());
		    dados.write(mensagem.getBytes("UTF-8"));
	
		    // Prepara um buffer para receber a resposta do servidor
		    InputStreamReader s = new InputStreamReader(cliente.getInputStream());
		    BufferedReader rec = new BufferedReader(s);
	
		    // Lê os dados enviados pela aplicação servidora
		    String resposta = rec.readLine();
	
		    // Encerra a conexão com o servidor
		    cliente.close();
		    
		    return resposta;
	    } catch (Exception e) {}
		return null;
	}
	
	private void exibeResposta(Socket socket, String resposta) {
		// Apresenta a resposta recebida
	    InetAddress ipResposta = socket.getInetAddress();
	    int portaResposta = socket.getPort();
	    System.out.println(ipResposta + ":" + portaResposta + ": " + resposta);
	}
}
