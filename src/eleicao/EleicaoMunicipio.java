package eleicao;

import eleicao.enums.Cargo;
import eleicao.enums.Genero;
import eleicao.enums.SituacaoTurno;
import eleicao.exceptions.CandidatoJaExistenteException;
import eleicao.exceptions.PartidoJaExistenteException;
import eleicao.exceptions.PartidoNaoExistenteException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EleicaoMunicipio {
    private static final int[] VOTOS_NAO_VALIDOS = {95, 96, 97, 98};

    private final int codigoMunicipio;
    private final LocalDate dataEleicao;
    private final Map<Integer, Partido> partidos;
    private final Map<Integer, Candidato> candidatos;


    EleicaoMunicipio(LocalDate dataEleicao, int codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
        this.dataEleicao = dataEleicao;
        this.partidos = new HashMap<>();
        this.candidatos = new HashMap<>();
    }

    public int getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public LocalDate getDataEleicao() {
        return dataEleicao;
    }

    public Partido registraPartido(int numero, String sigla) {
        if (partidos.containsKey(numero)) {
            throw new PartidoJaExistenteException(sigla, numero);
        }
        Partido partido = new Partido(numero, sigla);
        partidos.put(numero, partido);
        return partido;
    }

    /** Pode retornar nulo, se o partido não estiver registrado */
    public Partido getPartidoByNumero(int numero) {
        return this.partidos.get(numero);
    }


    public List<Partido> getPartidos() {
        return new ArrayList<>(partidos.values());
    }

    public Candidato registrarCandidato(int numeroPartido, int numeroCandidato,
                                        String nomeUrna, int numeroFederacao,
                                        LocalDate dataNascimento, Genero genero,
                                        SituacaoTurno situacaoTurno, int codigoMunicipio,
                                        Cargo cargo) {
        if (candidatos.containsKey(numeroCandidato)) {
            throw new CandidatoJaExistenteException(nomeUrna, numeroCandidato);
        }
        Partido partido = partidos.get(numeroPartido);
        if (partido == null) {
            throw new PartidoNaoExistenteException(numeroPartido);
        }
        return partido.registrarCandidato(numeroCandidato, nomeUrna,numeroFederacao,
                dataNascimento, genero, situacaoTurno, codigoMunicipio, cargo);
    }

    boolean contemCandidato(int numero) {
        return candidatos.containsKey(numero);
    }

    public List<Candidato> getCandidatos() {
        return new ArrayList<>(candidatos.values());
    }

    /** Pode retornar nulo se o candidato não existir*/
    public Candidato getCandidatoByNumero(int numero) {
        return candidatos.get(numero);
    }

    /** Adiciona os votos, considerando o número de dígitos (5 para nominal e 2 para legenda).
     *  Retorna booleano que indica o sucesso. */
    public boolean registrarVotos(String numeroVotoTexto, int quantidade) {
        String numeroLimpo = numeroVotoTexto.trim();

        int numeroVoto;
        try {
            numeroVoto = Integer.parseInt(numeroLimpo);
        } catch (NumberFormatException ex) {
            return false;
        }

        for (int codigo : VOTOS_NAO_VALIDOS) {
            if (numeroVoto == codigo) {
                return false;
            }
        }


        if (numeroLimpo.length() == 5) {
            Candidato candidato = candidatos.get(numeroVoto);
            if (candidato == null) {
                return false;
            }

            candidato.adicionarVotos(quantidade);
            return true;
        } else if (numeroLimpo.length() == 2) {
            Partido partido = partidos.get(numeroVoto);
            if (partido == null) {
                return false;
            }

            partido.adicionarVotosLegenda(quantidade);
            return true;
        }
        return false;
    }

    public int getTotalVotosNominais() {
        int total = 0;
        for(Candidato c : candidatos.values()) {
            total += c.getVotos();
        }
        return total;
    }

    public int getTotalVotosLegenda() {
        int total = 0;
        for(Partido p : partidos.values()) {
            total += p.getVotosLegenda();
        }
        return total;
    }

    public long getTotalVotosValidos() {
        return getTotalVotosNominais() + getTotalVotosLegenda();
    }
}
