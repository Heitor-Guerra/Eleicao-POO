# Eleicao-POO

## Compilação e execução

Requer JDK 17 e Apache Ant. Configure `JAVA_HOME` para a pasta do JDK e inclua seu diretório `bin` no `PATH`.

```bash
ant clean compile
java -cp bin Main <codigoMunicipio> <candidatos.csv> <votacao.csv> <dd/MM/aaaa>
```

## Validação

Os 32 testes próprios estão incluídos no repositório. Requerem Python 3 e Oracle JDK 17, sem bibliotecas Python adicionais:

```bash
python3 testes/executar.py
```

Para executar também os sete cenários externos do professor, configure `PASTA_SCRIPT_TESTES` para a pasta do pacote de testes e execute:

```bash
PASTA_SCRIPT_TESTES=/caminho/script_testes bash avaliar.sh
```

O pacote externo deve conter a subpasta `testes` com os cenários. Ant deve estar no `PATH` ou em `ferramentas/ant/bin/ant` dentro desse pacote. A avaliação usa Oracle JDK 17 conforme `JAVA_HOME` ou procura uma instalação local, inclusive em `ferramentas/oracle-jdk-17` no pacote.

Detalhes dos cenários e resultados esperados em [testes/README.md](testes/README.md).
