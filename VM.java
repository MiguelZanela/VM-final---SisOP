/*
* da inicio na nossa VM, com o parametros iniciais
* e funcoes essenciais para seu funcionamento
* 
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

import java.util.concurrent.Semaphore;

public class VM {
	public static int tamMem;
	public static int tamPagi;
	public static Word[] m;
	public static Cpu cpu;
	public static gerenteDeMemoria gm;
	public static GerenteDeProcessos gp;
	public static FilaProntos fp;
	public static FilaBloqueados fb;
	public static FilaPedidosConsole fpc;
	public static RotinasTratamento rotTrat;
	public static Escalonador esc;
	public static Semaphore semESC;
	public static Semaphore semCPU;
	public static Semaphore semConsole;
	public static Shell shell;
	public static Console console;

	//inicia a VM, com o tamanho da memoria informado, e o tamanho da pagina da memoria
	//cria um console, shell, escalonador, rotina de tratamento, fila de prontos,
	//fila de bloqueados, fila de pedidos console, gerente de processos,
	//gerente de memoria e cpu
	//inicia os semESC, semCPU, semConsole com 0
	//da start nas threads, cpu,esc,shell e console
	public VM() {
		tamMem = 1024;  // colocar o tamanho da memoria
		tamPagi = 16; //colocar o tamanho da pagina		
		m = new Word[tamMem]; // m ee a memoria
		console = new Console();
		shell = new Shell();
		esc = new Escalonador();
		rotTrat = new RotinasTratamento();
		fp = new FilaProntos();
		fb = new FilaBloqueados();
		fpc = new FilaPedidosConsole();
		gp = new GerenteDeProcessos();
		gm = new gerenteDeMemoria();
		cpu = new Cpu(m);
		semESC = new Semaphore(0);
		semCPU = new Semaphore(0);	
		semConsole = new Semaphore(0);
		
		//da inicio nas trheads
		cpu.start();
		esc.start();
		shell.start();	
		console.start();
	}
	
	public static void main(String args[]){
		//coloca a vm para rodar
		VM vm = new VM();
    }		
}