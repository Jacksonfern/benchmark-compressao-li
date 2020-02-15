package com.jackson.app;

public class Uncompressed implements Codec{
    public Uncompressed(String src){
    }

    @Override
    public int[] compress(int[] data){
        return data;
    }

    @Override
    public int[] uncompress(int[] data){
        return data;
    }

    @Override
    public String getMethod(){
        return "Descomprimido";
    }

    @Override
    public String getFileName(){
        return "/gov.postings";
    }

    @Override
    public void write(int[] data){
        return;
    }

    @Override
    public void closeWriter(){
        return;
    }
}