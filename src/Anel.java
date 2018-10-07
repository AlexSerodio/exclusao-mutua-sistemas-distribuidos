import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class Anel {

	private static final int ADICIONA = 4000;
	private static final int INATIVO_PROCESSO = 8000;
	private static final int INATIVO_COORDENADOR = 30000;
	private static final int CONSOME_RECURSO_MIN = 5000;
	private static final int CONSOME_RECURSO_MAX = 10000;
	

	private static ArrayList<Processo> processosAtivos = new ArrayList<Processo>();
	private final Object lock = new Object();

	public static ArrayList<Processo> getProcessosAtivos() {
		return processosAtivos;
	}
	
	public static void removerProcesso(Processo processo) {
		processosAtivos.remove(processo);
	}
	
	public void criarProcessos () {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					synchronized (lock) {
						Processo processo = new Processo(gerarIdUnico());
						
						if (processosAtivos.isEmpty())
							processo.setEhCoordenador(true);

						processosAtivos.add(processo);
					}
					
					esperar(ADICIONA);
					
				}
			}
		}).start();
	}

	private int gerarIdUnico() {
		Random random = new Random();
		int idRandom = random.nextInt(1000);
		
		for(Processo p : processosAtivos) {
			if(p.getPid() == idRandom)
				return gerarIdUnico();
		}
		
		return idRandom;
	}

	public void inativarProcesso () {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					esperar(INATIVO_PROCESSO);
					
					synchronized (lock) {
						if (!processosAtivos.isEmpty()) {
							int indexProcessoAleatorio = new Random().nextInt(processosAtivos.size());
							Processo pRemover = processosAtivos.get(indexProcessoAleatorio);
							if (pRemover != null && !pRemover.isCoordenador())
								pRemover.destruir();
						}
					}
				}
			}
		}).start();
	}

	public void inativarCoordenador () {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					esperar(INATIVO_COORDENADOR);
					
					synchronized (lock) {
						Processo coordenador = null;
						for (Processo p : processosAtivos) {
							if (p.isCoordenador())
								coordenador = p;
						}
						if (coordenador != null){
							coordenador.destruir();
							System.out.println("Processo coordenador " + coordenador + " destruido.");
						}
					}
				}
			}
		}).start();
	}

	public void acessarRecurso() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Random random = new Random();
				int intervalo = 0;
				while (true) {
					intervalo = random.nextInt(CONSOME_RECURSO_MAX - CONSOME_RECURSO_MIN);
					esperar(CONSOME_RECURSO_MIN + intervalo);
					
					synchronized (lock) {
						if (!processosAtivos.isEmpty()) {
							int indexProcessoAleatorio = new Random().nextInt(processosAtivos.size());
														
							Processo processoConsumidor = processosAtivos.get(indexProcessoAleatorio);
							processoConsumidor.acessarRecursoCompartilhado();
						}
					}
				}
			}
		}).start();
	}
	
	private void esperar(int segundos) {
		try {
			Thread.sleep(segundos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}