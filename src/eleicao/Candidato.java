package eleicao;

import java.time.LocalDate;
import eleicao.enums.*;

public class Candidato {
    private int numero;
    private String nomeUrna;
    private Partido partido;
    private int numeroFederacao;
    private LocalDate dataNascimento;
    private Genero genero;
    private SituacaoTurno situacaoTurno;
    private int codigoMunicipio;
    private Cargo cargo;

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
        return situacaoTurno == 2 || situacaoTurno == 3;
    }

    public boolean isValido() {
        return situacaoTurno != -1;
    }

    // Para teste por enquanto
    @Override
    public String toString() {
        return nomeUrna;
    }
}
