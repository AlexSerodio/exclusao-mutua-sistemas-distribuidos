import java.util.Scanner;

public class TesteTCP {

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		
		System.out.println("Digite servidor ou cliente");
		String comando = input.nextLine();
		
		Conexao conexao = new Conexao();
		
		if(comando.equals("servidor")) {
			//ServidorTCP servidor= new ServidorTCP();
			//servidor.conectar();
			conexao.conectar();
		} else if(comando.equals("cliente")) {
			//ClienteTCP cliente = new ClienteTCP();
			System.out.println("O cliente foi criado com sucesso.");
			
			while(true) {
				System.out.println("Digite a mensagem de requisicao");
				String mensagem = input.nextLine();
				//cliente.realizarRequisicao(mensagem + "\n");
				conexao.realizarRequisicao(mensagem + "\n");
			}
		}
	}
	
}
