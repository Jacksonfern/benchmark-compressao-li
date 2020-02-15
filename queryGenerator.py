import sys
from random import randint

# Este script gera pseudo-queries a partir do arquivo .terms
# Para usa-lo: python3 queryGenerator.py x
# onde x eh o numero de queries desejado

try:
    termFile = open("indexed/gov.terms", "r")
    queryFile = open("collections/queries.query", "w+")
    terms = termFile.readlines()
    if len(sys.argv)>1 and sys.argv[1].isnumeric():
        numQueries = int(sys.argv[1])
        for i in range(numQueries):
            queryLen = randint(1, 5)
            while queryLen>0:
                queryFile.write(terms[randint(0, len(terms))][:-1] + " ")
                queryLen-=1
            queryFile.write("\n")
    else:
        print("Parametro inexistente ou invalido!")

except IOError:
    print("Erro ao ler arquivo")