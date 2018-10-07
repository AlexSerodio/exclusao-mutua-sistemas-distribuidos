/**
 * 
 * @author Alex Serodio Goncalves e Luma Kuhl
 *
 */
public class Main {
	
	public static void main(String[] args) {
		ControladorDeProcessos anelLogico = new ControladorDeProcessos();
		
		anelLogico.criarProcessos();
		anelLogico.inativarCoordenador();
		anelLogico.inativarProcesso();
		anelLogico.acessarRecurso();
	}

}
