import eleicao.Candidato;
import eleicao.Eleicao;
import leitura.LeitorCandidatos;
import leitura.LeitorEleicao;
import relatorios.Relatorio;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Ponto de entrada para testar a leitura dos CSVs de candidatos e votação. */
public final class Main {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter
            .ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"));

    private Main() { }

    public static void main(String[] args) {
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
        Eleicao.INSTANCIA.inicializar(codigoMunicipio, dataEleicao);

        LeitorCandidatos l1 = new LeitorCandidatos(args[1]);
        LeitorEleicao l2 = new LeitorEleicao(args[2]);
        l1.le();
        l2.le();

        Relatorio.printRelatorio();
    }
}
