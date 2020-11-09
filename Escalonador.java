/*
* escalona processos na fila de pronto, metodo usado é Round-robin
* usa semESC iniciado com 0 esperando a liberação para executar
* thread save, somente 1 processo pode ser escalonado por vez (useESC)
*
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

import java.util.concurrent.Semaphore;

public class Escalonador extends Thread {
    public static Semaphore useESC;
    private volatile int cont;

    //construtor do escalonador
    public Escalonador(){
        useESC = new Semaphore(1);
    }
    
    //run da thread escalonador, espera alguem mandar ela executar, 
    //apos alguem mandar executar, 1 processo por vez,
    //espera que a rotina de tratamento libere o uso da cpu para voltar a escalonar
    public void run(){
        while(true) {
            try {
                VM.semESC.acquire();
            } catch (Exception e) {}
                cont = 0;
                try {
                    useESC.acquire();
                } catch (Exception e) {}
                    //apos liberado, fica testando se a fila é > 0, caso seja executa 1 vez e espera a liberacao
                    //e caso seja 0, continua esperando para escalonar
                    while(cont == 0){
                        //pega o primeiro da fila de prontos, muda status para executando, seta contexto na cpu, retira da fila de prontos, libera cpu.
                        if(VM.fp.filaProntos.size() > 0){
                            VM.fp.filaProntos.peek().setStatus(StatusPCB.EXECUTANDO);
                            VM.cpu.setContext(VM.fp.filaProntos.peek().getLista(), VM.fp.filaProntos.peek().pc, VM.fp.filaProntos.peek().id, VM.fp.filaProntos.peek().getReg());            
                            VM.fp.retiraDaFilaProntos();
                            VM.semCPU.release();
                            cont++;
                        }
                    }         
        }        
    }
}
