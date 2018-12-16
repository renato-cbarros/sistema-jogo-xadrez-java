package aplicacao;

import java.util.InputMismatchException;
import java.util.Scanner;

import xadrez.PartidaXadrez;
import xadrez.PecaXadrez;
import xadrez.PosicaoXadrez;
import xadrez.XadrezException;

public class Programa {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Scanner sc = new Scanner(System.in);
		
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		
		while (true) {
			try {
				
				UI.clearScreen();
				UI.mostraTabuleiro(partidaXadrez.getPecas());
				System.out.println("");
				System.out.println("");
				System.out.print("Inicial: ");
				PosicaoXadrez inicial = UI.lerPosicaoXadrez(sc);
				
				boolean[][] possiveisMovimentos = partidaXadrez.possiveisMovimentos(inicial);
				UI.clearScreen();
				UI.mostraTabuleiro(partidaXadrez.getPecas(), possiveisMovimentos);
			
				System.out.println("");
				System.out.print("Final: ");
				PosicaoXadrez fim = UI.lerPosicaoXadrez(sc);
				
				PecaXadrez pecaCapturada = partidaXadrez.moveXadrez(inicial, fim);
			
			}
			catch (XadrezException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
			catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}

		}

	}

}
