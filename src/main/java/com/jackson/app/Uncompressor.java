package com.jackson.app;

import me.lemire.integercompression.*;

public class Uncompressor{

    public static int[] NewPFD(int[] data){
        int[] recov = new int[data[0]]; //data[0] = tamanho da lista original
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
		IntegerCODEC newpfd = new Composition(new NewPFD(), new VariableByte());
		// NewPFD newpfd = new NewPFD();
		
		inpos.set(1);
		outpos.set(0);
		newpfd.uncompress(data, inpos, data.length-inpos.get(), recov, outpos);
		for(int j=1; j<recov.length; j++)
			recov[j]+=recov[j-1];
        return recov;
    }
    
    public static int[] FastPFor(int[] data){
        int[] recov = new int[data[0]]; //data[0] = tamanho da lista original + offset
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
		IntegerCODEC fp = new Composition(new FastPFOR(), new VariableByte());
		// FastPFOR fp = new FastPFOR();

		inpos.set(1);
		outpos.set(0);
		fp.uncompress(data, inpos, data.length-inpos.get(), recov, outpos);
		for(int j=1; j<recov.length; j++)
			recov[j]+=recov[j-1];
        return recov;
    }
    
    public static int[] Milc(int[] data){
        int[] recov = new int[data[0]]; //data[0] = tamanho da lista original
		Milc mc = new Milc();
		recov = mc.uncompress(data);
        return recov;
	}
    public static void main(String[] args){

    }
}

