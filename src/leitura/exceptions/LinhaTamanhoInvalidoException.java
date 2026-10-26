package leitura.exceptions;

public class LinhaTamanhoInvalidoException extends RuntimeException {

    public LinhaTamanhoInvalidoException(int numLinha, int quantidadeColunasCabecalho, int quantidadeColunasLinha) {
        super(String.format("Linha %d: o cabeçalho possui %d colunas, mas a linha possui %d", numLinha, quantidadeColunasCabecalho, quantidadeColunasLinha));
    }
}
