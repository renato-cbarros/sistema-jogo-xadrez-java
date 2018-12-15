package xadrez;

import pecas.xadrez.Rei;
import pecas.xadrez.Torre;
import tabuleirojogo.Tabuleiro;

public class PartidaXadrez {
	
	private Tabuleiro tabuleiro;
	
	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		formacaoInicial();
	}
	
	public PecaXadrez[][] getPecas(){
		PecaXadrez[][] mat = new PecaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
		
		for(int i = 0; i<tabuleiro.getLinhas(); i++) {
			for(int j = 0; j<tabuleiro.getColunas(); j++) {
				mat[i][j] = (PecaXadrez)tabuleiro.peca(i, j);
			}
		}
		return mat;
	}
	
	private void colocaNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna,linha).passaPosicaoXadrez());
	} 
	
	public void formacaoInicial() {
		colocaNovaPeca('b', 6, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO));
		colocaNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO));
	}

}
