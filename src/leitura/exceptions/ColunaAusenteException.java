package leitura.exceptions;

public class ColunaAusenteException extends RuntimeException {
    public ColunaAusenteException(String coluna) {
        super("A coluna obrigatória '" + coluna + "' não foi encontrada no cabeçalho.");
    }
}
