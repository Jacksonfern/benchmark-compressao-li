package com.jackson.app;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.Delta;
import java.util.Arrays;
//import fi.iki.yak.compression.integer.*;
public class FastPFor {

	public static int[] testFastPForCompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] res = new int[4*data.length+1024];
        FastPFOR fp = new FastPFOR();
        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();

		for(int i=1; i<=numTimes; i++){
			inpos.set(0);
			outpos.set(0);
            long start = System.nanoTime();
			int[] gaps = data.clone();
			Delta.delta(gaps);
			fp.compress(gaps, inpos, gaps.length-inpos.get(), res, outpos);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/1000000;
		}
		System.out.println("Tempo de compressao (FastPFor): "+ ((double)medExec/numTimes) + "ms");
        return Arrays.copyOf(res, outpos.get());
        // return res;
	}

	public static int[] testFastPForUncompress(int[] data, int numTimes){
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
			for(int j=1; j<recov.length; j++)
				recov[j]+=recov[j-1];
			long end = System.nanoTime();
			medExec+=(double)(end-start)/100000;
		}
		System.out.println("Tempo de descompressao (FastPFor): "+ ((double)medExec/numTimes) + "ms");
        return recov;
	}

	public FastPFor(){
	}
	
	// public static void main(String[] args){
	// 	int[] data = new int[10];
	// 	int[] res = new int[4*data.length + 1024];
	// 	IntWrapper inpos = new IntWrapper();
	// 	IntWrapper outpos = new IntWrapper();
	// 	inpos.set(0);
	// 	outpos.set(0);

	// 	for(int i=0; i<data.length; i++)
	// 		data[i]=i+1;
	// 	IntegerCODEC ic = new Composition(new FastPFOR(), new VariableByte());
	// 	ic.compress(data, inpos, data.length - inpos.get(), res, outpos);
	// 	int[] teste = new int[data.length];
	// 	inpos.set(0);
	// 	outpos.set(0);
	// 	ic.uncompress(res, inpos, res.length, teste, outpos);
	// 	for(int i=0; i<teste.length; i++)
	// 		System.out.println(teste[i]);
	// }
}
