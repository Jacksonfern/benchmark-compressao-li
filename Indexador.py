# from os.path import isfile, join
import os

class Indexador:
    def __init__(self, folder, dest):
        self.folder = folder
        self.dest = dest
        self.files = os.listdir(folder)
        self.files = sorted(self.files, key=lambda f: int(f[2:]))
        self.invIndex = {}
        self.size = 0

    def indexFile(self, file, docId):
        for line in file.readlines():
            terms = line.split(" ")
            for term in terms:
                term=term.lower()
                if term in self.invIndex:
                    if self.invIndex[term][-1] != docId:
                        self.invIndex[term].append(docId)
                else:
                    self.invIndex[term] = [docId]

    def writeIndex(self):
        docId = 0
        for i in self.files:
            file = open(os.path.join(self.folder, i), 'r')
            self.indexFile(file, docId)
            docId+=1
        if not os.path.exists('indexed'):
            os.makedirs('indexed')
        termsFile = open(os.path.join(self.dest, 'file.terms'), 'w+')
        postingsFile = open(os.path.join(self.dest, 'file.postings'), 'w+')
        for i in self.invIndex:
            termsFile.write(i+"\n")
            for docId in self.invIndex[i]:
                postingsFile.write(str(docId)+" ")
            postingsFile.write("\n")
            self.size+=len(i)+4*len(self.invIndex[i]) #tamanho do indice (termo + n*sizeof(int))

    def print(self):
        for i in self.invIndex:
            print(i, self.invIndex[i])

    def getSize(self):
        return self.size/1000000

index = Indexador("collections/basic_collection", "indexed")
index.writeIndex()
print("Tamanho do arquivo invertido:", index.getSize(), "MB")