package leitura.exceptions;

public class ErroParsingException extends RuntimeException {

    public ErroParsingException(int numLinha, String coluna) {
        super(String.format("Linha %d: Erro ao tentar pegar o valor na coluna %s", numLinha, coluna));
    }
}
