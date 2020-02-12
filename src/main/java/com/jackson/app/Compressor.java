package com.jackson.app;

import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.Delta;
import java.util.Arrays;

public class Compressor{

	public static int[] NewPFD(int[] data){
        int[] res = new int[4*data.length+1024];
		// NewPFD newpfd = new NewPFD();
		IntegerCODEC newpfd = new Composition(new NewPFD(), new VariableByte());
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
		
		inpos.set(0);
		outpos.set(1);
		res[0] = data.length;
		Delta.delta(data);
		newpfd.compress(data, inpos, data.length-inpos.get(), res, outpos);

        return Arrays.copyOf(res, outpos.get());
        // return res;
	}

    public static int[] FastPFor(int[] data){
		int[] res = new int[4*data.length+1024]; //heuristica do prof. Lemire
		IntegerCODEC fp = new Composition(new FastPFOR(), new VariableByte());
        // FastPFOR fp = new FastPFOR();
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
		
		inpos.set(0);
		outpos.set(1);
		Delta.delta(data);
		res[0] = data.length;
		fp.compress(data, inpos, data.length-inpos.get(), res, outpos);
        return Arrays.copyOf(res, outpos.get());
        // return res;
	}

	public static int[] Milc(int[] data){
		Milc mc = new Milc();
		int[] res = mc.compress(data);
        return res;
	}
    public static void main(String[] main){

    }
}