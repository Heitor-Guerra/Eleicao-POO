package eleicao;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import eleicao.enums.*;

public class Partido {
    private final int numero;
    private final String sigla;
    private final List<Candidato> candidatos;

    Partido(int numero, String sigla) {
        this.numero = numero;
        this.candidatos = new ArrayList<>();
        this.sigla = sigla;
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
        Candidato candidato = new Candidato(numero, nomeUrna, this, numeroFederacao, dataNascimento, genero, situacaoTurno, codigoMunicipio, cargo);
        this.candidatos.add(candidato);
        return candidato;
    }

    public List<Candidato> getCandidatos() {
        return new ArrayList<>(this.candidatos);
    }

    // Para teste por enquanto
    @Override
    public String toString() {
        return sigla;
    }
}