#!/usr/bin/env python3
"""Testes próprios, sem bibliotecas externas. Requer Python 3 e Oracle JDK 17."""
import difflib
import json
import os
import re
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
from datetime import datetime

PROJETO = Path(__file__).resolve().parents[1]
CASOS = PROJETO / 'testes' / 'proprios'
RESULTADOS = Path(os.environ.get('RESULTADOS_TESTES_PROPRIOS', PROJETO / 'testes' / 'resultados'))


def ferramenta(nome):
    java_home = os.environ.get('JAVA_HOME')
    caminho = Path(java_home) / 'bin' / nome if java_home else None
    if caminho:
        if not caminho.is_file() or not os.access(caminho, os.X_OK):
            raise RuntimeError(f'Ferramenta ausente no JAVA_HOME: {caminho}')
        return str(caminho)
    encontrado = shutil.which(nome)
    if not encontrado:
        raise RuntimeError('Configure JAVA_HOME para um Oracle JDK 17.')
    return encontrado


def executar():
    java, javac = ferramenta('java'), ferramenta('javac')
    release = Path(java).resolve().parent.parent / 'release'
    if (not release.is_file()
            or 'IMPLEMENTOR="Oracle Corporation"' not in release.read_text().splitlines()):
        raise RuntimeError('A avaliação exige o Oracle JDK 17. Configure JAVA_HOME.')
    versao = subprocess.run([java, '-version'], capture_output=True, text=True, check=True)
    versao_javac = subprocess.run([javac, '-version'], capture_output=True, text=True, check=True)
    if (not re.search(r'version "17(?:\.|")', versao.stdout + versao.stderr)
            or not re.match(r'javac 17(?:\.|\s|$)', versao_javac.stdout + versao_javac.stderr)):
        raise RuntimeError('A avaliação exige java e javac do Oracle JDK 17. Configure JAVA_HOME.')
    if Path(java).resolve().parent != Path(javac).resolve().parent:
        raise RuntimeError('java e javac devem pertencer ao mesmo Oracle JDK 17. Configure JAVA_HOME.')
    RESULTADOS.mkdir(parents=True, exist_ok=True)
    registros = []
    with tempfile.TemporaryDirectory(prefix='eleicao-testes-') as pasta:
        classes = Path(pasta) / 'classes'
        classes.mkdir()
        compilacao = subprocess.run(
            [javac, '--release', '17', '-encoding', 'UTF-8', '-d', str(classes)]
            + [str(p) for p in sorted((PROJETO / 'src').rglob('*.java'))],
            capture_output=True, text=True)
        (RESULTADOS / 'compilacao.log').write_text(compilacao.stdout + compilacao.stderr)
        if compilacao.returncode:
            print('Falha na compilação; consulte testes/resultados/compilacao.log')
            return 2
        for caso in json.loads((CASOS / 'casos.json').read_text()):
            pasta_caso = CASOS / caso['id']
            destino = RESULTADOS / caso['id']
            destino.mkdir(exist_ok=True)
            argumentos = caso['argumentos']
            if argumentos is None:
                argumentos = ['1', '{caso}/candidatos.csv', '{caso}/votacao.csv', '06/10/2024']
            argumentos = [a.replace('{caso}', str(pasta_caso)) for a in argumentos]
            falhas = []
            try:
                processo = subprocess.run([java, '-cp', str(classes), 'Main'] + argumentos,
                                          capture_output=True, timeout=30)
                saida = processo.stdout.decode('utf-8')
                erros = processo.stderr.decode('utf-8')
                (destino / 'obtido.txt').write_bytes(processo.stdout)
                (destino / 'erros.log').write_bytes(processo.stderr)
                if caso['erro']:
                    if processo.returncode == 0:
                        falhas.append('Entrada inválida terminou com sucesso.')
                    for trecho in caso['erro']:
                        if trecho not in erros:
                            falhas.append('Erro esperado ausente: ' + repr(trecho))
                    if saida:
                        falhas.append('Entrada inválida produziu relatório parcial.')
                else:
                    if processo.returncode != 0:
                        falhas.append('Execução falhou: ' + erros)
                    if erros:
                        falhas.append('Execução válida escreveu na saída de erros.')
                    for trecho in caso['contem']:
                        if trecho not in saida:
                            falhas.append('Trecho esperado ausente: ' + repr(trecho))
                    for trecho in caso['nao_contem']:
                        if trecho in saida:
                            falhas.append('Trecho proibido encontrado: ' + repr(trecho))
                    if 'saida_exata' in caso:
                        esperado = (pasta_caso / caso['saida_exata']).read_bytes()
                        if processo.stdout != esperado:
                            falhas.append('A saída completa difere do resultado calculado manualmente.')
                            (destino / 'diferencas.diff').write_text(''.join(difflib.unified_diff(
                                esperado.decode().splitlines(True), saida.splitlines(True),
                                fromfile='esperado', tofile='obtido')))
            except subprocess.TimeoutExpired:
                falhas.append('Limite de 30 segundos excedido.')
            registro = {'id': caso['id'], 'objetivo': caso['objetivo'],
                        'passou': not falhas, 'falhas': falhas}
            registros.append(registro)
            print(('OK     ' if not falhas else 'FALHOU ') + caso['id'])
            for falha in falhas:
                print('  ' + falha)
    resumo = {'executado_em': datetime.now().astimezone().isoformat(),
              'java': (versao.stdout + versao.stderr).strip(),
              'compilacao': '--release 17 -encoding UTF-8',
              'total': len(registros), 'aprovados': sum(r['passou'] for r in registros),
              'casos': registros}
    (RESULTADOS / 'resumo.json').write_text(json.dumps(resumo, ensure_ascii=False, indent=2) + '\n')
    print(f"\n{resumo['aprovados']}/{resumo['total']} casos aprovados. Registros em {RESULTADOS}")
    return 0 if resumo['aprovados'] == resumo['total'] else 1


if __name__ == '__main__':
    try:
        sys.exit(executar())
    except (RuntimeError, OSError) as erro:
        print(str(erro), file=sys.stderr)
        sys.exit(2)
