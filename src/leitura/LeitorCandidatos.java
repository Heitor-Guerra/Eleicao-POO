package leitura;

import eleicao.Candidato;
import eleicao.Partido;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class LeitorCandidatos {
    private static final String ENCODING = "ISO-8859-1";
    private static final char SEPARADOR = ';';
    private static final String NULO = "#NULO";
    private static final String NAO_EXISTE = "#NE";
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Map<Integer, Candidato> candidatos;
    private final Map<Integer, Partido> partidos;

    public LeitorCandidatos() {
        this.candidatos = new HashMap<>();
        this.partidos = new HashMap<>();
    }


    public Map<Integer, Candidato> lerArquivoCandidatos(String caminhoArquivo, int codigoMunicipio) throws IOException {

        try (BufferedReader leitor = new BufferedReader( new InputStreamReader(new FileInputStream(caminhoArquivo), ENCODING))) {
            String linha;
            boolean primeiraLinha = true;
            Map<String, Integer> headers = new HashMap<>();

            while ((linha = leitor.readLine()) != null) {
                if (primeiraLinha) {
                    headers = processarHeaders(linha);
                    primeiraLinha = false;
                    continue;
                }

                Candidato candidato = processarLinhaCandidato(linha, headers, codigoMunicipio);
                if (candidato != null) {
                    candidatos.put(candidato.getNumero(), candidato);
                }
            }
        }

        return candidatos;
    }

    private Map<String, Integer> processarHeaders(String linha) {
        // TODO
    }

    private Candidato processarLinhaCandidato(String linha,
                                              Map<String, Integer> indices,
                                              int codigoMunicipio) {
        // TODO
    }

    public Map<Integer, Candidato> getCandidatos() {
        return candidatos;
    }

    public Map<Integer, Partido> getPartidos() {
        return partidos;
    }
}
