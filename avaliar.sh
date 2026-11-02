#!/usr/bin/env bash
# Uso: ./avaliar.sh [pasta que contém os cenários do professor]
# Executa também os cenários próprios definidos em testes/proprios/casos.json.
# Retorno: 0 = todos passaram; 1 = diferenças/erro de execução; 2 = erro de preparação.
set -u
set -o pipefail

PROJETO=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
PACOTE="${PASTA_SCRIPT_TESTES:-/home/arthur/Downloads/script_testes}"
TESTES="${1:-$PACOTE/testes}"
if [[ ! -d "$TESTES" ]]; then
    echo "Pasta de testes não encontrada: $TESTES" >&2
    exit 2
fi
TESTES=$(cd -- "$TESTES" && pwd)

# JAVA_HOME explícito tem prioridade e nunca é substituído silenciosamente.
validar_jdk17() {
    local pasta="$1" versao_java versao_javac
    [[ -f "$pasta/release" ]] && grep -qx 'IMPLEMENTOR="Oracle Corporation"' "$pasta/release" || return 1
    [[ -x "$pasta/bin/java" && -x "$pasta/bin/javac" ]] || return 1
    versao_java=$("$pasta/bin/java" -version 2>&1) || return 1
    versao_javac=$("$pasta/bin/javac" -version 2>&1) || return 1
    [[ "$versao_java" =~ version\ \"17(\.|\") && "$versao_javac" =~ ^javac\ 17(\.|$) ]]
}
if [[ -n "${JAVA_HOME:-}" ]]; then
    if ! validar_jdk17 "$JAVA_HOME"; then
        echo "JAVA_HOME deve apontar para um Oracle JDK 17 completo: $JAVA_HOME" >&2
        exit 2
    fi
else
    CANDIDATOS=("$PACOTE/ferramentas/oracle-jdk-17")
    if command -v javac >/dev/null; then
        CANDIDATOS+=("$(dirname -- "$(dirname -- "$(readlink -f -- "$(command -v javac)")")")")
    fi
    CANDIDATOS+=(/usr/lib/jvm/* /opt/java/* "$HOME"/.jdks/*)
    for CANDIDATO in "${CANDIDATOS[@]}"; do
        if validar_jdk17 "$CANDIDATO"; then
            JAVA_HOME="$CANDIDATO"
            break
        fi
    done
    if [[ -z "${JAVA_HOME:-}" ]]; then
        echo "Oracle JDK 17 não encontrado. Instale-o e configure JAVA_HOME para sua pasta." >&2
        exit 2
    fi
fi
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"
JAVA="$JAVA_HOME/bin/java"
if command -v ant >/dev/null; then
    ANT=$(command -v ant)
elif [[ -x "$PACOTE/ferramentas/ant/bin/ant" ]]; then
    ANT="$PACOTE/ferramentas/ant/bin/ant"
else
    echo "Ant não encontrado. Instale-o ou configure PASTA_SCRIPT_TESTES." >&2
    exit 2
fi

if ! command -v python3 >/dev/null; then
    echo "Python 3 não encontrado; ele é necessário para os testes próprios." >&2
    exit 2
fi

RESULTADOS=$(mktemp -d "$PACOTE/resultados-avaliacao-$(date +%Y%m%d-%H%M%S)-XXXXXX") || exit 2
TEMPORARIO=$(mktemp -d) || exit 2
trap 'rm -rf -- "$TEMPORARIO"' EXIT
mkdir -p "$TEMPORARIO/src"
cp -R -- "$PROJETO/src/." "$TEMPORARIO/src/" || exit 2
cp -- "$PROJETO/build.xml" "$TEMPORARIO/build.xml" || exit 2
{
    printf 'JAVA_HOME=%s\n' "$JAVA_HOME"
    "$JAVA" -version
    "$JAVA_HOME/bin/javac" -version
} > "$RESULTADOS/ambiente.log" 2>&1
printf 'Oracle JDK 17: %s\n' "$JAVA_HOME"
printf 'Resultados: %s\nCompilando a versão atual...\n' "$RESULTADOS"
if ! (cd "$TEMPORARIO" && "$ANT" compile) > "$RESULTADOS/compilacao.log" 2>&1; then
    echo "ERRO de compilação. Consulte $RESULTADOS/compilacao.log"
    exit 2
fi

PASSARAM=0
FALHARAM=0
shopt -s nullglob
CENARIOS=("$TESTES"/*/)
if (( ${#CENARIOS[@]} == 0 )); then
    echo "Nenhum cenário encontrado." >&2
    exit 2
fi
for CENARIO in "${CENARIOS[@]}"; do
    NOME=$(basename -- "$CENARIO")
    DESTINO="$RESULTADOS/$NOME"
    mkdir -p "$DESTINO"
    if [[ ! -f "$CENARIO/in/cdMunicipio.txt" || ! -f "$CENARIO/in/candidatos.csv" || ! -f "$CENARIO/in/votacao.csv" || ! -f "$CENARIO/out/output.txt" ]]; then
        echo "[ERRO] $NOME: cenário incompleto."
        FALHARAM=$((FALHARAM + 1))
        continue
    fi
    MUNICIPIO=$(tr -d '[:space:]' < "$CENARIO/in/cdMunicipio.txt")
    # Execução direta permite detectar exceções que o alvo run do Ant pode ocultar.
    if ! "$JAVA" -cp "$TEMPORARIO/bin" Main "$MUNICIPIO" \
        "$CENARIO/in/candidatos.csv" "$CENARIO/in/votacao.csv" '06/10/2024' \
        > "$DESTINO/obtido.txt" 2> "$DESTINO/erros.log"; then
        echo "[ERRO] $NOME: falha na execução; consulte $DESTINO/erros.log"
        FALHARAM=$((FALHARAM + 1))
        continue
    fi
    cp -- "$CENARIO/out/output.txt" "$DESTINO/esperado.txt"
    # Mesmas tolerâncias do professor: ignora espaços, linhas vazias e CRLF.
    diff -u --strip-trailing-cr --ignore-all-space --ignore-blank-lines \
        "$DESTINO/esperado.txt" "$DESTINO/obtido.txt" > "$DESTINO/diferencas.diff"
    STATUS=$?
    if (( STATUS == 0 )); then
        echo "[OK] $NOME"
        PASSARAM=$((PASSARAM + 1))
    else
        echo "[FALHOU] $NOME: consulte $DESTINO/diferencas.diff"
        cat -- "$DESTINO/diferencas.diff"
        FALHARAM=$((FALHARAM + 1))
    fi
done
PROFESSOR_PASSARAM=$PASSARAM
PROFESSOR_FALHARAM=$FALHARAM
printf '\nExecutando os testes próprios...\n'
RESULTADOS_TESTES_PROPRIOS="$RESULTADOS/proprios" python3 "$PROJETO/testes/executar.py" \
    2>&1 | tee "$RESULTADOS/proprios.log"
STATUS_PROPRIOS=$?
if (( STATUS_PROPRIOS > 1 )) || [[ ! -f "$RESULTADOS/proprios/resumo.json" ]]; then
    printf 'Erro na preparação dos testes próprios. Consulte %s/proprios.log\n' "$RESULTADOS" \
        | tee "$RESULTADOS/resumo.txt"
    exit 2
fi
CONTAGENS=$(python3 - "$RESULTADOS/proprios/resumo.json" <<'PYTHON'
import json
import sys
with open(sys.argv[1], encoding='utf-8') as arquivo:
    resumo = json.load(arquivo)
print(resumo['aprovados'], resumo['total'] - resumo['aprovados'])
PYTHON
) || exit 2
read -r PROPRIOS_PASSARAM PROPRIOS_FALHARAM <<< "$CONTAGENS"
PASSARAM=$((PROFESSOR_PASSARAM + PROPRIOS_PASSARAM))
FALHARAM=$((PROFESSOR_FALHARAM + PROPRIOS_FALHARAM))
printf '\nProfessor: %d passaram; %d falharam.\nPróprios: %d passaram; %d falharam.\nTotal: %d passaram; %d falharam.\nResultados: %s\n' \
    "$PROFESSOR_PASSARAM" "$PROFESSOR_FALHARAM" \
    "$PROPRIOS_PASSARAM" "$PROPRIOS_FALHARAM" \
    "$PASSARAM" "$FALHARAM" "$RESULTADOS" | tee "$RESULTADOS/resumo.txt"
(( FALHARAM == 0 && STATUS_PROPRIOS == 0 ))
