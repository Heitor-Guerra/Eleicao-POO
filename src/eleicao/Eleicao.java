package eleicao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Eleicao {
    private final Map<Integer, Partido> partidos;


    public Eleicao() {
        this.partidos = new HashMap<>()
    }

    public Partido registrarPartido(int numero, String sigla) {
        Partido partido = new Candidato(numero, sigla);
        this.partidos.put(numero, candidato);
        return partido;
    }

    public List<Partido> getPartidos() {
        return new ArrayList<>(this.candidatos.values());
    }

    public Partido getPartidoByNumero(int numero) {
        return this.partidos.get(numero)
    }

}
