benchmark-compressao-li
=======================

Projeto final da disciplina de Recuperação de Informação (Férias-2020). Neste projeto, implementa-se um benchmark bem simples para testar os algoritmos de compressão PForDelta, FastPFor e Milc. Caso deseje replicar este experimento, siga os passos a seguir:

obs: Não é recomendado adotar este experimento como base para testar seus próprios métodos para fins de produção de artigo. O ideal seria usar o lucene para indexação e, para testar seu método de compressão, modifique o codec do lucene e insira seu algoritmo.

Indexador
---------

Primeiramente, execute o indexador construído em python, usando o seguinte comando:

	python3 Indexador.py ./diretorio_da_colecao
	
O script criará dois arquivos na pasta "indexed": um arquivo de termos e um de listas. Não mexa neles!

Gerador de queries
------------------

Caso deseje gerar queries aleatórias, execute o seguinte comando:

 	python3 queryGenerator.py numero_de_queries
	
As queries serão geradas na pasta collections (certifique-se de que esta pasta existe)!

Executar experimento
-------------------------
Antes de executar os experimentos, certifique-se de que o Apache Maven está devidamente instalado e configurado (com a variável de ambiente JAVA_HOME apontando para o jdk corretamente). Após isso, execute o seguinte comando:

	mvn compile
	
Se compilou sem erros, você pode escolher duas opções:

1- Experimentar o algoritmo em cima de listas geradas aleatoriamente (modifique o código para mudar o intervalo dos valores). Para isso, execute o seguinte comando:

	mvn exec:java -Dexec.mainClass="com.jackson.app.App"
	
2- Experimentar com o arquivo invertido. Para isso execute o seguinte comando:

	mvn exec:java -Dexec.mainClass="com.jackson.app.Benchmark"
