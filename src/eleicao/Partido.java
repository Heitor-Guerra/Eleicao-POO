package eleicao;

import java.util.*;
import java.time.LocalDate;

import eleicao.enums.*;

public class Partido implements Comparable<Partido> {
    private final int numero;
    private final String sigla;
    private final Set<Candidato> candidatos;
    private int votosLegenda;

    Partido(int numero, String sigla) {
        this.numero = numero;
        this.candidatos = new HashSet<>();
        this.sigla = sigla;
        this.votosLegenda = 0;
    }

    public int getNumero() {
        return numero;
    }

    public String getSigla() {
        return sigla;
    }

    Candidato registrarCandidato(int numero, String nomeUrna, int numeroFederacao,
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

    public void adicionarVotosLegenda(int quantidade) {
        votosLegenda += quantidade;
    }

    public long getNumeroEleitos() {
        return this.candidatos.stream().filter(Candidato::isEleito).count();
    }

    public Candidato getCandidatoMaisVotado() {
        List<Candidato> candidatos = new ArrayList<>(this.candidatos.stream().sorted(Collections.reverseOrder()).toList());
        return candidatos.get(0);
    }

    public Candidato getCandidatoMenosVotado() {
        List<Candidato> candidatos = new ArrayList<>(this.candidatos.stream().sorted(Collections.reverseOrder()).toList());
        return candidatos.get(candidatos.size() - 1);
    }

     // Para teste por enquanto
    @Override
    public String toString() {
        return sigla;
    }

    @Override
    public int compareTo(Partido outro) {
        int diffVotos = this.getTotalVotos() - outro.getTotalVotos();
        if (diffVotos == 0) {
            return outro.getNumero() - this.getNumero();
        }
        return diffVotos;
    }
}
