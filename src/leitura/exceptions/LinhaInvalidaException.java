package leitura.exceptions;

public class LinhaInvalidaException extends RuntimeException {

    public LinhaInvalidaException(int numLinha) {
        super(String.format("Linha %d: não foi possível realizar a leitura apropriada da linha", numLinha));
    }
    public LinhaInvalidaException(int numLinha, Throwable causa) {
        super(String.format("Linha %d: não foi possível realizar a leitura apropriada da linha", numLinha), causa);
    }
}
