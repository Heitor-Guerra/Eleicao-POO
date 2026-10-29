package leitura;

import eleicao.Eleicao;
import eleicao.Partido;
import eleicao.enums.Cargo;
import eleicao.enums.Genero;
import eleicao.enums.SituacaoTurno;
import eleicao.exceptions.CandidatoJaExistenteException;
import eleicao.exceptions.PartidoNaoExistenteException;
import leitura.exceptions.LinhaInvalidaException;

import java.time.LocalDate;
import java.util.List;

public class LeitorCandidatos extends LeitorCSV {
    public LeitorCandidatos(String caminhoArquivo) {
        super(caminhoArquivo, "SG_UE", "CD_CARGO", "NR_PARTIDO", "SG_PARTIDO",
                "CD_SIT_TOT_TURNO", "NR_CANDIDATO", "NM_URNA_CANDIDATO",
                "NR_FEDERACAO", "DT_NASCIMENTO", "CD_GENERO");
    }

    @Override
    protected void processarLinha(List<String> campos, int numeroLinha) throws LinhaInvalidaException {
        int codigoMunicipio = getInteiro(campos, "SG_UE", numeroLinha);
        Eleicao em = Eleicao.INSTANCIA;

        int numeroPartido = getInteiro(campos, "NR_PARTIDO", numeroLinha);
        String siglaPartido = getString(campos, "SG_PARTIDO");
        Partido partido = em.getPartidoByNumero(numeroPartido);
        if (partido == null) {
            partido = em.registraPartido(numeroPartido, siglaPartido);
        }

        Cargo cargo = Cargo.deCodigo(getInteiro(campos, "CD_CARGO", numeroLinha));
        if (cargo != Cargo.VEREADOR) {
            return;
        }

        SituacaoTurno situacao = SituacaoTurno.deCodigo(getInteiro(campos, "CD_SIT_TOT_TURNO", numeroLinha));
        if (situacao == SituacaoTurno.INVALIDO) {
            return;
        }

        if (em.getCodigoMunicipio() != codigoMunicipio) {
            return;
        }

        int numeroCandidato = getInteiro(campos, "NR_CANDIDATO", numeroLinha);
        String nomeUrna = getString(campos, "NM_URNA_CANDIDATO");
        int numeroFederacao = getInteiro(campos, "NR_FEDERACAO", numeroLinha);
        LocalDate dataNascimento = getData(campos, "DT_NASCIMENTO", numeroLinha);
        Genero genero = Genero.deCodigo(getInteiro(campos, "CD_GENERO", numeroLinha));

        try {
            em.registrarCandidato(partido, numeroCandidato,
                    nomeUrna, numeroFederacao, dataNascimento, genero, situacao,
                    codigoMunicipio, cargo);
        } catch (CandidatoJaExistenteException | PartidoNaoExistenteException ex) {
            throw new LinhaInvalidaException(numeroLinha, ex);
        }
    }

}
