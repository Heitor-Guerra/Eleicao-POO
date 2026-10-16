package eleicao;

import java.time.LocalDate;
import eleicao.enums.*;

public class Candidato {
    private final int numero;
    private final String nomeUrna;
    private final Partido partido;
    private final int numeroFederacao;
    private final LocalDate dataNascimento;
    private final Genero genero;
    private final SituacaoTurno situacaoTurno;
    private final int codigoMunicipio;
    private final Cargo cargo;

    Candidato(int numero, String nomeUrna, Partido partido, int numeroFederacao,
                     LocalDate dataNascimento, Genero genero, SituacaoTurno situacaoTurno,
                     int codigoMunicipio, Cargo cargo) {
        this.numero = numero;
        this.nomeUrna = nomeUrna;
        this.partido = partido;
        this.numeroFederacao = numeroFederacao;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.situacaoTurno = situacaoTurno;
        this.codigoMunicipio = codigoMunicipio;
        this.cargo = cargo;
    }

    public int getNumero() {
        return numero;
    }

    public String getNomeUrna() {
        return nomeUrna;
    }

    public Partido getPartido() {
        return partido;
    }

    public int getNumeroFederacao() {
        return numeroFederacao;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Genero getGenero() {
        return genero;
    }

    public SituacaoTurno getSituacaoTurno() {
        return situacaoTurno;
    }

    public int getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public boolean isEleito() {
        return situacaoTurno == SituacaoTurno.ELEITO || situacaoTurno == SituacaoTurno.ELEITO_QP || situacaoTurno == SituacaoTurno.ELEITO_MIDIA;
    }

    public boolean isValido() {
        return situacaoTurno == SituacaoTurno.INVALIDO;
    }

    // Para teste por enquanto
    @Override
    public String toString() {
        return nomeUrna;
    }
}
