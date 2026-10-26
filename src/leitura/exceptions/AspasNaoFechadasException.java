package leitura.exceptions;

public class AspasNaoFechadasException extends RuntimeException {

    public AspasNaoFechadasException(int numLinha) {
        super(String.format("Linha %d: há um campo com aspas não fechadas.", numLinha));
    }
}
