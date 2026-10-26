package leitura;

import eleicao.Eleicao;
import eleicao.EleicaoMunicipio;
import eleicao.Partido;
import eleicao.enums.Cargo;
import eleicao.enums.Genero;
import eleicao.enums.SituacaoTurno;
import eleicao.exceptions.CandidatoJaExistenteException;
import eleicao.exceptions.PartidoJaExistenteException;
import leitura.exceptions.LinhaInvalidaException;

import java.time.LocalDate;
import java.util.List;

public class LeitorCandidatos extends LeitorCSV {
    private final LocalDate dataEleicao;

    public LeitorCandidatos(String caminhoArquivo, LocalDate dataELeicao) {
        super(caminhoArquivo);
        this.dataEleicao = dataELeicao;
    }

    @Override
    protected void processarLinha(List<String> campos, int numeroLinha) throws LinhaInvalidaException {
        Cargo cargo = Cargo.deCodigo(getInteiro(campos, "CD_CARGO", numeroLinha));
        if (cargo != Cargo.VEREADOR) {
            return;
        }

        int codigoMunicipio = getInteiro(campos, "SG_UE", numeroLinha);
        EleicaoMunicipio em = Eleicao.INSTANCIA.getEleicaoByCodigoMunicipio(codigoMunicipio);
        if(em == null) {
            em = Eleicao.INSTANCIA.registrarEleicao(codigoMunicipio, dataEleicao);
        }

        SituacaoTurno situacao = SituacaoTurno.deCodigo(getInteiro(campos, "CD_SIT_TOT_TURNO", numeroLinha));
        if (situacao == SituacaoTurno.INVALIDO) {
            return;
        }

        int numeroCandidato = getInteiro(campos, "NR_CANDIDATO", numeroLinha);
        String nomeUrna = getString(campos, "NM_URNA_CANDIDATO");
        int numeroPartido = getInteiro(campos, "NR_PARTIDO", numeroLinha);
        String siglaPartido = getString(campos, "SG_PARTIDO");
        int numeroFederacao = getInteiro(campos, "NR_FEDERACAO", numeroLinha);
        LocalDate dataNascimento = getData(campos, "DT_NASCIMENTO", numeroLinha);
        Genero genero = Genero.deCodigo(getInteiro(campos, "CD_GENERO", numeroLinha));

        try {
            Partido partido = em.getPartidoByNumero(numeroPartido);
            if (partido == null) {
                partido = em.registraPartido(numeroPartido, siglaPartido);
            }

            if (em.getCandidatoByNumero(numeroCandidato) == null) {
                em.registrarCandidato(partido, numeroCandidato,
                        nomeUrna, numeroFederacao, dataNascimento, genero, situacao,
                        codigoMunicipio, cargo);
            }
        } catch (CandidatoJaExistenteException | PartidoJaExistenteException ex) {
            throw new LinhaInvalidaException(numeroLinha);
        }
    }

}
