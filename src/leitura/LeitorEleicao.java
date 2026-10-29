package leitura;

import eleicao.Eleicao;
import eleicao.enums.Cargo;
import leitura.exceptions.LinhaInvalidaException;

import java.util.List;

public final class LeitorEleicao extends LeitorCSV {
    public LeitorEleicao(String caminhoArquivo) {
        super(caminhoArquivo, "CD_MUNICIPIO", "CD_CARGO", "NR_VOTAVEL", "QT_VOTOS");
    }

    @Override
    protected void processarLinha(List<String> campos, int numeroLinha) throws LinhaInvalidaException {
        int codigoMunicipio = getInteiro(campos, "CD_MUNICIPIO", numeroLinha);
        Eleicao em = Eleicao.INSTANCIA;
        if (em.getCodigoMunicipio() != codigoMunicipio) {
            return;
        }

        Cargo cargo = Cargo.deCodigo(getInteiro(campos, "CD_CARGO", numeroLinha));
        if (cargo != Cargo.VEREADOR) {
            return;
        }

        // NR_VOTAVEL deve continuar como texto para distinguir legenda (2 dígitos)
        // de candidato (5 dígitos).
        String numeroVotavel = getString(campos, "NR_VOTAVEL");
        int quantidadeVotos = getInteiro(campos, "QT_VOTOS", numeroLinha);
        if (quantidadeVotos < 0) {
            throw new LinhaInvalidaException(numeroLinha);
        }

        em.registrarVotos(numeroVotavel, quantidadeVotos);
    }
}
