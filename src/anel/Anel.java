package anel;

import java.util.ArrayList;
import java.util.Random;


/**
 * @author Alex Serodio Goncalves e Luma Kuhl
 * 
 * 1. a cada 30 segundos um novo processo deve ser criado;
 * 2. a cada 25 segundos um processo deve fazer uma requisicao para o coordenador;
 * 3. a cada 100 segundos o coordenador fica inativo;
 * 4. a cada 80 segundos um processo da lista de processos fica inativo;
 * 5. dois processos nao podem ter o mesmo ID;
 * 6. dois processos de eleicao nao podem acontecer simultaneamente.
 */
public class Anel {

	private static final int ADICIONA = 4000;
	private static final int INATIVO_PROCESSO = 8000;
//	private static final int INATIVO_COORDENADOR = 10000;
//	private static final int CONSOME_RECURSO_MIN = 10000;
//	private static final int CONSOME_RECURSO_MAX = 25000;
	private static final int INATIVO_COORDENADOR = 30000;
	private static final int CONSOME_RECURSO_MIN = 5000;
	private static final int CONSOME_RECURSO_MAX = 10000;
	

	public static ArrayList<Processo> processosAtivos = new ArrayList<Processo>();
	private final Object lock = new Object();

	public void criarProcessos () {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int novoId = 1;
				while (true) {
					synchronized (lock) {
						if (processosAtivos.isEmpty()) {
							processosAtivos.add(new Processo(1, true));
						} else {
							novoId = processosAtivos.get(processosAtivos.size() - 1).getPid() + 1;
							processosAtivos.add(new Processo(novoId, false));
						}
					}

					try {
						Thread.sleep(ADICIONA);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void inativarProcesso () {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(INATIVO_PROCESSO);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					synchronized (lock) {
						if (!processosAtivos.isEmpty()) {
							int indexProcessoAleatorio = new Random().nextInt(processosAtivos.size());
							Processo pRemover = processosAtivos.get(indexProcessoAleatorio);
							if (pRemover != null && !pRemover.isEhCoordenador())
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
					try {
						Thread.sleep(INATIVO_COORDENADOR);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					synchronized (lock) {
						Processo coordenador = null;
						for (Processo p : processosAtivos) {
							if (p.isEhCoordenador())
								coordenador = p;
						}
						if (coordenador != null){
							coordenador.destruir();
							System.out.println("Processo coordenador " + coordenador + " inativado.");
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
					try {
						intervalo = CONSOME_RECURSO_MIN + random.nextInt(CONSOME_RECURSO_MAX - CONSOME_RECURSO_MIN);
						Thread.sleep(intervalo);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					synchronized (lock) {
						if (processosAtivos.size() > 0) {
							int indexProcessoAleatorio = new Random().nextInt(processosAtivos.size());
														
							Processo processoConsumidor = processosAtivos.get(indexProcessoAleatorio);
							processoConsumidor.acessarRecursoCompartilhado();
						}
					}
				}
			}
		}).start();
	}
}