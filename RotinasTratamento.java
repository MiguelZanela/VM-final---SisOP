/*
* serve para receber interrupcoes da cpu, tratar cada um delas e 
* liberar o escalonador para escalonar e o gerente de processos para desalocar um processo
* thread save, somente 1 processo pode ser tratado por vez (Tratando) 
*
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

import java.util.List;
import java.util.concurrent.Semaphore;

public class RotinasTratamento {
    private Semaphore Tratando;   
    
    //construtor da rotinas de tratamento
    public RotinasTratamento(){
        Tratando = new Semaphore(1);
    }

    //traduz o endereço logico para o endereco de memoria fisica
    //verifica a validade do endereco caso seja invalido, interrompe o programa
    private int traduz(int posicao, List<Integer> pagiProg){
        boolean valido = true;
        try {
            pagiProg.get(posicao/VM.tamPagi);
        } catch (Exception e) {
            valido = false;
        }
        if(valido){
            return (pagiProg.get(posicao/VM.tamPagi)*VM.tamPagi)+(posicao%VM.tamPagi);
        }
        else{
            return -1;
        }        
    }

    //trata a interrupcao do timer
    //recebe o contexto atual do processo da cpu
    //salva esse contexto no process control block do processo
    //muda o status do processo para pronto e coloca o mesmo na fila de prontos
    //libera o escalonador para escalonar
    //no final libera o tratando
    public void trataTimer(int id, int pc, int[] reg){
        try {
            Tratando.acquire();
        } catch (Exception e) {}
            for(ProcessControlBlock it: VM.gp.pcbList){
                if(it.getId() == id){
                    it.setStatus(StatusPCB.PRONTO);
                    it.setPc(pc);
                    it.setRegistradores(reg);
                    VM.fp.colocaNaFilaProntos(it);                     
                    break;
                }
            }               
            try { Thread.sleep (1500); } catch (InterruptedException ex) {}
            //VM.gm.dump(0,80);
            VM.semESC.release();
            Escalonador.useESC.release();
            Tratando.release();
    }

    //trata a interrupcao da trap
    //recebe o contexto atual do processo da cpu
    //verifica se o endereco de leitura ou escrita é valido para esse processo
    //verifica se é leitura(1) ou escrita(2)
    //salva esse contexto no process control block do processo
    //libera o escalonador para escalonar
    //no final libera o tratando
    public void trataTrap(int id, int pc, int[] reg, List<Integer> pagiProg){
        try {
            Tratando.acquire();
        } catch (Exception e) {}
            int leituraEscrita = reg[8];
            int posicao = reg[9];
            //verifica se o endereco fornecido é valido
            if(traduz(posicao, pagiProg) > -1 && posicao > -1){
                //vefifica se a instrucao é valida
                if(leituraEscrita == 1 || leituraEscrita == 2){
                    for(ProcessControlBlock it: VM.gp.pcbList){
                        if(it.getId() == id){
                            it.setStatus(StatusPCB.BLOQUEADO);
                            it.setPc((pc+1));
                            it.setRegistradores(reg);
                            it.setValorEscritaLeitura(leituraEscrita);
                            it.setPosicaoEscritaLeitura(traduz(posicao, pagiProg));
                            VM.fb.colocaNaFilaBloqueados(it);
                            VM.fpc.colocaNaFilaPedidosConsole(it);
                            break;
                        }
                    }
                }
                //caso instrucao invalida
                else{
                    for(ProcessControlBlock it: VM.gp.pcbList){
                        if(it.getId() == id){
                            it.setIrtp(Interrupts.intInstrucaoInvalida);
                            VM.gm.dump(id);
                            VM.gp.desalocaPCB(id);
                            break;
                        }
                    }
                }
            }
            //caso endereco invalido
            else{
                for(ProcessControlBlock it: VM.gp.pcbList){
                    if(it.getId() == id){
                        it.setIrtp(Interrupts.intEnderecoInvalido);
                        VM.gm.dump(id);
                        VM.gp.desalocaPCB(id);
                        break;
                    }
                }
            }                           
            try { Thread.sleep (1500); } catch (InterruptedException ex) {}
            //VM.gm.dump(0,80);
            VM.semESC.release();
            Escalonador.useESC.release();
            Tratando.release();
    }

    //trata a interrupcao de IO
    //recebe o id pro processo, muda o status de bloqueado para pronto
    //retira da fila de bloqueados e coloca na fila de prontos
    //seta a cpu como sem interrupcao de IO
    //libera o escalonador para escalonar
    //no final libera o tratando
    public void trataIO(int id){
        try {
            Tratando.acquire();
        } catch (Exception e) {}
            for(ProcessControlBlock it: VM.fb.filaBloqueados){
                if(it.getId() == id){
                    it.setStatus(StatusPCB.PRONTO);
                    VM.fb.retiraDaFilaBloqueados(id);
                    VM.fp.colocaNaFilaProntos(it);
                    break;
                }
            }
            VM.cpu.irptIO = Interrupts.noInterruptIO;
            //VM.gm.dump(0,80);
            VM.semESC.release();
            Tratando.release();
    }

    //trata as interrupcoes por endereco invalido, instrucao invalida, overflow e stop
    //recebe o contexto atual do processo da cpu
    //executa o caso de cada interrupcao, libera o escalonador
    //no final libera o tratando
    public void trataInterrupts(int id, Interrupts interrupcao){
        try {
            Tratando.acquire();
        } catch (Exception e) {}
            switch (interrupcao) { // intEnderecoInvalido, intInstrucaoInvalida, intOverflow, intSTOP
                //interrupcao por endereco invalido
                //percorre todos os process control block até achar um com o mesmo ID
                //da dump da memoria desse processo
                //pede para o gerente de processos desalocar esse processo da memoria
                case intEnderecoInvalido:
                    for(ProcessControlBlock it: VM.gp.pcbList){
                        if(it.getId() == id){
                            VM.gm.dump(id);
                            VM.gp.desalocaPCB(id);
                            break;
                        }
                    }
                    VM.semESC.release();
                    Escalonador.useESC.release();
                    break;

                //interrupcao por intrucao invalida
                //percorre todos os process control block até achar um com o mesmo ID
                //da dump da memoria desse processo
                //pede para o gerente de processos desalocar esse processo da memoria
                case intInstrucaoInvalida:
                    for(ProcessControlBlock it: VM.gp.pcbList){
                        if(it.getId() == id){
                            VM.gm.dump(id);
                            VM.gp.desalocaPCB(id);
                            break;
                        }
                    }
                    VM.semESC.release();
                    Escalonador.useESC.release();
                    break;

                //interrupcao por overflow
                //percorre todos os process control block até achar um com o mesmo ID
                //da dump da memoria desse processo
                //pede para o gerente de processos desalocar esse processo da memoria
                case intOverflow:
                    for(ProcessControlBlock it: VM.gp.pcbList){
                        if(it.getId() == id){
                            VM.gm.dump(id);
                            VM.gp.desalocaPCB(id);
                            break;
                        }
                    }
                    VM.semESC.release();
                    Escalonador.useESC.release();
                    break;

                //interrupcao por stop
                //percorre todos os process control block até achar um com o mesmo ID
                //da dump da memoria desse processo
                //pede para o gerente de processos desalocar esse processo da memoria
                case intSTOP:
                    for(ProcessControlBlock it: VM.gp.pcbList){
                        if(it.getId() == id){                            
                            VM.gm.dump(id);
                            VM.gp.desalocaPCB(id);
                            break;
                        }
                    }
                    VM.semESC.release();
                    Escalonador.useESC.release();
                    break;

                default:
                    break;
            }
            try { Thread.sleep (1500); } catch (InterruptedException ex) {}
            Tratando.release();
    }
}
