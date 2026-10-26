package leitura.exceptions;

public class ErroLeituraArquivoException extends RuntimeException {

    public ErroLeituraArquivoException(String caminho, Throwable causa) {
        super("Não foi possível ler o arquivo: " + caminho, causa);
    }
}
