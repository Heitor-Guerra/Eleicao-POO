package eleicao.enums;

public enum SituacaoTurno {

    ELEITO(1, "Eleito", true),
    ELEITO_QP(2, "Eleito", true),
    ELEITO_MIDIA(3, "Eleito", true),
    NAO_ELEITO(4, "Não eleito", false),
    SUPLENTE(5, "Suplente", false),
    INVALIDO(-1, "Candidatura Inválida", false);

    private final boolean codigo;
    private final String descricao;
    private final boolean eleito;

    SituacaoTurno(int codigo, String descricao, boolean eleito) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.eleito = eleito;
    }

    public inte getCodigo() {
        return this.descricao;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public boolean isEleito() {
        return this.eleito;
    }

    public static SituacaoTurno deCodigo(int codigo) {
        for (SituacaoTurno situacaoTurno : values()) {
            if (situacaoTurno.codigo == codigo) {
                return situacaoTurno;
            }
        }
        return INVALIDO;
    }
}