package eleicao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Eleicao {
    private final Map<Integer, Partido> partidos;


    public Eleicao() {
        this.partidos = new HashMap<>();
    }

    public Partido registrarPartido(int numero, String sigla) {
        Partido partido = new Partido(numero, sigla);
        this.partidos.put(numero, partido);
        return partido;
    }

    public List<Partido> getPartidos() {
        return new ArrayList<>(this.partidos.values());
    }

    public Partido getPartidoByNumero(int numero) {
        return this.partidos.get(numero);
    }

}
