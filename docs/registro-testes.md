# Registro de testes para o relatório

## 28/10/2026 — desenho e primeira execução da suíte própria

Foram criados 24 cenários de integração executados pela linha de comando real da classe Main. As entradas são sintéticas, pequenas e incluídas no projeto, em `testes/proprios`. Não contêm dados pessoais reais. O objetivo é complementar os sete casos fornecidos pelo professor com situações cujos resultados possam ser conferidos manualmente e com entradas inválidas.

O executor `testes/executar.py` compila os fontes atuais, inicia um processo Java independente por cenário e registra a saída padrão, a saída de erros e o resultado. Isso evita que o estado do singleton de um cenário contamine outro. A matriz completa de objetivos está em `testes/README.md`; as verificações estão em `testes/proprios/casos.json`.

### Critérios de escolha

- Particionamento de entradas: votos nominais, votos de legenda, códigos 95–98, município/cargo de interesse e registros descartados.
- Valores de fronteira: aniversários no dia da eleição e no dia seguinte; idades 29/30, 39/40, 49/50 e 59/60; zero e um voto.
- Ordenação e desempates: candidatos com votação igual e idades diferentes; partidos com totais iguais; partidos cujos primeiros candidatos têm votação igual.
- Representação de texto: acentos Latin-1, espaços internos e nas extremidades, ponto e vírgula em campo entre aspas e aspas duplas escapadas.
- Robustez: arquivo vazio/inexistente, cabeçalho incompleto/repetido, aspas abertas, quantidade de campos errada, número/data inválidos, duplicidade, votos negativos e argumentos inválidos.
- Estrutura do CSV: colunas fora da ordem original, coluna extra e linhas vazias.

### Cálculo independente do cenário integrado

Data de referência: 06/10/2024. Município sintético: 1.

| Candidato | Partido | Nascimento | Situação | Votos |
|---|---|---|---|---:|
| ANA | PA (10), federação 7 | 01/01/1980 | Eleita, código 2 | 60 + 40 = 100 |
| BRUNO | PA (10) | 01/01/1990 | Não eleito | 90 |
| CARLA | PB (20) | 01/01/1970 | Eleita, código 3 | 80 |
| DAVI | PB (20) | 01/01/1960 | Não eleito | 90 |
| EVA | PC (30) | 01/01/1999 | Não eleita | 0 |

Há duas vagas. O ranking nominal é ANA, DAVI, BRUNO, CARLA e EVA. DAVI precede BRUNO por ser mais velho. Assim, DAVI seria eleito por votação majoritária (posição 2), enquanto CARLA é beneficiada pelo sistema proporcional (posição 4). Apenas ANA recebe a marca de federação nos relatórios 2–5.

PA recebe 190 votos nominais e 10 de legenda; PB recebe 170 nominais e 30 de legenda. Ambos totalizam 200, portanto PA aparece antes por ter o menor número partidário. PC aparece com zero na votação dos partidos e não aparece no relatório de primeiro/último. A soma válida é 400 = 360 nominais (90,00%) + 40 de legenda (10,00%). As quatro linhas de votos 95–98, cada uma com 40 votos, não entram nessa soma.

As eleitas têm 44 e 54 anos: uma na faixa 40–49 e outra na faixa 50–59, cada uma com 50,00%. Ambas são do gênero feminino (100,00%). A saída completa foi escrita a partir desses cálculos, antes da execução, em `testes/proprios/01_integrado/esperado.txt`; não foi gerada pelo programa testado.

### Resultado observado

A rodada inicial de 23 cenários passou integralmente. Em seguida foi adicionado o cenário 24, que usa as mesmas pessoas do teste de fronteiras, mas muda a data pelo argumento para 05/10/2024. Ele verifica que a idade é calculada com a data recebida, em vez de uma data fixa. A rodada consolidada passou em **24 de 24 cenários**, sem falhas. O resultado consolidado e a identificação exata do Java constam em `testes/resultados/resumo.json`.

A compilação é feita com `--release 17 -encoding UTF-8`. O ambiente disponível executa Microsoft OpenJDK 21. Isso valida o uso das APIs de Java 17 na compilação, mas ainda não substitui uma execução no JDK 17 de referência.

Nenhuma alteração no código de produção foi necessária nesta etapa de testes.

### Verificações anteriores, antes desta suíte

Na etapa anterior, os sete cenários fornecidos pelo professor (AC1392, AC1473, ES56472, ES57053, PE25810, PE26298 e PE26310) passaram com saída idêntica byte por byte. Esses dados ficam fora deste repositório, em `/home/arthur/Downloads/script_testes/testes`. Essa execução anterior é complementar e não faz parte dos 24 testes próprios.

### Limites da evidência

A suíte não prova ausência de defeitos. Apenas o cenário integrado compara todos os bytes da saída; os demais verificam resultados específicos. Não foram feitos testes de desempenho com arquivos de todos os estados, testes de mutação nem execução no Oracle JDK 17. Não há cenários para toda forma possível de CSV malformado, datas impossíveis que um formatador permissivo possa normalizar, códigos desconhecidos de gênero ou empate simultâneo de votos e data de nascimento. Essas condições não devem ser descritas no relatório como validadas.

Este registro é material de apoio para a seção de testes. A descrição da modelagem, o diagrama UML e a seção de bugs conhecidos ainda precisam ser elaborados para o relatório final.

## 28/10/2026 — integração com avaliar.sh

O script de avaliação passou a executar a suíte própria após os cenários do professor. Preserva as verificações específicas dos nossos casos: comparação completa no integrado, trechos esperados nos demais válidos e exceções previstas nos inválidos. Uma exceção esperada é aprovação do teste, não falha da avaliação.

O resumo mostra resultados do professor, próprios e total. O retorno é 0 somente quando as duas etapas passam, 1 quando há falhas de casos e 2 quando ocorre erro de preparação. As saídas próprias ficam dentro da pasta exclusiva de cada avaliação, em `proprios`, acompanhadas por `proprios.log` e pelo resumo consolidado.

Validação da integração: `bash avaliar.sh`, com JAVA_HOME apontando para Microsoft OpenJDK 21, terminou com retorno 0: **7 testes do professor + 24 próprios = 31 aprovados, nenhuma falha**. Evidências em `/home/arthur/Downloads/script_testes/resultados-avaliacao-20261028-203502-yRGyY6/resumo.txt` e na subpasta `proprios` dessa avaliação.


## Validação com Oracle JDK 17 — 28/10/2026

As execuções históricas acima foram complementadas pela avaliação no Oracle JDK 17.0.12, obtido do arquivo oficial https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html e conferido pelo SHA-256 publicado pela Oracle. A instalação local fica em `/home/arthur/Downloads/script_testes/ferramentas/oracle-jdk-17`. O Temurin instalado anteriormente foi removido.

`./avaliar.sh` terminou com retorno 0: **7 casos do professor e 24 próprios aprovados, nenhuma falha**. Compilação e execução usaram a mesma instalação Oracle. Evidências em `/home/arthur/Downloads/script_testes/resultados-avaliacao-20261028-210624-43HsCq`, incluindo `ambiente.log`, `compilacao.log` e `resumo.txt`. A limitação histórica de ausência de execução no Oracle JDK 17 foi resolvida por esta avaliação. Os scripts agora verificam também o fornecedor Oracle, além da versão 17.
