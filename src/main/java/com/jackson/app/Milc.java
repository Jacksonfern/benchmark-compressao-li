package com.jackson.app;

import java.lang.Math;
import java.util.ArrayList;

public class Milc{
	final static int OVERHEAD = 80;
	final static int L = 2*OVERHEAD;
	final static int[] mask = {0x00000000,
			0x00000001, 0x00000003, 0x00000007, 0x0000000f,
			0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff,
			0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff,
			0x00001fff, 0x00003fff, 0x00007fff, 0x0000ffff,
			0x0001ffff, 0x0003ffff, 0x0007ffff, 0x000fffff,
			0x001fffff, 0x003fffff, 0x007fffff, 0x00ffffff,
			0x01ffffff, 0x03ffffff, 0x07ffffff, 0x0fffffff,
			0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff
		};

	private static void binary(int x){ //converte pra binario (pra depuracao)
		for(int i=31; i>=0; i--)
			System.out.print((x>>>i)&1);
		System.out.println("");
	}

	private static int numBits(int x){ //Quantos bits precisa para representar um num
		int cont;
		for(cont=0; x!=0; cont++)
			x>>>=1;
		return cont;
	}

	public int[] compress(int[] data){
		int[] memo=new int[data.length];
		int[] blocks=new int[data.length];

		memo[0]=0;
		for(int i=1; i<data.length; i++){
		    memo[i]=1<<30; //numero inicial=oo
		    for(int j=Math.max(0, i-L); j<i; j++){
		      int cji = numBits(data[i]-data[j+1])*(i-j-1)+OVERHEAD;
		      if(memo[i]>=memo[j]+cji){
		      	memo[i]=memo[j]+cji;
		      	blocks[i]=i-j+1;
		      }
		    }
		}

		ArrayList<Integer> pilha = new ArrayList<Integer>();
		int sz=0;
		for(int i=data.length-1; i>=0; ){
		    pilha.add(blocks[i]);
		    sz+=numBits(data[i]-data[i-blocks[i]+1])*(blocks[i]-1)+OVERHEAD;
		    i-=blocks[i];
		}

		sz=(int)Math.ceil((double)sz/32.0);
		int m, num=0, pos=0, cont=0;
		int[] output = new int[1+sz];
		output[cont++]=data.length;
		for(int i=0, k=pilha.size()-1; i<data.length; i+=m){
			m=pilha.get(k--);
			int b=numBits((data[i+m-1]-data[i]));
			if(pos==0){
				output[cont++]=data[i]; //start value
				output[cont++]=i; //offset (posicao de inicio do bloco)
				num=m|(b<<8); //tamanho do bloco e b
				pos=16;
			}
			else{
				output[cont++]=num|((data[i]&0xffff)<<16); //coloca os 16 lsb no resto do num
				output[cont++]=(data[i]>>>16)|((i&0xffff)<<16); //coloca os 16 msb no comeco do num
				num=(i>>>16)|(m<<16)|(b<<24);
				output[cont++]=num;
				num=pos=0;
			}
  		}

		for(int i=0, k=pilha.size()-1; i<data.length; i+=m){
			m = pilha.get(k--); //tamanho do bloco
			int b=numBits(data[i+m-1]-data[i]);

			for(int j=1; j<m; j++){
				int value=data[i+j]-data[i];
				if(pos<=32-b){
					num|=(value<<pos); //pode gerar overflow, mas nao eh problema
					pos+=b;
				}
				else{
					num|=((value&mask[32-pos])<<pos);
					output[cont++]=num;
					num=value>>>(32-pos); //tira os LSB (ficaram no int anterior)
					pos+=b-32;
				}
			}
		}
		output[cont++]=num;
		return output;
	}

	public int[] uncompress(int[] data){
		int pos=0, cont=1, length=data[0];
		ArrayList<Integer> b = new ArrayList<Integer>();
		ArrayList<Integer> blockSize = new ArrayList<Integer>();
		int[] out = new int[length];

		while(length>0){
			if(pos==0){
				out[data[cont+1]]=data[cont]; //data[cont+1]==offset
				cont+=2;
				int auxsz = data[cont]&0xff;
				blockSize.add(auxsz);
				b.add((data[cont]>>>8)&0xff);
				pos=16;
				length-=auxsz;
			}
			else{	
				int offset=(data[cont+1]>>>16)|((data[cont+2]&0xffff)<<16);
				out[offset]=(data[cont]>>>16)|((data[cont+1]&0xffff)<<16);
				cont+=2;
				int auxsz = (data[cont]>>>16)&0xff;
				blockSize.add(auxsz);
				b.add((data[cont++]>>>24)&0xff);
				pos=0;
				length-=auxsz;
			}
		}

		for(int i=0, k=0; i<blockSize.size(); i++){
			int bi = b.get(i);
			for(int j=1; j<blockSize.get(i); j++){
				if(bi<=32-pos){
					out[k+j]=((data[cont]>>>pos)&mask[bi]) + out[k];
					pos+=bi;
				}
				else{
					int frag=(data[cont++]>>>pos)&mask[32-pos];
					out[k+j]=(frag|((data[cont]&mask[bi-32+pos])<<(32-pos))) + out[k];
					pos+=bi-32;
				}
			}
			k+=blockSize.get(i);
		}

		// for(int i=0; i<blockSize.size(); i++){
		// 	System.out.println("b: " + b.get(i));
		// 	System.out.println("block size: " + (blockSize.get(i)) + "\n");
		// }

		return out;
	}

	 public static int[] testMilcCompress(int[] data, int numTimes){
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

    public static int[] testMilcUncompress(int[] data, int numTimes){
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

	// public static void main(String[] args){
	// 	int[] paperExample = new int[256];
    //     for(int i = 108; i > 0; i--) {
    //         paperExample[i] = 120+ (int) (1.07*(double) (i-108));
    //     }
    //     for(int i = 108; i < paperExample.length; i++) {
    //         double val = 500D+2.7*(double) (i-108);
    //         paperExample[i] = (int) val;
    //     }

    //     // Alignments mentioned in the paper (middle values were not, they're approximations)
    //     paperExample[0] = 4;
    //     paperExample[107] = 120;
    //     paperExample[108] = 500;
    //     paperExample[255] = 900;

    //     // for(int i=0; i<paperExample.length; i++)
    //     // 	paperExample[i]=i+1;

    //     System.out.println("Espaco ocupado: " + ((double)(paperExample.length*4)/1000000) + "MB");
    //     System.out.println("\n****MILC****\n");

    //     Milc mc = new Milc();
    //     System.out.print("Tempo de compressão: ");
    //     long start = System.nanoTime();
	// 	int[] output = mc.compress(paperExample);
	// 	long end = System.nanoTime();

	// 	System.out.println(((double)(end-start)/1000000) + "ms");
	// 	System.out.println("Espaco ocupado: " +((double)(output.length*4)/1000000) + "MB");

	// 	System.out.print("Tempo de descompressão: ");
	// 	start = System.nanoTime();
	// 	int[] res = mc.uncompress(output);
	// 	end = System.nanoTime();
	// 	System.out.println(((double)(end-start)/1000000) + "ms");
	// }

	// public static void main(String[] args){
	// 	int vet[] = {120, 200, 270, 420, 820, 860, 1060, 
	// 			1160, 1220, 1340, 1800, 1980, 2160, 2400};
	// 	int[] res = compress(vet);
	// 	for(int i=0; i<res.length; i++){
	// 		System.out.print(res[i]+" ");
	// 		binary(res[i]);
	// 	}
	// }
}
