package eleicao.exceptions;

import java.time.LocalDate;

public class EleicaoJaExistenteException extends RuntimeException {
    public EleicaoJaExistenteException(int codigoMunicipio, LocalDate dataEleicao) {
        super(String.format("A eleicão do municipio de codigo %d, no dia %s, já foi registrado na eleicao", codigoMunicipio, dataEleicao));
    }
}
