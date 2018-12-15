package aplicacao;

import xadrez.PartidaXadrez;

public class Programa {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		PartidaXadrez partidaXadrez = new PartidaXadrez();
		
		UI.mostraTabuleiro(partidaXadrez.getPecas());

	}

}
