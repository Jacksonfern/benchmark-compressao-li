package com.jackson.app;


import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.IntegratedIntCompressor;
import me.lemire.integercompression.differential.Delta;
import java.util.Arrays;

public class PForDelta {

	public static int[] testNewPFDCompress(int[] data, int numTimes){
		double medExec = 0.0;
        int[] res = new int[4*data.length+1024];
        NewPFD newpfd = new NewPFD();
        IntWrapper inpos = new IntWrapper();
        IntWrapper outpos = new IntWrapper();

		for(int i=1; i<=numTimes; i++){
			inpos.set(0);
			outpos.set(0);
            long start = System.nanoTime();
			int[] gaps = data.clone();
			Delta.delta(gaps);
			newpfd.compress(gaps, inpos, gaps.length-inpos.get(), res, outpos);
			long end = System.nanoTime();
			medExec+=(double)(end-start)/1000000;
		}
		System.out.println("Tempo de compressao (NewPForDelta): "+ ((double)medExec/numTimes) + "ms");
        return Arrays.copyOf(res, outpos.get());
        // return res;
	}

    public static int[] testNewPFDUncompress(int[] data, int numTimes){
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
			for(int j=1; j<recov.length; j++)
				recov[j]+=recov[j-1];
			long end = System.nanoTime();
			medExec+=(double)(end-start)/100000;
		}
		System.out.println("Tempo de descompressao (NewPForDelta): "+ ((double)medExec/numTimes) + "ms");
        return recov;
	}

	public PForDelta(){
	}
}