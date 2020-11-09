/*
* Local destinado para o usuario informar o nome do programa, 
* tamnho da memoria necessario para seu uso, nome do programa e a sua logica 
* 
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

public class Programas {
    private Word[] logica;
    private int tamProg;
    private String nome;

    //contrutor do programa, recebe a logica, tamanho do programa e nome
    public Programas(Word[] logica, int tamProg, String nome){
        this.logica = logica;
        this.tamProg = tamProg;
        this.nome = nome;
    }

    //retorna a logica do programa
    public Word[] getLogica(){
        return this.logica;
    }

    //retorna o tamanho do programa
    public int getTamanhoProg(){
        return this.tamProg;
    }

    //retorna o nome do programa
    public String getNome(){
        return this.nome;
    }
}
