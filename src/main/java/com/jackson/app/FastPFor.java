package com.jackson.app;

import me.lemire.integercompression.differential.IntegratedIntCompressor;
import me.lemire.integercompression.*;
//import fi.iki.yak.compression.integer.*;
public class FastPFor {
	private static void binary(int x){ //converte pra binario (pra depuracao)
		for(int i=31; i>=0; i--)
			System.out.print((x>>>i)&1);
		System.out.println("");
	}

	public FastPFor(){
	}

	public double timeFastPForCompress(int[] data, int numTimes, FastPFOR fp){
		double medExec = 0.0;
		for(int i=1; i<=numTimes; i++){
			long start = System.nanoTime();
			int[] res = new int[4*data.length+1024];
			IntWrapper inpos = new IntWrapper();
			IntWrapper outpos = new IntWrapper();
			inpos.set(0);
			outpos.set(0);

			fp.compress(data, inpos, data.length-inpos.get(), res, outpos);
			long end = System.nanoTime();
			medExec+=(double)end-start;
		}
		return medExec/10.0;
	}

	public double timeFastPForUncompress(int[] data, int numTimes, FastPFOR fp){
		double medExec = 0.0;
		for(int i=1; i<=numTimes; i++){
			long start = System.nanoTime();
			int[] res = new int[4*data.length+1024];
			IntWrapper inpos = new IntWrapper();
			IntWrapper outpos = new IntWrapper();
			inpos.set(0);
			outpos.set(0);
			
			int[] recov = new int[data.length];
			fp.uncompress(res, inpos, res.length-inpos.get(), recov, outpos);
			long end = System.nanoTime();
			medExec+=(double)end-start;
		}
		return medExec/10.0;
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
