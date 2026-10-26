package relatorios;

import eleicao.Candidato;
import eleicao.Eleicao;
import eleicao.Partido;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
            printPrimeiroUltimoPartido(writer);
            printEleitosFaixaEtaria(writer);
            printEleitosPorGenero(writer);
            printTotalVotos(writer);
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

    private static void printPrimeiroUltimoPartido(Writer writer) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%nPrimeiro e último colocados de cada partido:%n"));

        List<Partido> partidos = Eleicao.INSTANCIA.getPartidos().stream()
                .filter(p -> p.getCandidatos().stream().anyMatch(c -> c.getVotos() > 0))
                .sorted(Comparator.comparingInt((Partido partido) -> partido.getCandidatoMaisVotado().getVotos())
                        .reversed().thenComparingInt(Partido::getNumero))
                .toList();

        int posicao = 1;

        for (Partido partido : partidos) {
            List<Candidato> candidatos = partido.getCandidatos().stream()
                    .sorted(Collections.reverseOrder())
                    .toList();

            Candidato primeiro = candidatos.get(0);
            Candidato ultimo = candidatos.get(candidatos.size() - 1);

            sb.append(String.format(
                    LOCALE,
                    "%d - %s - %d, %s (%d, %,d %s) / %s (%d, %,d %s)%n",
                    posicao,
                    partido.getSigla(),
                    partido.getNumero(),
                    normalizarNome(primeiro),
                    primeiro.getNumero(),
                    primeiro.getVotos(),
                    primeiro.getVotos() <= 1 ? "voto" : "votos",
                    normalizarNome(ultimo),
                    ultimo.getNumero(),
                    ultimo.getVotos(),
                    ultimo.getVotos() <= 1 ? "voto" : "votos"
            ));

            posicao++;
        }

        writer.write(sb.toString());
    }

    private static void printEleitosFaixaEtaria(Writer writer) throws IOException {
        List<Candidato> eleitos = Eleicao.INSTANCIA.getEleitosVotosDecrescente();
        long totalEleitos = eleitos.size();

        long abaixoDe30 = 0;
        long de30a39 = 0;
        long de40a49 = 0;
        long de50a59 = 0;
        long aPartirDe60 = 0;

        for (Candidato candidato : eleitos) {
            long idade = candidato.getIdade(Eleicao.INSTANCIA.getDataEleicao());

            if (idade < 30) {
                abaixoDe30++;
            } else if (idade < 40) {
                de30a39++;
            } else if (idade < 50) {
                de40a49++;
            } else if (idade < 60) {
                de50a59++;
            } else {
                aPartirDe60++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%nEleitos, por faixa etária (na data da eleição):%n"));

        sb.append(formatarFaixaEtaria("      Idade < 30", abaixoDe30, totalEleitos));
        sb.append(formatarFaixaEtaria("30 <= Idade < 40", de30a39, totalEleitos));
        sb.append(formatarFaixaEtaria("40 <= Idade < 50", de40a49, totalEleitos));
        sb.append(formatarFaixaEtaria("50 <= Idade < 60", de50a59, totalEleitos));
        sb.append(formatarFaixaEtaria("60 <= Idade     ", aPartirDe60, totalEleitos));

        writer.write(sb.toString());
    }

    private static void printEleitosPorGenero(Writer writer) throws IOException {
        List<Candidato> eleitos = Eleicao.INSTANCIA.getEleitosVotosDecrescente();
        long totalEleitos = eleitos.size();

        long feminino = 0;
        long masculino = 0;

        for (Candidato candidato : eleitos) {
            switch (candidato.getGenero()) {
                case FEMININO -> feminino++;
                case MASCULINO -> masculino++;
                case NAO_INFORMADO -> {}
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%nEleitos, por gênero:%n"));
        sb.append(formatarGenero("Feminino", feminino, totalEleitos));
        sb.append(formatarGenero("Masculino", masculino, totalEleitos));

        writer.write(sb.toString());
    }

    private static void printTotalVotos(Writer writer) throws IOException {
        int totalValidos = Eleicao.INSTANCIA.getTotalVotosValidos();
        int totalNominais = Eleicao.INSTANCIA.getTotalVotosNominais();
        int totalLegenda = Eleicao.INSTANCIA.getTotalVotosLegenda();

        double percentualNominais = totalValidos == 0 ? 0.0 : totalNominais * 100.0 / totalValidos;
        double percentualLegenda = totalValidos == 0 ? 0.0 : totalLegenda * 100.0 / totalValidos;

        String resultado = String.format(
                LOCALE,
                "%nTotal de votos válidos:    %,d%n" +
                        "Total de votos nominais:   %,d (%.2f%%)%n" +
                        "Total de votos de legenda: %,d (%.2f%%)%n",
                totalValidos,
                totalNominais,
                percentualNominais,
                totalLegenda,
                percentualLegenda
        );

        writer.write(resultado);
    }

    ///// Auxiliares
    private static String formatarCandidato(int posicao, Candidato candidato) {
        String federacao = candidato.getNumeroFederacao() != -1 ? "*" : "";

        return String.format(
                LOCALE,
                "%d - %s%s (%s, %,d votos)%n",
                posicao,
                federacao,
                normalizarNome(candidato),
                candidato.getPartido().getSigla(),
                candidato.getVotos()
        );
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

    private static String normalizarNome(Candidato candidato) {
        return candidato.getNomeUrna().trim().replaceAll("\\s+", " ");
    }

    private static String formatarFaixaEtaria(String faixa, long quantidade, long total) {
        double percentual = total == 0 ? 0.0 : quantidade * 100.0 / total;
        return String.format(LOCALE, "%s: %d (%.2f%%)%n", faixa, quantidade, percentual);
    }

    private static String formatarGenero(String genero, long quantidade, long total) {
        double percentual = total == 0 ? 0.0 : quantidade * 100.0 / total;
        return String.format(LOCALE, "%s: %d (%.2f%%)%n", genero, quantidade, percentual);
    }
}
