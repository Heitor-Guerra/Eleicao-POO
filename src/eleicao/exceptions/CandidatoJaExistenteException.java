package eleicao.exceptions;

public class CandidatoJaExistenteException extends RuntimeException {
    public CandidatoJaExistenteException(String nome, int numero) {
        super(String.format("O candidato %s (numero %d) já foi registrado na eleicao", nome, numero));
    }
}
