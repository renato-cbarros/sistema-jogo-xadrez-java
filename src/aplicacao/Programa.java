package aplicacao;

import java.util.List;
import java.util.ArrayList;
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
		List<PecaXadrez> capturada = new ArrayList<>();
		
		while (!partidaXadrez.getCheckMate()) {
			try {
				
				UI.clearScreen();
				UI.mostraPartida(partidaXadrez, capturada);
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
			
				if (pecaCapturada != null) {
					capturada.add(pecaCapturada);
				}
				
				if (partidaXadrez.getPromocao() != null) {
					System.out.println("Digite a peca para promocao (B/C/R/A): ");
					String tipo = sc.nextLine();
					partidaXadrez.substituiPecaPromovida(tipo);
				}
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
		UI.clearScreen();
		UI.mostraPartida(partidaXadrez, capturada);

	}

}
