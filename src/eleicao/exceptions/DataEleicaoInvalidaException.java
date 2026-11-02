package eleicao.exceptions;

import java.time.format.DateTimeParseException;

public class DataEleicaoInvalidaException extends IllegalArgumentException {
    public DataEleicaoInvalidaException(String valor, DateTimeParseException causa) {
        super(String.format(
                "Data da eleição inválida: \"%s\". Informe uma data existente no formato dd/MM/aaaa.",
                valor), causa);
    }
}
