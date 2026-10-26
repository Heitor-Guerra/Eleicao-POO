package leitura.exceptions;

public class CabecalhoRepetidoException extends RuntimeException {
    public CabecalhoRepetidoException(String coluna) {
        super(String.format("A coluna '%s' aparece mais de uma vez no cabeçalho.", coluna));
    }
}
