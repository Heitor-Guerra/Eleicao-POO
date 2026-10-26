import eleicao.Candidato;
import eleicao.Eleicao;
import eleicao.EleicaoMunicipio;
import leitura.LeitorCandidatos;
import leitura.LeitorEleicao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Ponto de entrada para testar a leitura dos CSVs de candidatos e votação. */
public final class Main {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter
            .ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"));

    private Main() { }

    public static void main(String[] args) {
        try {
            executar(args);
        } catch (DateTimeParseException ex) {
            System.err.println("Data inválida. Use dd/MM/aaaa, por exemplo 06/10/2024.");
            imprimirUso();
        } catch (IOException ex) {
            System.err.println("Erro de leitura dos CSVs: " + ex.getMessage());
        } catch (RuntimeException ex) {
            System.err.println("Erro ao processar a eleição: " + ex.getMessage());
        }
    }

    public static void executar(String[] args) throws IOException {
        if (args == null || args.length != 4) {
            throw new IllegalArgumentException(
                    "São necessários quatro argumentos: município, candidatos, votação e data.");
        }

        final int codigoMunicipio;
        try {
            codigoMunicipio = Integer.parseInt(args[0]);
            if (codigoMunicipio <= 0) {
                throw new NumberFormatException("O código deve ser positivo.");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "O primeiro argumento deve ser um código de município positivo.", ex);
        }

        LocalDate dataEleicao = LocalDate.parse(args[3], FORMATO_DATA);

        LeitorCandidatos l1 = new LeitorCandidatos(args[1], dataEleicao);
        LeitorEleicao l2 = new LeitorEleicao(args[2]);
        l1.le();
        l2.le();

        EleicaoMunicipio em = Eleicao.INSTANCIA.getEleicaoByCodigoMunicipio(codigoMunicipio);

        System.out.println("Leitura dos arquivos concluída.");
        System.out.println("Município: " + em.getCodigoMunicipio());
        System.out.println("Data da eleição: " + dataEleicao.format(FORMATO_DATA));
        System.out.println("Partidos registrados: " + em.getPartidos().size());
        System.out.println("Candidatos registrados: " + em.getCandidatos().size());
        System.out.println("Total de votos nominais: " + em.getTotalVotosNominais());
        System.out.println("Total de votos de legenda: " + em.getTotalVotosLegenda());
        System.out.println("Total de votos válidos: " + em.getTotalVotosValidos());

        List<Candidato> ranking = em.getCandidatos().stream()
                .sorted(Comparator.comparingInt(Candidato::getVotos).reversed()
                        .thenComparing(Candidato::getDataNascimento,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparingInt(Candidato::getNumero))
                .limit(10)
                .toList();

        System.out.println();
        System.out.println("Candidatos mais votados (amostra para validar a leitura):");
        if (ranking.isEmpty()) {
            System.out.println("Nenhum candidato foi carregado para este município.");
            return;
        }

        int posicao = 1;
        for (Candidato candidato : ranking) {
            String federacao = candidato.getNumeroFederacao() != -1 ? "*" : "";
            System.out.printf("%d - %s%s (%s, %s votos)%n",
                    posicao++, federacao, candidato.getNomeUrna(),
                    candidato.getPartido().getSigla(), candidato.getVotos());
        }
    }

    private static void imprimirUso() {
        System.err.println("Uso: java -jar vereadores.jar <código_município> "
                + "<arquivo_candidatos.csv> <arquivo_votação.csv> <dd/MM/aaaa>");
        System.err.println("Exemplo: java -jar vereadores.jar 57053 "
                + "dados-teste/candidatos.csv dados-teste/votacao.csv 06/10/2024");
    }
}
