package com.jackson.app;

// import me.lemire.integercompression.*;
// import me.lemire.integercompression.differential.Delta;
// import java.util.Arrays;
import java.util.Random;

/**
 * Executa testes (para depuração)
 * Passos
 * 1- Definir um multiplo de 256 (em razao do PFD e do FastPFOR)
 * 2- Cria um vetor de numeros aleatorios
 * 3- Executar cada um dos 3 metodos 10 vezes e tirar media 
 */

public class App 
{
    public static void main( String[] args )
    {
		Codec[] codecs = { new Uncompressed(null),
						   new PForDelta(null),
						   new FastPFor(null),
						   new Milc(null)
		};
		int[] sparse = {1, 10, 50, 100, 500, 1000};
		for(int i=24; i<25; i++){ //data.length=2^i elementos (multiplo de 512)
			System.out.println("************************************");
			System.out.println("Tamanho da lista: " + (1<<i));
			System.out.println("Tamanho: " + ((double)4*(1<<i)/1000000) + "MB");
			for(int j=0; j<sparse.length; j++){
				Random rd = new Random();
				int[] data = new int[1<<i];
				System.out.println("*Gerando lista de espalhamento " + (sparse[j])+"...");
				data[0]=rd.nextInt(sparse[j])+1;
				for(int k=1; k<data.length; k++)
					data[k]=data[k-1]+rd.nextInt(sparse[j])+1;
				for(Codec codec:codecs){
					System.out.print("\tTempo de compressao (" + codec.getMethod() + "): ");
					long start = System.nanoTime();
					int[] res = codec.compress(data);
					long end = System.nanoTime();
					System.out.println((double)(end-start)/1000.0+"ms");
					System.out.println("\tTamanho: " + (double)(res.length*4)/1000000 + "MB");

					System.out.print("\tTempo de descompressao (" + codec.getMethod() + "): ");
					start = System.nanoTime();
					codec.uncompress(res);
					end = System.nanoTime();
					System.out.println((double)(end-start)/1000.0+"ms\n");

				}
			}
		}
    }
}
