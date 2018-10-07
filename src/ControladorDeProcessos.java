import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class ControladorDeProcessos {

	private static ArrayList<Processo> processosAtivos = new ArrayList<Processo>();
	
	public static ArrayList<Processo> getProcessosAtivos() {
		return processosAtivos;
	}
	
	public static Processo getCoordenador() {
		for (Processo processo : processosAtivos) {
			if (processo.isCoordenador())
				return processo;
		}
		return null;
	}
	
	public static void removerProcesso(Processo processo) {
		processosAtivos.remove(processo);
	}
	
}