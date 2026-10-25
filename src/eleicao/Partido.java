package eleicao;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import eleicao.enums.*;

public class Partido {
    private final int numero;
    private final String sigla;
    private final List<Candidato> candidatos;
    private int votosLegenda;

    Partido(int numero, String sigla) {
        this.numero = numero;
        this.candidatos = new ArrayList<>();
        this.sigla = sigla;
        this.votosLegenda = 0;
    }

    public int getNumero() {
        return numero;
    }

    public String getSigla() {
        return sigla;
    }

    public Candidato registrarCandidato(int numero, String nomeUrna, int numeroFederacao,
                                        LocalDate dataNascimento, Genero genero, SituacaoTurno situacaoTurno,
                                        int codigoMunicipio, Cargo cargo) {
        Candidato candidato = new Candidato(numero, nomeUrna, this, numeroFederacao, dataNascimento,
                                            genero, situacaoTurno, codigoMunicipio, cargo);
        this.candidatos.add(candidato);
        return candidato;
    }

    public List<Candidato> getCandidatos() {
        return new ArrayList<>(this.candidatos);
    }

    public int getVotosNominais() {
        int total = 0;
        for(Candidato c : this.candidatos) {
            total += c.getVotos();
        }
        return total;
    }

    public int getVotosLegenda() {
        return votosLegenda;
    }

    public int getTotalVotos() {
        return getVotosNominais() + votosLegenda;
    }

    void adicionarVotosLegenda(int quantidade) {
        votosLegenda += quantidade;
    }

    // Para teste por enquanto
    @Override
    public String toString() {
        return sigla;
    }
}