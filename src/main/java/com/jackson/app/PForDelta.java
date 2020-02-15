package com.jackson.app;

import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.Delta;
import me.lemire.integercompression.Kamikaze;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
// import com.kamikaze.pfordelta.*;

public class PForDelta implements Codec{
    private FileWriter writer;
    private IntegerCODEC codec;

    public PForDelta(String src){
        this.codec = new Composition(new Kamikaze(), new VariableByte());
        if(src==null)
            return;
        try{
            this.writer = new FileWriter(src);
        }catch(IOException e){
            this.writer = null;
        }
    }

    @Override
    public int[] compress(int[] data){
        int[] res = new int[4*data.length+1024];
		// NewPFD newpfd = new NewPFD();
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
		
		inpos.set(0);
		outpos.set(1);
		res[0] = data.length;
		Delta.delta(data);
		codec.compress(data, inpos, data.length-inpos.get(), res, outpos);

        return Arrays.copyOf(res, outpos.get());
    }

    @Override
    public int[] uncompress(int[] data){
        int[] recov = new int[data[0]]; //data[0] = tamanho da lista original
        IntWrapper inpos = new IntWrapper();
		IntWrapper outpos = new IntWrapper();
		// NewPFD newpfd = new NewPFD();
		
		inpos.set(1);
		outpos.set(0);
		codec.uncompress(data, inpos, data.length-inpos.get(), recov, outpos);
		for(int j=1; j<recov.length; j++)
			recov[j]+=recov[j-1];
        return recov;
    }

    @Override
    public String getMethod(){
        return "PForDelta";
    }

    @Override
    public String getFileName(){
        return "/pfordelta.postings";
    }

    @Override
    public void write(int[] data){
        if(writer==null)
            return;
        try{
            for(int num:data)
                writer.write(num + " ");
            writer.write('\n');
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void closeWriter() throws IOException{
        if(writer!=null)
            writer.close();
    }
}