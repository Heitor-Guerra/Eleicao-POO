package eleicao.exceptions;

public class PartidoJaExistenteException extends RuntimeException {
    public PartidoJaExistenteException(String sigla, int numero) {
        super(String.format("O partido %s (numero %d) já foi registrado na eleicao", sigla, numero));
    }
}
