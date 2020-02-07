package com.jackson.app;

import me.lemire.integercompression.*;
import java.util.Random;
import java.util.Arrays;

/**
 * Executa os testes
 * Passos
 * 1- Definir um multiplo de 256 (em razao do PFD e do FastPFOR)
 * 2- Cria um vetor de numeros aleatorios
 * 3- Executar cada um dos 3 metodos 10 vezes e tirar media 
 */

public class App 
{
    private static int[] testFastPForCompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] res = new int[4*data.length+1024];
        FastPFOR fp = new FastPFOR();
        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();

		for(int i=1; i<=numTimes; i++){
			inpos.set(0);
			outpos.set(0);
            long start = System.nanoTime();
			fp.compress(data, inpos, data.length-inpos.get(), res, outpos);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/1000000;
		}
		System.out.println("Tempo de compressao (FastPFor): "+ ((double)medExec/numTimes) + "ms");
        return Arrays.copyOf(res, outpos.get());
        // return res;
	}

	private static int[] testFastPForUncompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] recov = new int[data[0]]; //data[0] = tamanho da lista original
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
        FastPFOR fp = new FastPFOR();

		for(int i=1; i<=numTimes; i++){
			long start = System.nanoTime();
			inpos.set(0);
			outpos.set(0);
			fp.uncompress(data, inpos, data.length-inpos.get(), recov, outpos);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/100000;
		}
		System.out.println("Tempo de descompressao (FastPFor): "+ ((double)medExec/numTimes) + "ms");
        return recov;
	}

    private static int[] testMilcCompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] res = new int[1];
        Milc mc = new Milc();

		for(int i=1; i<=numTimes; i++){
            long start = System.nanoTime();
			res = mc.compress(data);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/1000000;
		}
		System.out.println("Tempo de compressao (Milc): "+ ((double)medExec/numTimes) + "ms");
        return res;
        // return res;
	}

    private static int[] testMilcUncompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] recov = new int[data[0]]; //data[0] = tamanho da lista original
        Milc mc = new Milc();

		for(int i=1; i<=numTimes; i++){
			long start = System.nanoTime();
			recov = mc.uncompress(data);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/100000;
		}
		System.out.println("Tempo de descompressao (Milc): "+ ((double)medExec/numTimes) + "ms");
        return recov;
	}

    private static int[] testNewPFDCompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] res = new int[4*data.length+1024];
        NewPFD newpfd = new NewPFD();
        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();

		for(int i=1; i<=numTimes; i++){
			inpos.set(0);
			outpos.set(0);
            long start = System.nanoTime();
			newpfd.compress(data, inpos, data.length-inpos.get(), res, outpos);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/1000000;
		}
		System.out.println("Tempo de compressao (NewPForDelta): "+ ((double)medExec/numTimes) + "ms");
        return Arrays.copyOf(res, outpos.get());
        // return res;
	}

    private static int[] testNewPFDUncompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] recov = new int[data[0]]; //data[0] = tamanho da lista original
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
        NewPFD newpfd = new NewPFD();

		for(int i=1; i<=numTimes; i++){
			long start = System.nanoTime();
			inpos.set(0);
			outpos.set(0);
			newpfd.uncompress(data, inpos, data.length-inpos.get(), recov, outpos);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/100000;
		}
		System.out.println("Tempo de descompressao (NewPForDelta): "+ ((double)medExec/numTimes) + "ms");
        return recov;
	}

    public static void main( String[] args )
    {
        Random rd = new Random();
        int[] data = new int[256];
        data[0]=rd.nextInt(10);
        for(int i=1; i<data.length; i++)
            data[i]=data[i-1]+rd.nextInt(10);

        int[] compressed = testNewPFDCompress(data, 10);
        int[] tt = testNewPFDUncompress(compressed, 10);
        System.out.println("Espaco: " + ((double)compressed.length*4/1000000) + "MB\n");

        compressed = testFastPForCompress(data, 10);
        tt = testFastPForUncompress(compressed, 10);
        System.out.println("Espaco: " + ((double)compressed.length*4/1000000) + "MB\n");

        compressed = testMilcCompress(data, 1);
        tt = testMilcUncompress(compressed, 10);
        System.out.println("Espaco: " + ((double)compressed.length*4/1000000) + "MB\n");
    }
}
