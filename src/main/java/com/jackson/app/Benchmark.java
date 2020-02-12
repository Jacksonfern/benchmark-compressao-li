package com.jackson.app;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class Benchmark{
    final static int UNIT = 1000000; //MB 
    final static int INT_SIZE = Integer.SIZE/8;

    private void invFile(String folder){
        File terms = new File(folder + "/file.terms");
        File postings = new File(folder + "/file.postings");
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
                
                System.out.println(term + " : ");
                for(String docId : list)
                    posting[i++] = Integer.parseInt(docId);
                int[] FPFCompressed = Compressor.FastPFor(posting.clone());
                int[] PFDCompressed = Compressor.NewPFD(posting.clone());
                int[] MilcCompressed = Compressor.Milc(posting); //nao modifica posting

                size += (long)term.length() + INT_SIZE * posting.length; //tamanho nao comprimido
                sizeFPF += (long)term.length() + INT_SIZE*FPFCompressed.length;
                sizePFD += (long)term.length() + INT_SIZE*PFDCompressed.length;
                sizeMilc += (long)term.length() + INT_SIZE*MilcCompressed.length;
            }
            scanTerm.close();
            scanPostings.close();
            System.out.println("Tamanhos dos arquivos:");
            System.out.println("\t*descomprimido: " + ((double)size/UNIT)
                + "MB");
            System.out.println("\t*Comprimido com NewPFD : " + ((double)sizePFD/UNIT)
                + "MB");
            System.out.println("\t*Comprimido com FastPFor: " + ((double)sizeFPF/UNIT)
                + "MB");
            System.out.println("\t*Comprimido com Milc: " + ((double)sizeMilc/UNIT)
                + "MB");

        }catch(FileNotFoundException e){
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void writeCompress(int[] data){

        // int sizeFPF = (4*FPFCompressed.length)/UNIT;
        // int sizePFD = (4*PFDCompressed.length)/UNIT;
        // int sizeMilc = (4*MilcCompressed.length)/UNIT;

        // int[] a = Uncompressor.FastPFor(FPFCompressed);
        // int[] b = Uncompressor.NewPFD(PFDCompressed);
        // int[] c = Uncompressor.Milc(MilcCompressed);
    }

    public Benchmark(){

    }
    public static void main(String[] args){
        String folder = "indexed";
        Benchmark bm = new Benchmark();
        bm.invFile(folder);
        // int[] data = {1,2,3,4,5,6,7,8,9,10};
        // int[] c = PForDelta.testNewPFDCompress(data, 1);
        // int[] d = PForDelta.testNewPFDUncompress(c, 1);
        // System.out.println(d.length);
        // for(int i:d)
        //     System.out.println(i + " - " + Integer.toBinaryString(i));
    }
}