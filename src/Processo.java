import java.util.LinkedList;
import java.util.Random;

/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class Processo {
	
	private int pid;
	private Thread utilizaRecurso;
	private Conexao conexao = new Conexao();
	
	private boolean ehCoordenador;
	private LinkedList<Processo> listaDeEspera;
	private boolean recursoEmUso;
	
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
		if(this.ehCoordenador) {
			listaDeEspera = new LinkedList<>();
			conexao.conectar(this);
			
			if(RecursoCompartilhado.isSendoConsumido())
				RecursoCompartilhado.getConsumidor().interronperAcessoRecurso();
			
			recursoEmUso = false;
		}
	}
	
	public boolean isRecursoEmUso() {
		return encontrarCoordenador().recursoEmUso;
	}
	
	public void setRecursoEmUso(boolean estaEmUso, Processo processo) {
		Processo coordenador = encontrarCoordenador();
		
		coordenador.recursoEmUso = estaEmUso;
		RecursoCompartilhado.setConsumidor(coordenador.recursoEmUso ? processo : null);
	}
	
	public boolean isListaDeEsperaEmpty() {
		return getListaDeEspera().isEmpty();
	}
	
	private void removerDaListaDeEspera(Processo processo) {
		if(getListaDeEspera().contains(processo))
			getListaDeEspera().remove(processo);
	}
	
	private LinkedList<Processo> getListaDeEspera() {
		return encontrarCoordenador().listaDeEspera;
	}
	
	private Processo encontrarCoordenador() {
		Processo coordenador = null;
		for (Processo p : Anel.processosAtivos) {
			if (p.isEhCoordenador()) {
				coordenador = p;
				break;
			}
		}
		
		if(coordenador == null) {
			coordenador = comecarEleicao();
			System.out.println("Eleicao concluida com sucesso. O novo coordenador eh " + coordenador + ".");
		}
		return coordenador;
	}

	private Processo comecarEleicao() {
		Eleicao eleicao = new Eleicao();
		Processo coordenador = eleicao.realizarEleicao(getPid());
		
		if(coordenador == null)
			comecarEleicao();
		
		return coordenador;
	}
	
	public void acessarRecursoCompartilhado() {
		if(RecursoCompartilhado.isUsandoRecurso(this) || this.isEhCoordenador())
			return;
		
		String resultado = conexao.realizarRequisicao("Processo " + this + " quer consumir o recurso.\n");
		
		System.out.println("Resultado da requisicao do processo " + this + ": " + resultado);
		
		if(resultado.equals(Conexao.PERMITIR_ACESSO))
			utilizarRecurso(this);
		else if(resultado.equals(Conexao.NEGAR_ACESSO))
			adicionarNaListaDeEspera(this);
		else
			System.out.println("ERROR. A mensagem recebida nao foi compreendida.");
	}
	
	public void adicionarNaListaDeEspera(Processo processoEmEspera) {
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
				setRecursoEmUso(true, processo);
				try {
					Thread.sleep(randomUsageTime);
				} catch (InterruptedException e) { }
				
				System.out.println("Processo " + processo + " parou de consumir o recurso.");
				processo.liberarRecurso();
			}
		});
		utilizaRecurso.start();
	}
	
	private void liberarRecurso() {
		setRecursoEmUso(false, this);
		
		if(!isListaDeEsperaEmpty()) {
			Processo processoEmEspera = getListaDeEspera().removeFirst();
			processoEmEspera.acessarRecursoCompartilhado();
			System.out.println("Processo " + processoEmEspera + " foi removido da lista de espera.");
			System.out.println("Lista de espera: " + getListaDeEspera());
		}
	}
	
	public void destruir() {
		if(!this.isEhCoordenador()) {
			removerDaListaDeEspera(this);
			if(RecursoCompartilhado.isUsandoRecurso(this)) {
				interronperAcessoRecurso();
				liberarRecurso();
			}
		} else
			conexao.encerrarConexao();
		
		Anel.processosAtivos.remove(this);
	}
	
	private void interronperAcessoRecurso() {
		if(utilizaRecurso != null)
			utilizaRecurso.interrupt();
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