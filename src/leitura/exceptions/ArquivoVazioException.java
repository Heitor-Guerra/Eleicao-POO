package leitura.exceptions;

public class ArquivoVazioException extends RuntimeException {
    public ArquivoVazioException(String caminho) {
        super("O arquivo está vazio ou não possui cabeçalho na primeira linha: " + caminho);
    }
}
