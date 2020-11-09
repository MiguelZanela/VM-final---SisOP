/*
* enum com as possiveis interrupcoes da CPU,
* tanto de IO como por erros
* 
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

public enum Interrupts {
    //interrupcoes por erros ou definicoes do programa ou usuario
    noInterrupt, intEnderecoInvalido, intInstrucaoInvalida, intSTOP, intOverflow, timer, 
    //interrupcoes por IO do processo
    trap, intIO, noInterruptIO;
}