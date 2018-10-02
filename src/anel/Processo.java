package anel;

import java.util.LinkedList;
import java.util.Random;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class Processo {
	
	private int pid;
	private LinkedList<Processo> listaDeEspera;
	private boolean ehCoordenador;
	private Thread utilizaRecurso;
	
//	private static final int USO_PROCESSO_MIN = 5000;
//	private static final int USO_PROCESSO_MAX = 15000;
	private static final int USO_PROCESSO_MIN = 10000;
	private static final int USO_PROCESSO_MAX = 20000;
	
	public Processo(int pid) {
		this.pid = pid;
		setEhCoordenador(false);
	}
	
	public Processo(int pid, boolean ehCoordenador) {
		this.pid = pid;
		setEhCoordenador(ehCoordenador);
	}
	
	public int getPid() {
		return pid;
	}
	
	public boolean isEhCoordenador() {
		return ehCoordenador;
	}

	public void setEhCoordenador(boolean ehCoordenador) {
		this.ehCoordenador = ehCoordenador;
		if(this.ehCoordenador)
			listaDeEspera = new LinkedList<>();
	}
	
	public boolean isListaDeEsperaEmpty() {
		return getListaDeEspera().isEmpty();
	}
	
	private LinkedList<Processo> getListaDeEspera() {
		return encontrarCoordenador().listaDeEspera;
	}
	
	public boolean enviarRequisicao() {
		Processo coordenador = encontrarCoordenador();
		
		boolean resultadoRequisicao = coordenador.receberRequisicao(this.pid);
		
		//System.out.println("Fim da requisicao.");
		return resultadoRequisicao;
	}
	
	private Processo encontrarCoordenador() {
		Processo coordenador = null;
		for (Processo p : Anel.processosAtivos) {
			if (p.isEhCoordenador()) {
				coordenador = p;
				break;
			}
		}
		
		if(coordenador == null)
			coordenador = comecarEleicao();
		
		return coordenador;
	}

	// TODO quando o coordenador morre o recurso precisa ser liberado
	
	private Processo comecarEleicao() {
		Eleicao eleicao = new Eleicao();
		Processo coordenador = eleicao.realizarEleicao(getPid());
			
		try {
			if(coordenador.equals(RecursoCompartilhado.getConsumidor()))
				coordenador.interronpeAcessoRecurso();
		} catch (NullPointerException e) {
			comecarEleicao();
		}
		return coordenador;
	}

	private boolean receberRequisicao(int pidOrigemRequisicao) {
		
		/* TRATAMENTO DA REQUISICAO AQUI... */
		
		//System.out.println("Requisicao do processo " + pidOrigemRequisicao + " recebida com sucesso.");
		return true;
	}
	
	public void acessarRecursoCompartilhado() {
		
		// se esse processo ja estiver consumindo o recurso
		if(this.equals(RecursoCompartilhado.getConsumidor()))
			return;
		
		System.out.println("Processo " + this + " quer consumir o recurso.");
		
		if(RecursoCompartilhado.isEmUso())
			adicionaNaListaDeEspera(this);
		else
			utilizarRecurso(this);
	}
	
	public void adicionaNaListaDeEspera(Processo processoEmEspera) {
		getListaDeEspera().add(processoEmEspera);
		
		System.out.println("Processo " + this + " foi adicionado na lista de espera.");
		System.out.println("Lista de espera: " + getListaDeEspera());
	}
	
	private void utilizarRecurso(Processo processo) {
		Random random = new Random();
		int randomUsageTime = USO_PROCESSO_MIN + random.nextInt(USO_PROCESSO_MAX - USO_PROCESSO_MIN);
		
		utilizaRecurso = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Processo " + processo + " está consumindo o recurso por " + randomUsageTime + " ms.");
				RecursoCompartilhado.setEmUso(true, processo);
				try {
					Thread.sleep(randomUsageTime);
				} catch (InterruptedException e) {
					
				}
				System.out.println("Processo " + processo + " parou de consumir o recurso.");
				processo.liberarRecurso();
			}
		});
		utilizaRecurso.start();
	}
	
	// TODO tratar quando ocorrer nullPointer no if
	public void liberarRecurso() {
		RecursoCompartilhado.setEmUso(false, this);
		
		if(!isListaDeEsperaEmpty()) {
			Processo processoEmEspera = getListaDeEspera().removeLast();
			processoEmEspera.acessarRecursoCompartilhado();
			System.out.println("Processo " + processoEmEspera + " foi remivido da lista de espera.");
			System.out.println("Lista de espera: " + getListaDeEspera());
		}
	}
	
	public void interronpeAcessoRecurso() {
		if(utilizaRecurso != null) {
			utilizaRecurso.interrupt();
			System.out.println("Processo " + this + " parou de consumir o recurso.");
			this.liberarRecurso();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		Processo processo = (Processo) obj;
		if(processo == null)
			return false;
		
		return this.pid == processo.pid;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.getPid());
	}
}