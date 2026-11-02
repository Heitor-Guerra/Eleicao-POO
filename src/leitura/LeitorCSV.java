package leitura;

import leitura.exceptions.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

public abstract class LeitorCSV {
    private static final Charset CODIFICACAO_TSE = StandardCharsets.ISO_8859_1;
    private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter
            .ofPattern("dd/MM/uuuu", LOCALE)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final char SEPARADOR = ';';

    private final String caminhoArquivo;
    private final Map<String, Integer> headersIndices;
    private final List<String> colunasObrigatorias;

    protected LeitorCSV(String caminhoArquivo, String... colunasObrigatorias) {
        this.headersIndices = new HashMap<>();
        this.caminhoArquivo = caminhoArquivo;
        this.colunasObrigatorias = List.of(colunasObrigatorias);
    }

    /**
     * Processa uma linha já separada em campos.
     */
    protected abstract void processarLinha(List<String> campos, int numeroLinha)  throws LinhaInvalidaException;

    public final void le() throws ErroLeituraArquivoException, ArquivoVazioException,
            LinhaTamanhoInvalidoException, AspasNaoFechadasException,
            CabecalhoRepetidoException, ColunaAusenteException, LinhaInvalidaException {
        try (BufferedReader leitor = new BufferedReader(new FileReader(caminhoArquivo, CODIFICACAO_TSE))) {
            String linha = leitor.readLine();
            if (linha == null || linha.isBlank()) {
                throw new ArquivoVazioException(caminhoArquivo);
            }

            List<String> camposCabecalho = parseLinha(linha, 1);
            int quantidadeColunasCabecalho = camposCabecalho.size();
            headersIndices.clear();
            mapearCabecalho(camposCabecalho);
            for (String coluna : colunasObrigatorias) {
                if (!headersIndices.containsKey(coluna)) {
                    throw new ColunaAusenteException(coluna);
                }
            }

            int numeroLinha = 1;
            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) {
                    continue;
                }

                List<String> campos = parseLinha(linha, numeroLinha);
                if (campos.size() != quantidadeColunasCabecalho) {
                    throw new LinhaTamanhoInvalidoException(numeroLinha, quantidadeColunasCabecalho, campos.size());
                }
                processarLinha(campos, numeroLinha);
            }
        } catch (IOException e) {
            throw new ErroLeituraArquivoException(caminhoArquivo, e);
        }
    }

    private void mapearCabecalho(List<String> cabecalho) throws CabecalhoRepetidoException {
        for (int i = 0; i < cabecalho.size(); i++) {
            String nome = cabecalho.get(i).trim();

            if (this.headersIndices.putIfAbsent(nome, i) != null) {
                throw new CabecalhoRepetidoException(nome);
            }
        }
    }

    protected final List<String> parseLinha(String linha, int numeroLinha)
            throws AspasNaoFechadasException {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char caractere = linha.charAt(i);
            if (caractere == '"') {
                if (dentroAspas && i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                    campo.append('"');
                    i++;
                } else {
                    dentroAspas = !dentroAspas;
                }
            } else if (caractere == SEPARADOR && !dentroAspas) {
                campos.add(campo.toString());
                campo.setLength(0);
            } else {
                campo.append(caractere);
            }
        }

        if (dentroAspas) {
            throw new AspasNaoFechadasException(numeroLinha);
        }
        campos.add(campo.toString());
        return campos;
    }

    protected final String getString(List<String> campos, String coluna) {
        Integer indice = this.headersIndices.get(coluna);
        if (indice == null) {
            throw new ColunaAusenteException(coluna);
        }
        return campos.get(indice);
    }

    protected final int getInteiro(List<String> campos, String coluna, int numeroLinha) {
        try {
            String valor = getString(campos, coluna).trim();
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            throw new ErroParsingException(numeroLinha, coluna);
        }
    }

    protected final LocalDate getData(List<String> campos, String coluna, int numeroLinha) {
        try {
            String valor = getString(campos, coluna).trim();
            return LocalDate.parse(valor, FORMATO_DATA);
        } catch (DateTimeParseException ex) {
            throw new ErroParsingException(numeroLinha, coluna);
        }
    }
}
