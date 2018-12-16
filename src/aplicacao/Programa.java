package aplicacao;

import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.PosicaoXadrez;

public class Programa {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Scanner sc = new Scanner(System.in);
		
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		
		while (true) {
			UI.mostraTabuleiro(partidaXadrez.getPecas());
			System.out.println("");
			System.out.println("");
			System.out.print("Inicial: ");
			PosicaoXadrez inicial = UI.lerPosicaoXadrez(sc);
		
			System.out.println("");
			System.out.print("Final: ");
			PosicaoXadrez fim = UI.lerPosicaoXadrez(sc);
			
			PecaXadrez pecaCapturada = partidaXadrez.moveXadrez(inicial, fim);
		}

	}

}
