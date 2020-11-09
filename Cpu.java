/*
* CPU, le instrucoes de processos, traduz essas instrucoes do endereco logico para o fisico,
* testa interrupcoes por endereco invalido, instrucao invalida, trap, stop, overflow, IO, em cada caso, envia um pedido de tratamento de excessao
* especifico para cada um desses.
* usa semCPU iniciado com 0 esperando a liberação para executar
* thread save, somente 1 processo pode ser tratado por vez (useCPU)
* 
*
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

// --------------------- definicoes da CPU ---------------------------------------------------------------
public class Cpu extends Thread {
    // característica do processador: contexto da CPU ...
    private int pc; // ... composto de program counter,
    private int id; //salva o id do processo em execucao
    public int idIrptIo; //salva o id do processo em execucao
    private Word ir; // instruction register,
    private int[] reg; // registradores da CPU
    private Interrupts irpt; // durante instrucao, interrupcao pode ser sinalizada
    public Interrupts irptIO;// durante instrucao, interrupcao pode ser sinalizada
    private List<Integer> pagiProg; // tem a os frames de um programa
    private Word[] m; // CPU acessa MEMORIA, guarda referencia 'm' a ela. memoria nao muda. Eh sempre a mesma.
    private int ciclosCPU = 5; //numero de instruções a serem realizadas de cada processo antes de sair da cpu, modificar para mais ou menos ciclos
    private Semaphore useCPU;

    //construtor da cpu
    public Cpu(Word[] m) { // ref a MEMORIA passada na criacao da CPU
        this.m = m; // usa o atributo 'm' para acessar a memoria.
        reg = new int[10]; // aloca o espaço dos registradores
        useCPU = new Semaphore(1);
        irptIO = Interrupts.noInterruptIO;
    }
    
    //traduz o endereço logico para o endereco de memoria do programa
    //verifica a validade do endereco a cada traducao, caso seja invalido, interrompe o programa
    private int traduz(int pc){
        boolean valido = true;
        try {
            pagiProg.get(pc/VM.tamPagi);
        } catch (Exception e) {
            irpt = Interrupts.intEnderecoInvalido;
            valido = false;
        }
        if(valido){
            return (pagiProg.get(pc/VM.tamPagi)*VM.tamPagi)+(pc%VM.tamPagi);
        }
        else{
            return 0;
        }        
    }

    //seta o id do processo que teve uma interrupcao por IO
    public void setIrpt(int idIrptIo){
        this.idIrptIo = idIrptIo;
    }

    //verifica se o numero informado ou resultante de uma conta não causa overflow      
    private int testaOverflow(int n){        
        if(n > -2147483648 && n < 2147483647){
            return n;
        } else {
            irpt = Interrupts.intOverflow;
            return 0;
        }
    }

    // seta todo o contexto do processo a ser executado na cpu
    public void setContext(ArrayList<Integer> paginas, int pc, int id, int[] reg) {
        this.pc = pc;
        this.pagiProg = paginas;
        this.id = id;
        this.reg = reg;
        irpt = Interrupts.noInterrupt; // reset da interrupcao registrada
    }

    //run da thread cpu, executa os comandos logicos dos programas em processos previamente setados no contexto
    //Somente um processo executando por vez, garantido pelo useCPU
    //espera a liberacao com o semCPU juntamente testando se nao existe nenhuma interrupcao de IO
    //desvia para rotinas de tratamento
    public void run() {
        while(true){
            try {
                useCPU.acquire(); //somente um processo executando por vez
            } catch (Exception e) {}
            if(VM.semCPU.tryAcquire() && irptIO == Interrupts.noInterruptIO){ //testando se a CPU esta liberada para executar e se nao existe interrupcao de IO
                int cont = 0;
                while (irpt == Interrupts.noInterrupt) { // ciclo de instrucoes.
                    // FETCH            
                    cont++;
                    ir = m[traduz(pc)]; // busca posicao da memoria apontada por pc, guarda em ir                
                    // EXECUTA INSTRUCAO NO ir
                    switch (ir.opc) { // DADO,JMP,JMPI,JMPIG,JMPIL,JMPIE,ADDI,SUBI,ANDI,ORI,LDI,LDD,STD,ADD,SUB,MULT,LDX,STX,SWAP,STOP,TRAP;

                        case LDI: // R1 <- p
                        reg[ir.r1] = testaOverflow(ir.p);
                        pc++;
                        break;

                        case LDD: // R1 <- [p]                        
                        reg[ir.r1] = m[traduz(ir.p)].p;
                        pc++;                        
                        break;

                        case LDX: // R1 <- [R2]                        
                        reg[ir.r1] = m[traduz(reg[ir.r2])].p;
                        pc++;                        
                        break;

                        case STD: // [P] <- R1                        
                        m[traduz(ir.p)].opc = Opcode.DADO;
                        m[traduz(ir.p)].p = reg[ir.r1];
                        pc++;                        
                        break;

                        case STX: // [R1] <- R2                        
                        m[traduz(reg[ir.r1])].opc = Opcode.DADO;
                        m[traduz(reg[ir.r1])].p = reg[ir.r2];
                        pc++;                        
                        break;

                        case ADD: // R1 <- R1 + R2
                        reg[ir.r1] = testaOverflow(reg[ir.r1]+reg[ir.r2]);
                        pc++;
                        break;

                        case ADDI: // R1 <- R1 + p
                        reg[ir.r1] = testaOverflow(reg[ir.r1]+ir.p);
                        pc++;
                        break;

                        case SUB: // R1 <- R1 - R2
                        reg[ir.r1] = testaOverflow(reg[ir.r1]-reg[ir.r2]);
                        pc++;
                        break;

                        case SUBI: // R1 <- R1 - p
                        reg[ir.r1] = testaOverflow(reg[ir.r1]-ir.p);
                        pc++;
                        break;

                        case MULT: // R1 <- R1 * R2
                        reg[ir.r1] = testaOverflow(reg[ir.r1]*reg[ir.r2]);
                        pc++;
                        break;

                        case JMP: // PC <- p                        
                        pc = testaOverflow(ir.p);                        
                        break;

                        case JMPI: // PC <- R1                        
                        pc = reg[ir.r1];                        
                        break;

                        case JMPIG: // If R2 > 0 Then PC <- R1 Else PC <- PC +1
                            if (reg[ir.r2] > 0) {
                            pc = reg[ir.r1];
                            break;
                            } else {
                            pc++;
                            break;
                            }                        

                        case JMPIL: // If R2 < 0 Then PC <- R1 Else PC <- PC +1
                            if (reg[ir.r2] < 0) {
                                pc = reg[ir.r1];
                                break;
                            } else {
                                pc++;
                                break;
                            }                        

                        case JMPIE: // If R2 = 0 Then PC <- R1 Else PC <- PC +1
                            if (reg[ir.r2] == 0) {
                                pc = reg[ir.r1];
                                break;
                            } else {
                                pc++;
                                break;
                            }
                        
                        case JMPIM: // PC <- [p] vai para a posicao p na memoria, pega o valor de p da memoria e pula o pc para esse valor p que estiver la
                            pc = m[traduz(ir.p)].p;
                            break;
                        
                        case JMPIMG: // If R1 > 0 Then PC <- [vai para a posicao p, pega o dado p que tiver la e usa esse p como pc] Else PC <- PC +1
                            if(reg[ir.r1] > 0){
                                pc = m[traduz(ir.p)].p;
                                break;        
                            } else {
                                pc++;
                                break;
                            }                        

                        case JMPIML: // If R1 < 0 Then PC <- [vai para a posicao p, pega o dado p que tiver la e usa esse p como pc] Else PC <- PC +1
                            if(reg[ir.r1] < 0){
                                pc = m[traduz(ir.p)].p;
                                break;    
                            } else {
                                pc++;
                                break;
                            }                        

                        case JMPIME: // If R1 = 0 Then PC <- [vai para a posicao p, pega o dado p que tiver la e usa esse p como pc] Else PC <- PC +1
                            if(reg[ir.r1] == 0){
                                pc = m[traduz(ir.p)].p;
                                break;                                
                            } else {
                                pc++;
                                break;
                            }

                        case SWAP: // T <- Ra | Ra <- Rb | Rb <- T
                            int aux;
                            aux = reg[ir.r1];
                            reg[ir.r1] = reg[ir.r2];
                            reg[ir.r2] = aux;
                            pc++;
                            break;                    

                        case STOP: // para execucao
                            irpt = Interrupts.intSTOP;
                            break;

                        case TRAP: // Interrupcao de software     R8 = 1(LEITURA) ou 2(ESCRITA)    R9 = armazena a posicao de memoria logica do IO
                            irpt = Interrupts.trap;
                            break;                           

                        case DADO:
                            pc++;
                            break;

                        default:
                            irpt = Interrupts.intInstrucaoInvalida;
                            break;                
                    }
                    if(irptIO == Interrupts.intIO){ //caso exista interrupcao de IO dentro da execucao de um processo
                        VM.rotTrat.trataIO(idIrptIo);
                    }
                    if(cont == ciclosCPU){ //numero de ciclos de um processo na cpu
                        irpt = Interrupts.timer;
                    }
                }

                //desvia para as rotinas de tratamento
                if (!(irpt == Interrupts.noInterrupt)) {
                    //serve somente para imprimir o suo da cpu por cada processo
                    int numVezesCPU = 0;
                    for(ProcessControlBlock it: VM.gp.pcbList){
                        if(it.getId() == id){
                            it.setNumVezesCpu();
                            numVezesCPU = it.getNumVezesCPU();
                            it.setIrtp(irpt);
                        }
                    }
                    if(Shell.verOs){
                        String aux = "[ ";
                        for(int i=0; i<reg.length; i++){
                            aux = aux+reg[i]+", ";
                        }
                        aux = aux + " ]";
                        System.out.println("id = "+id+"    Timer ="+cont+"     Num Vezes na CPU ="+numVezesCPU+"     pc ="+pc+"     Reg's ="+aux);
                    }

                    //caso timer
                    if(irpt == Interrupts.timer){
                        VM.rotTrat.trataTimer(id,pc,reg);

                    } 
                    //caso trap
                    else if(irpt == Interrupts.trap){
                        VM.rotTrat.trataTrap(id,pc,reg,pagiProg);

                    } 
                    //caso qualquer uma das outras
                    else {
                        VM.rotTrat.trataInterrupts(id,irpt);
                    }           
                }
            }

            //caso interrupcao por IO exista
            else if(irptIO == Interrupts.intIO){
                VM.rotTrat.trataIO(idIrptIo);                
            }
            useCPU.release();
        }
    }    
}