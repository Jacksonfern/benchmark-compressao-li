package com.jackson.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Indexer{
    private File folder;
    private Map<String, Set<Integer>> invFile;

    public Indexer(String dirSource){
        folder = new File(dirSource);
        invFile = new HashMap<String, Set<Integer>>();
    }

    public void IndexFiles(){
        File[] files = folder.listFiles();
        int docId=0;
        if(files==null)
            return;
        System.out.println("Achou " + files.length);
        for(final File f : files){
            if(f.isFile())
                Index(f, docId);
            docId++;
        }
    }

    public void print(){
        for(Map.Entry<String, Set<Integer>> entry : invFile.entrySet()){
            System.out.print(entry.getKey().toLowerCase() + " : [");
            Iterator<Integer> itr = entry.getValue().iterator();
            while(itr.hasNext())
                System.out.print(itr.next() + ",");
            System.out.println();
        }
    }

    private void Index(File f, int docId){
        try{
            Scanner reader = new Scanner(f);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                for(final String word : line.split(" ")){
                    Set<Integer> aux = invFile.get(word);
                    if(aux==null)
                        aux = new HashSet<Integer>();
                    aux.add(docId);
                    invFile.put(word, aux);
                }
                return;
            }
            reader.close();
        } catch(FileNotFoundException e){
            System.out.println("Error!");
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        String folderSource = "src/collections/basic_collection/";
        Indexer indexWrite = new Indexer(folderSource);
        indexWrite.IndexFiles();
        indexWrite.print();
    }
}