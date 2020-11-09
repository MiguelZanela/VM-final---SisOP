/*
* enum com os possivei opcode(palavras de memoria)
* cada palavra remete a uma instruct da CPU
* 
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

public enum Opcode {
    //serve para representar se a memoria na posicao informada possui um dado e nao esta vazia ou com instruct
    DADO, 
    //serve para representar se a memoria na posicao informada esta vazia e noa possui dado ou instructs
    ___,
    //serve para representar as instructs suportadas pela nossa CPU
    JMP, JMPI, JMPIG, JMPIL, JMPIE, JMPIM, JMPIMG, JMPIML, JMPIME, ADDI, SUBI, ANDI, ORI, LDI, LDD, STD, ADD, SUB, MULT, LDX, STX, SWAP, STOP, TRAP;
}
