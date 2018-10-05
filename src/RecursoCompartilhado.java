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

	public static boolean isUsandoRecurso(Processo consumidor) {
		return consumidor.equals(RecursoCompartilhado.getConsumidor());
	}
	
	public static boolean isSendoConsumido() {
		return consumidor != null;
	}
}
