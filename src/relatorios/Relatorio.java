package relatorios;

import eleicao.Candidato;
import eleicao.Eleicao;
import eleicao.Partido;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Relatorio {
    private static final OutputStream OUTPUT_STREAM = System.out;
    private static final Charset CODIFICACAO_SAIDA = StandardCharsets.UTF_8;
    private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

    public static void printRelatorio() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(OUTPUT_STREAM, CODIFICACAO_SAIDA))) {
            printVagas(writer);
            printEleitos(writer);
            printMaisVotados(writer);
            printPrejudicados(writer);
            printBeneficiados(writer);
            printVotacaoPartidos(writer);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void printVagas(Writer writer) throws IOException {
        String s = String.format(LOCALE, "Número de vagas: %,d%n", Eleicao.INSTANCIA.getNumeroEleitos());
        writer.write(s);
    }


    private static void printEleitos(Writer writer) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%nVereadores eleitos:%n"));
        int i = 1;
        for (Candidato c : Eleicao.INSTANCIA.getEleitosVotosDecrescente()) {
            stringBuilder.append(formatarCandidato(i, c));
            i++;
        }
        writer.write(stringBuilder.toString());
    }


    private static void printMaisVotados(Writer writer) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%nCandidatos mais votados (em ordem decrescente de votação e respeitando número de vagas):%n"));
        int i = 1;
        long num = Eleicao.INSTANCIA.getNumeroEleitos();
        for (Candidato c : Eleicao.INSTANCIA.getCandidatosMaisVotosDecrescente(num)) {
            stringBuilder.append(formatarCandidato(i, c));
            i++;
        }
        writer.write(stringBuilder.toString());
    }


    private static void printPrejudicados(Writer writer) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%nTeriam sido eleitos se a votação fosse majoritária, e não foram eleitos:%n"));
        stringBuilder.append(String.format("(com sua posição no ranking de mais votados)%n"));

        Map<Integer, Integer> posicoes = getPosicoesRanking();

        for (Candidato c : Eleicao.INSTANCIA.getPrejudicados()) {
            stringBuilder.append(formatarCandidato(posicoes.get(c.getNumero()), c));
        }
        writer.write(stringBuilder.toString());
    }


    private static void printBeneficiados(Writer writer) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%nEleitos, que se beneficiaram do sistema proporcional:%n"));
        stringBuilder.append(String.format("(com sua posição no ranking de mais votados)%n"));

        Map<Integer, Integer> posicoes = getPosicoesRanking();

        for (Candidato c : Eleicao.INSTANCIA.getEleitosBeneficiados()) {
            stringBuilder.append(formatarCandidato(posicoes.get(c.getNumero()), c));
        }
        writer.write(stringBuilder.toString());
    }


    private static void printVotacaoPartidos(Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%nVotação dos partidos e número de candidatos eleitos:%n"));

        int i = 1;
        for (Partido partido : Eleicao.INSTANCIA.getPartidosVotosDecrescente()) {
            long total = partido.getTotalVotos();
            long nominais = partido.getVotosNominais();
            long legenda = partido.getVotosLegenda();
            long numeroEleitos = partido.getNumeroEleitos();

            String rotuloVotos = total <= 1 ? "voto" : "votos";
            String rotuloNominais = nominais <= 1 ? "nominal" : "nominais";

            String rotuloEleitos = numeroEleitos <= 1 ? "candidato eleito" : "candidatos eleitos";

            sb.append(String.format(LOCALE,"%d - %s - %d, %,d %s (%,d %s e %,d de legenda), %d %s%n",
                    i, partido.getSigla(), partido.getNumero(), total, rotuloVotos,
                    nominais, rotuloNominais, legenda, numeroEleitos, rotuloEleitos));
            i++;
        }

        writer.write(sb.toString());
    }



    ///// Auxiliares

    private static String formatarCandidato(int posicao, Candidato candidato) {
        String federacao = candidato.getNumeroFederacao() != -1 ? "*" : "";
        return String.format(LOCALE, "%d - %s%s (%s, %,d votos)%n",
            posicao, federacao, candidato.getNomeUrna(), candidato.getPartido().getSigla(), candidato.getVotos());
    }

    private static Map<Integer, Integer> getPosicoesRanking() {
        long num = Eleicao.INSTANCIA.getCandidatos().size();
        List<Candidato> ranking = Eleicao.INSTANCIA.getCandidatosMaisVotosDecrescente(num);
        Map<Integer, Integer> posicoes = new HashMap<>();

        for (int i = 0; i < ranking.size(); i++) {
            posicoes.put(ranking.get(i).getNumero(), i + 1);
        }

        return posicoes;
    }
}
