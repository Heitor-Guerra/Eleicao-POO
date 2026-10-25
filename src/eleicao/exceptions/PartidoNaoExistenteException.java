package eleicao.exceptions;

public class PartidoNaoExistenteException extends RuntimeException {
    public PartidoNaoExistenteException(int numero) {
        super(String.format("O partido de numero %d nao foi registrado na eleicao", numero));
    }
}
