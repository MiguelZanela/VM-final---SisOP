/*
* classe que molda o que um process control block do nosso sistema precisa possuir de informacoes
* armazena todas as informacoes da vida de um processo no sistema
* 
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

import java.util.ArrayList;

public class ProcessControlBlock {
    public ArrayList<Integer> pagiProg;
    public int id;
    public int pc;
    public StatusPCB estado;
    public int[] reg;
    public String nome;
    public int nVezesCPU;
    public Interrupts irpt;
    public int valorEscritaLeitura;
    public int posicaoEscritaLeitura;

    //contrutor do process control block,
    //cria uma lista vazia de processos e seta todo o contexto inicial do mesmo
    public ProcessControlBlock(int id) {
        pagiProg = new ArrayList<>();
        this.id = id;
        this.irpt = Interrupts.noInterrupt;
        this.pc = 0;
        this.estado = StatusPCB.PRONTO;
        this.nVezesCPU = 0;
        this.valorEscritaLeitura = 0;
        this.posicaoEscritaLeitura = 0;
        reg = new int[10];
    }

    //adiciona paginas a um processo existente
    public void addPaginas(ArrayList<Integer> paginas){
        this.pagiProg = paginas;
    }

    //recebe e modifica o status de um processo
    public void setIrtp(Interrupts irtp){
        this.irpt = irtp;
    }

    //recebe uma interrupt e seta no irtp
    public void setStatus(StatusPCB estado){
        this.estado = estado;
    }

    //recebe e modifica um vetor de registradores de um processo
    public void setRegistradores(int[] registradores){
        this.reg = registradores;
    }

    //recebe e modifica o pc de um processo
    public void setPc(int pc){
        this.pc = pc;
    }

    //recebe o nome de um processo
    public void setNome(String nome){
        this.nome = nome;
    }

    //adiciona 1 ao numero de vezes que o processo passou pela cpu
    public void setNumVezesCpu(){
        this.nVezesCPU++;
    }

    //recebe o valor de escritaLeitura
    public void setValorEscritaLeitura(int escritaLeitura){
        this.valorEscritaLeitura = escritaLeitura;
    } 

    //recebe a posicao de escrita ou leitura
    public void setPosicaoEscritaLeitura(int posicaoEscritaLeitura){
        this.posicaoEscritaLeitura = posicaoEscritaLeitura;
    }

    //retorna a lista de paginas de um processo
    public ArrayList<Integer> getLista(){
        return this.pagiProg;
    }

    //retorna a interrupt de um processo
    public Interrupts getIrtp(){
        return this.irpt;
    }

    //retorna a lista de registradores do processo
    public int[] getReg(){
        return this.reg;
    }

    //retorna o id de um processo
    public int getId(){
        return this.id;
    }
    
    //retorna o valor de escritaLeitura
    public int getValorEscritaLeitura(){
        return this.valorEscritaLeitura;
    } 

    //retorna a posicao de escrita ou leitura
    public int getPosicaoEscritaLeitura(){
        return this.posicaoEscritaLeitura;
    }

    //retorna o status de um processo
    public StatusPCB getStatus(){
        return this.estado;
    }

    //retorna o nome de um processo
    public String getNome(){
        return this.nome;
    }

    //retorna o numero de vezes que o processo passou pela cpu
    public int getNumVezesCPU(){
        return this.nVezesCPU;
    }

    //imprime o id, pc, estado e paginas de um processo
    @Override
    public String toString(){
        String identi = "id = "+this.id + " ";
        String estadoPc = "pc = "+this.pc + " ";
        String status = "estado = "+this.estado + " ";
        String aux = "";
        for(Integer it: pagiProg){
            aux = aux+" "+it;
        }
        return identi+estadoPc+status+aux;
    }
}