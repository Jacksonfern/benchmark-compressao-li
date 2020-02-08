package com.jackson.app;

import me.lemire.integercompression.*;
import me.lemire.integercompression.differential.Delta;
import java.util.Random;
import java.util.Arrays;

/**
 * Executa os testes
 * Passos
 * 1- Definir um multiplo de 256 (em razao do PFD e do FastPFOR)
 * 2- Cria um vetor de numeros aleatorios
 * 3- Executar cada um dos 3 metodos 10 vezes e tirar media 
 */

public class App 
{
    public static void main( String[] args )
    {
		int[] sparse = {1, 10, 50, 100, 500, 1000};
		for(int i=16; i<17; i++){ //data.length=2^i elementos (multiplo de 512)
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

				int[] compressed = PForDelta.testNewPFDCompress(data, 3);
				int[] tt = PForDelta.testNewPFDUncompress(compressed, 3);
				System.out.println("Espaco: " + ((double)compressed.length*4/1000000) + "MB\n");

				compressed = FastPFor.testFastPForCompress(data, 3);
				tt = FastPFor.testFastPForUncompress(compressed, 3);
				System.out.println("Espaco: " + ((double)compressed.length*4/1000000) + "MB\n");

				compressed = Milc.testMilcCompress(data, 3);
				tt = Milc.testMilcUncompress(compressed, 3);
				System.out.println("Espaco: " + ((double)compressed.length*4/1000000) + "MB\n");
			}
		}
    }
}
