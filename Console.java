/*
* Serve como um tratador de IO, caso existam pedidos de console na fila de pedidos, pega o primeiro e executa a leitura ou escrita desse processo.
* na leitura, escreve pedido e entrada e juntamente com a shell, recebe a entrada e escreve diretamente na memoria, na posicao dada pelo processo por DMA
* usa semConsole iniciado com 0 esperando a liberação para executar
* thread save, somente 1 processo com IO e tratado por vez
*
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

import java.util.concurrent.Semaphore;

public class Console extends Thread{
    private Semaphore useConsole;
    public Semaphore consoleEntrada;
    private volatile int cont;
    public int inteiro;
    public int id;
    public int posicaoEscritaLeitura;
    public int valorEscritaLeitura;
    public String nome;
    public boolean mandouPedido;

    //contrutor de console
    public Console(){
        useConsole = new Semaphore(1);
        consoleEntrada = new Semaphore(0);
        mandouPedido = false;
    }

    //apos receber um pedido de leitura, avisa a shell que tem pedido (mandoupedido = true)
    //escreve na tela a solicitação e aguarda parado até receber a entrada, apos receber, 
    //coloca o que recebeu na posicao dada pelo processo, avisa a CPU de interrupcao por IO e libera o console para tratar proximo IO
    //apos receber um pedido de escrita, escreve na tela o dado existente na posicao informada pelo processo, 
    //avisa a CPU de interrupcao por IO e libera o console para tratar proximo IO
    public void run(){
        while(true) {
            try {
                VM.semConsole.acquire();
            } catch (Exception e) {}
                cont = 0;
                try {
                    useConsole.acquire();
                } catch (Exception e) {}
                    while(cont == 0){
                        if(VM.fpc.filaPedidosConsole.size() > 0){ 
                            mandouPedido = true; 
                            id = VM.fpc.filaPedidosConsole.peek().getId();
                            nome = VM.fpc.filaPedidosConsole.peek().getNome();
                            valorEscritaLeitura = VM.fpc.filaPedidosConsole.peek().getValorEscritaLeitura();
                            posicaoEscritaLeitura = VM.fpc.filaPedidosConsole.peek().getPosicaoEscritaLeitura();
                            VM.cpu.idIrptIo = id;
                            if(valorEscritaLeitura == 1){
                                System.out.println("\n------------------------------------------------------");                            
                                System.out.println("O programa: "+VM.fpc.filaPedidosConsole.peek().getNome()+"\nEsta solicitando uma entrada para continuar"+"\nDigite \"ent\" para continuar");
                                System.out.println("------------------------------------------------------\n"); 
                                try {
                                    consoleEntrada.acquire();
                                } catch (Exception e) {}
                                    inteiro = VM.shell.entradaConsole;
                                    VM.m[posicaoEscritaLeitura].p = inteiro;
                                    VM.m[posicaoEscritaLeitura].opc = Opcode.DADO;
                                    VM.cpu.irptIO = Interrupts.intIO;
                                    VM.fpc.retiraDaFilaPedidosConsole();
                                    mandouPedido = false;
                            }
                            else{
                                String escrita = ""+VM.m[posicaoEscritaLeitura];
                                System.out.println("\n------------------------------------------------------");                            
                                System.out.println("O programa: "+VM.fpc.filaPedidosConsole.peek().getNome()+"\nEnvio "+posicaoEscritaLeitura+":"+escrita);
                                System.out.println("------------------------------------------------------\n"); 
                                VM.cpu.irptIO = Interrupts.intIO;
                                VM.fpc.retiraDaFilaPedidosConsole();
                            }
                            cont++;
                            useConsole.release();
                        }
                    }         
        }
    }    
}
