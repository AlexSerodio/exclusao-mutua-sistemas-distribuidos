package anel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class Conexao {

	private final int PORTA = 8000;
	private static final String resposta = "Ok";
	
	
	public void ativarServidor() {
	    try {
		    ServerSocket servidor = new ServerSocket(PORTA);
		    Socket socketServidor = servidor.accept();
	
		    InputStreamReader s = new InputStreamReader(socketServidor.getInputStream());
		    BufferedReader requisicao = new BufferedReader(s);
	
		    String mensagem = requisicao.readLine();
		    System.out.println("Mensagem recebida pelo servidor: " + mensagem);
		    
		    DataOutputStream dados = new DataOutputStream(socketServidor.getOutputStream());
		    dados.write(resposta.getBytes("UTF-8"));
	
		    socketServidor.close();
		    servidor.close();
	    } catch (Exception e) {}
	}
	
	public String requisitarRecurso(String ipServidor, String mensagem) {
		String respostaServidor = "error";
		try {
			Socket cliente = new Socket(ipServidor, PORTA);

		    DataOutputStream dados = new DataOutputStream(cliente.getOutputStream());
		    dados.write(mensagem.getBytes("UTF-8"));
	
		    InputStreamReader s = new InputStreamReader(cliente.getInputStream());
		    BufferedReader rec = new BufferedReader(s);
	
		    respostaServidor = rec.readLine();
		    cliente.close();
	    } catch (Exception e) {}
		
		return respostaServidor;
	}
}
