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

/** Singleton de ELeicao */
public enum Eleicao {
    INSTANCIA;

    private static final int[] VOTOS_NAO_VALIDOS = {95, 96, 97, 98};
    private int codigoMunicipio;
    private LocalDate dataEleicao;
    private Map<Integer, Partido> partidos;
    private Map<Integer, Candidato> candidatos;

    public void inicializar(int codigoMunicipio, LocalDate dataEleicao) {
        this.codigoMunicipio = codigoMunicipio;
        this.dataEleicao = dataEleicao;
        this.partidos = new HashMap<>();
        this.candidatos = new HashMap<>();
    }
    public int getCodigoMunicipio() {
        return this.codigoMunicipio;
    }

    public LocalDate getDataEleicao() {
        return dataEleicao;
    }

    public Partido registraPartido(int numero, String sigla) throws PartidoJaExistenteException {
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

    public Candidato registrarCandidato(Partido partido, int numeroCandidato,
                                        String nomeUrna, int numeroFederacao,
                                        LocalDate dataNascimento, Genero genero,
                                        SituacaoTurno situacaoTurno, int codigoMunicipio,
                                        Cargo cargo) throws CandidatoJaExistenteException,
            PartidoNaoExistenteException {
        if (candidatos.containsKey(numeroCandidato)) {
            throw new CandidatoJaExistenteException(nomeUrna, numeroCandidato);
        }
        Candidato c = partido.registrarCandidato(numeroCandidato, nomeUrna,numeroFederacao,
                dataNascimento, genero, situacaoTurno, codigoMunicipio, cargo);
        candidatos.put(c.getNumero(), c);
        return c;
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

    /** Adiciona os votos, considerando o número de dígitos (5 para nominal e 2 para legenda). */
    public void registrarVotos(String numeroVotoTexto, int quantidade) {
        String numeroLimpo = numeroVotoTexto.trim();

        int numeroVoto;
        try {
            numeroVoto = Integer.parseInt(numeroLimpo);
        } catch (NumberFormatException ex) {
            return;
        }

        for (int codigo : VOTOS_NAO_VALIDOS) {
            if (numeroVoto == codigo) {
                return;
            }
        }


        if (numeroLimpo.length() == 5) {
            Candidato candidato = candidatos.get(numeroVoto);
            if (candidato == null) {
                return;
            }

            candidato.adicionarVotos(quantidade);
        } else if (numeroLimpo.length() == 2) {
            Partido partido = partidos.get(numeroVoto);
            if (partido == null) {
                return;
            }

            partido.adicionarVotosLegenda(quantidade);
        }
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
