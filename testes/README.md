# Testes próprios

## Execução

Requisitos: Python 3 e Oracle JDK 17. Não exige bibliotecas Python externas, Ant ou JUnit.

```bash
python3 testes/executar.py
```

Se Java não estiver no PATH, configure JAVA_HOME para a instalação do JDK. Execute a partir da raiz do projeto. O executor também funciona a partir de outra pasta quando chamado pelo caminho completo.

O código de saída é 0 quando todos passam, 1 quando um caso falha e 2 quando há erro de preparação/compilação. A compilação usa `--release 17 -encoding UTF-8` em uma pasta temporária removida ao terminar. Não altera os fontes nem depende de classes previamente compiladas.

## Avaliação conjunta com os casos do professor

Na raiz do projeto, execute:

```bash
bash avaliar.sh
```

Esse comando executa os casos do professor e os 32 próprios, apresentando contagens separadas e um total consolidado. O argumento opcional continua sendo a pasta dos cenários do professor. Requer também Ant, além de Python 3 e Oracle JDK 17. O avaliador verifica o fornecedor Oracle e exige a versão 17 tanto para compilar quanto para executar, usa a mesma instalação nos dois passos e registra as versões em `ambiente.log`. Um `JAVA_HOME` inválido gera erro de preparação; sem essa variável, procura primeiro em `$PASTA_SCRIPT_TESTES/ferramentas/oracle-jdk-17` (por padrão, `/home/arthur/Downloads/script_testes/ferramentas/oracle-jdk-17`), depois o JDK do PATH e instalações em `/usr/lib/jvm`, `/opt/java` e `~/.jdks`.

Cada avaliação mantém os resultados próprios na subpasta `proprios` da pasta de resultados anunciada pelo script, junto com `proprios.log` e o `resumo.txt` consolidado. Assim, uma avaliação não sobrescreve a evidência de outra. O executor próprio continua podendo ser usado sozinho. A variável opcional `RESULTADOS_TESTES_PROPRIOS` permite escolher seu diretório de resultados.

## Organização

- `proprios/casos.json`: objetivos, argumentos e verificações de cada cenário.
- `proprios/<cenário>/candidatos.csv` e `votacao.csv`: entradas pequenas em ISO-8859-1, com separador ponto e vírgula e campos entre aspas.
- `proprios/01_integrado/esperado.txt`: saída completa em UTF-8, calculada manualmente e mantida independente da aplicação.
- `resultados/resumo.json`: ambiente, horário, resultado e falhas de cada caso da última execução.
- `resultados/<cenário>/obtido.txt` e `erros.log`: saídas capturadas; `diferencas.diff` é criado se a comparação completa falhar.
- `../docs/registro-testes.md`: metodologia, cálculos e histórico para o relatório.

A pasta `resultados` representa a última execução; arquive-a antes de executar novamente se quiser preservar uma execução anterior. Arquivos de diferença de execuções antigas podem permanecer: consulte sempre o resumo atual.

Nos casos válidos, o processo precisa terminar com sucesso e sem mensagens na saída de erros. No caso integrado, a comparação é byte por byte, incluindo espaços e linhas vazias. Nos outros, são verificados trechos calculados previamente. Nos casos inválidos, exige-se término com erro, exceção e contexto esperados, sem relatório parcial na saída padrão. Cada processo tem limite de 30 segundos.

## Matriz de cenários

| Caso | Objetivo |
|---|---|
| 01_integrado | Conferir os dez relatórios, federação, desempate por idade e por número partidário, e eleitos beneficiados/prejudicados. |
| 02_filtros | Ignorar outro município, outro cargo, candidatura inválida e votos sem candidato válido. |
| 03_texto_csv | Preservar Latin-1, espaços, ponto e vírgula e aspas escapadas dentro de um nome. |
| 04_limites_idade | Testar aniversário no dia e no dia seguinte e limites 30, 40, 50 e 60 anos. |
| 05_sem_votos | Zero votos e nenhum eleito não podem causar divisão por zero. |
| 06_somente_legenda | Contabilizar legenda de partido cadastrado por candidatura inválida, sem listar extremos sem votos. |
| 07_empate_extremos | Desempatar partidos pelo número quando seus candidatos mais votados empatam. |
| 08_situacao_um | O código 1 não deve criar uma vaga de vereador. |
| 09_vazio | Arquivo de candidatos vazio deve gerar exceção específica. |
| 10_coluna_ausente | Validar cabeçalho incompleto mesmo sem linhas de dados. |
| 11_cabecalho_repetido | Rejeitar nomes de coluna repetidos. |
| 12_aspas_abertas | Detectar campo com aspas não fechadas e informar a linha. |
| 13_numero_campos | Rejeitar linha com quantidade de campos diferente do cabeçalho. |
| 14_numero_invalido | Informar coluna e linha de inteiro inválido. |
| 15_data_invalida | Informar coluna e linha de data impossível. |
| 16_duplicado | Rejeitar candidato duplicado com linha e causa original. |
| 17_votos_negativos | Rejeitar quantidade negativa no CSV de votação. |
| 18_coluna_votacao | Validar colunas obrigatórias do arquivo de votos. |
| 19_arquivo_inexistente | Informar erro de abertura preservando a causa. |
| 20_argumentos | Rejeitar linha de comando sem os quatro argumentos. |
| 21_municipio_invalido | Rejeitar município não positivo. |
| 22_colunas_reordenadas | Localizar colunas pelo nome, aceitando ordem diferente e colunas extras. |
| 23_linhas_vazias | Ignorar linhas vazias entre registros. |
| 24_data_parametro | Usar a data recebida por argumento: um dia antes do aniversário os limites etários mudam. |

Os oito cenários adicionais (25 a 28, para parâmetro e nascimento) verificam a rejeição de 31/02/2024, 31/04/2024 e 29/02/2023, além da aceitação de 29/02/2024. As entradas inválidas devem falhar sem gerar relatório; os erros de nascimento devem identificar coluna e linha.
