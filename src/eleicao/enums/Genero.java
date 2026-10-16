package eleicao.enums;

public enum Genero {

    MASCULINO(2, "Masculino"),
    FEMININO(4, "Feminino"),
    NAO_INFORMADO(-1, "Não informado");

    private final int codigo;
    private final String descricao;

    Genero(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Genero deCodigo(int codigo) {
        for (Genero genero : values()) {
            if (genero.codigo == codigo) {
                return genero;
            }
        }
        return NAO_INFORMADO;
    }
}