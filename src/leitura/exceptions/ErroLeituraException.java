package vereadores.io;

public class ErroLeituraException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErroLeituraException(String mensagem) {
        super(mensagem);
    }

    public ErroLeituraException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}