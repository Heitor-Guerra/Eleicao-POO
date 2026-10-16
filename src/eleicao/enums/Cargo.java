package eleicao.enums;

public enum Cargo {

    VEREADOR(13, "Vereador");
    NAO_INFORMADO(-1, "Não informado");

    private final int codigo;
    private final String descricao;

    Cargo(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return this.codigo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public static Cargo deCodigo(int codigo) {
        for (Cargo cargo : values()) {
            if (cargo.codigo == codigo) {
                return cargo;
            }
        }
        return NAO_INFORMADO;
    }
}