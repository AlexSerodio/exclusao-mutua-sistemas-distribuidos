package anel;

public class RecursoCompartilhado {

	private static int conteudo;
	private static Processo consumidor;
	private static boolean emUso;
	
	private RecursoCompartilhado() { 
		
	}
	
	public static int getConteudo() {
		return conteudo;
	}

	public static Processo getConsumidor() {
		return consumidor;
	}

	public static void setConsumidor(Processo novoConsumidor) {
		consumidor = novoConsumidor;
	}
	
	public static boolean isEmUso() {
		return emUso;
	}
	
	public static void setEmUso(boolean estaEmUso, Processo processo) {
		emUso = estaEmUso;
		setConsumidor(emUso ? processo : null);
	}
}
