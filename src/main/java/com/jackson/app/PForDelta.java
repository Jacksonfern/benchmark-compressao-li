package com.jackson.app;


import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.IntegratedIntCompressor;

public class PForDelta {

    public static void main(String[] args){
		int[] data = new int[256];
		int[] res = new int[4*data.length + 1024];
		IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
		inpos.set(0);
		outpos.set(0);

		for(int i=0; i<data.length; i++)
			data[i]=i*2;
		NewPFD pfd = new NewPFD();
		pfd.compress(data, inpos, data.length - inpos.get(), res, outpos);
		int[] teste = new int[data.length];
		inpos.set(0);
		outpos.set(0);
		pfd.uncompress(res, inpos, res.length, teste, outpos);
		for(int i=0; i<teste.length; i++)
			System.out.println(teste[i]);
	}
}