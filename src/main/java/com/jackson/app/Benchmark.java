package com.jackson.app;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.text.DecimalFormat;

public class Benchmark{
    final static int UNIT = 1000000; //MB 
    final static int MICRO = 1000;
    final static int INT_SIZE = Integer.SIZE/8;
    private String folder;

    private void setInvFile() throws IOException{
        File terms = new File(folder + "/gov.terms");
        File postings = new File(folder + "/gov.postings"); 
        Codec[] codecs = {new Uncompressed(null), 
            new PForDelta(folder + "/pfordelta.postings"),
            new FastPFor(folder + "/fastpfor.postings"), 
            new Milc(folder + "/milc.postings")};

        try{
            Scanner scanTerm = new Scanner(terms);
            Scanner scanPostings = new Scanner(postings);
            long size=0, sizeFPF=0, sizePFD=0, sizeMilc=0;

            while(scanTerm.hasNextLine()){
                String term = scanTerm.nextLine();
                String str_posting = scanPostings.nextLine();
                String[] list = str_posting.split(" ");
                int[] posting = new int[list.length];
                int i=0;
                
                // System.out.println(term + " : ");
                for(String docId : list)
                    posting[i++] = Integer.parseInt(docId);

                int[] PFDCompressed = codecs[1].compress(posting.clone());
                codecs[1].write(PFDCompressed);

                int[] FPFCompressed = codecs[2].compress(posting.clone());
                codecs[2].write(FPFCompressed);

                int[] MilcCompressed = codecs[3].compress(posting); //nao modifica posting
                codecs[3].write(MilcCompressed);

                size += (long)term.length() + INT_SIZE * posting.length; //tamanho nao comprimido
                sizePFD += (long)term.length() + INT_SIZE*PFDCompressed.length;
                sizeFPF += (long)term.length() + INT_SIZE*FPFCompressed.length;
                sizeMilc += (long)term.length() + INT_SIZE*MilcCompressed.length;
            }
            codecs[1].closeWriter();
            codecs[2].closeWriter();
            codecs[3].closeWriter();

            scanTerm.close();
            scanPostings.close();

            DecimalFormat df = new DecimalFormat("0.00");
            System.out.println("Tamanhos dos arquivos:");
            System.out.println("\t*descomprimido: " + df.format((double)size/UNIT)
                + "MB");
            System.out.println("\t*Comprimido com " + codecs[1].getMethod() + ": " + df.format((double)sizePFD/UNIT)
                + "MB (" + df.format((double)size/sizePFD) + ":1)");
            System.out.println("\t*Comprimido com " + codecs[2].getMethod() + ": " + df.format((double)sizeFPF/UNIT)
                + "MB (" + df.format((double)size/sizeFPF) + ":1)");
            System.out.println("\t*Comprimido com " + codecs[3].getMethod() + ": " + df.format((double)sizeMilc/UNIT)
                + "MB (" + df.format((double)size/sizeMilc) + ":1)");

        }catch(FileNotFoundException e){
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void queryProcessor(String src) throws IOException{
        File queryFile = new File(src);
        Scanner queryReader = new Scanner(queryFile);
        Codec[] codecs = {new Uncompressed(null), 
                          new PForDelta(null),
                          new FastPFor(null), 
                          new Milc(null)};

        int numQuery = 1;
        long start, end; //medidor de tempo em disco
        while(queryReader.hasNextLine()){
            String query = queryReader.nextLine();
            String[] terms = query.split(" ");

            System.out.println("***Query " + numQuery++);
            for(Codec codec : codecs){
                start = System.currentTimeMillis();
                getDocs(terms, codec);
                end = System.currentTimeMillis();
                System.out.println("\t#Tempo de processamento em disco("+codec.getMethod()+"): "
                    + (end-start) + "ms\n");
            }
            System.out.println("");
        }
        queryReader.close();
    }

    private void getDocs(String[] queryTerms, Codec codec){
        int[][] postings = getList(queryTerms, 
                codec.getFileName());
        if(postings==null)
            return;
        long start = System.nanoTime();

        Set<Integer> res = new HashSet<Integer>();
        int[] recov = codec.uncompress(postings[0]);
        for(int num:recov)
            res.add(num);

        Set<Integer> rm = new HashSet<Integer>();
        for(int i=1; i<postings.length; i++){
            Iterator<Integer> it = res.iterator();
            recov = codec.uncompress(postings[i]);
            while(it.hasNext()){
                int key = it.next();
                int find = Arrays.binarySearch(recov, key);
                if(find<0)
                    rm.add(key);
            }
        }
        for(int num:rm)
            res.remove(num);
        long end = System.nanoTime();

        System.out.print("\t#Documentos relevantes: ");
        for(int num:res)
            System.out.print(num+" ");
        System.out.println("\n\t#Tempo de processamento em memoria("+codec.getMethod()+"): " 
            + ((double)(end-start)/MICRO)+ "us");
    }

    private int[][] getList(String[] queryTerms, String file){
        File termsFile = new File(folder + "/gov.terms");
        File postingsFile = new File(folder + file);
        int[][] postings = new int[queryTerms.length][100];

        try{
            Scanner termsScan = new Scanner(termsFile);
            Scanner postingsScan = new Scanner(postingsFile);
            while(termsScan.hasNextLine()){
                String term = termsScan.nextLine();
                String[] str_posting = postingsScan.nextLine().split(" ");
                for(int i=0; i<queryTerms.length; i++){
                    if(queryTerms[i].equals(term)){
                        int[] posting = new int[str_posting.length];
                        for(int j=0; j<str_posting.length; j++)
                            posting[j] = Integer.parseInt(str_posting[j]);
                        postings[i] = posting;
                    }
                }
            }
            termsScan.close();
            postingsScan.close();
            return postings;
        }catch(FileNotFoundException e){
            System.out.println("Erro de acesso ao arquivo!" + e.getMessage());
            return null;
        }
    }

    public Benchmark(String folder){
        this.folder = folder;
    }
    public static void main(String[] args){
        String folder = "indexed";
        Benchmark bm = new Benchmark(folder);
        try{
            bm.setInvFile();
            bm.queryProcessor("collections/queries.query");
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}