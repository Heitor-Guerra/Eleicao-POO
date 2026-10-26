package eleicao;

import eleicao.exceptions.EleicaoJaExistenteException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/** Singleton de ELeicao */
public enum Eleicao {
    INSTANCIA;

    private final Map<Integer, EleicaoMunicipio> eleicoesPorMunicipio = new HashMap<>();

    public EleicaoMunicipio registrarEleicao(int codigoMunicipio, LocalDate dataEleicao)
            throws EleicaoJaExistenteException {
        if (eleicoesPorMunicipio.containsKey(codigoMunicipio) &&
            eleicoesPorMunicipio.get(codigoMunicipio).getDataEleicao().isEqual(dataEleicao)) {
            throw new EleicaoJaExistenteException(codigoMunicipio, dataEleicao);
        }
        EleicaoMunicipio nova = new EleicaoMunicipio(dataEleicao, codigoMunicipio);
        eleicoesPorMunicipio.put(codigoMunicipio, nova);
        return nova;
    }

    /** Pode retornar nulo se o código não estiver registrado. */
    public EleicaoMunicipio getEleicaoByCodigoMunicipio(int codigoMunicipio) {
        return eleicoesPorMunicipio.get(codigoMunicipio);
    }

    public Map<Integer, EleicaoMunicipio> getEleicoesPorMunicipio() {
        return new HashMap<>(eleicoesPorMunicipio);
    }
}
