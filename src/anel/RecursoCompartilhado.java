package anel;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class RecursoCompartilhado {

	private static int conteudo;
	private static Processo consumidor;
	
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
	/*
	public static boolean isEmUso() {
		return emUso;
	}
	
	public static void setEmUso(boolean estaEmUso, Processo processo) {
		emUso = estaEmUso;
		setConsumidor(emUso ? processo : null);
	}
	*/
	public static boolean isUsandoRecurso(Processo consumidor) {
		return consumidor.equals(RecursoCompartilhado.getConsumidor());
	}
}
