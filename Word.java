/*
* Define como a palavra da memoria deve ser
* cada posicao da memoria tem uma palavra que pode ser uma instrucao (ou um dado)
* possui o primeiro registrado = r1(reg de 0 a 9)
* segundo registrador = r2(reg de 0 a 9)
* opcode(instrucao) = opc(___(sem dado) ou DADO(possui um p))
* por ultimo o parametro = p(K ou A(um valor inteiro))
*
* @author MIGUEL ZANELA, JONATHAN CARDARELLI BARBOSA, ALOISIO MIGUEL BERTOLO BASTIAN
*
* @Version 30 novembro de 2020
*
*/

public class Word {
	public Opcode opc;
	public int r1;
	public int r2;
	public int p;

	public Word(Opcode opc, int r1, int r2, int p) {
		this.opc = opc;
		this.r1 = r1;
		this.r2 = r2;
		this.p = p;
	}
	
	@Override
	public String toString(){
		return "[ " + opc + ", " + r1 + ", " + r2 + ", " + p + " ] " + "\n";
	}
}
