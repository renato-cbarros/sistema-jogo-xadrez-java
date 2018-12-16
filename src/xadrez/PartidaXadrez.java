package xadrez;

import pecas.xadrez.Rei;
import pecas.xadrez.Torre;
import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
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
	
	public PecaXadrez moveXadrez(PosicaoXadrez posicaoInicial, PosicaoXadrez posicaoFinal) {
		Posicao inicial = posicaoInicial.passaPosicaoXadrez();
		Posicao fim = posicaoFinal.passaPosicaoXadrez();
		validaPosicaoInicial(inicial);
		Peca capturaPeca = fazMover(inicial,fim);
		return (PecaXadrez)capturaPeca;
	} 
	
	private Peca fazMover(Posicao inicial, Posicao fim) {
		Peca p = tabuleiro.removePeca(inicial);
		Peca pecaCapturada = tabuleiro.removePeca(fim);
		tabuleiro.colocaPeca(p, fim);
		return pecaCapturada;
	}
	
	public void validaPosicaoInicial(Posicao posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadrezException("Posição inicial sem peça");
		}
		if (!tabuleiro.peca(posicao).temUmMovimentoPossivel()) {
			throw new XadrezException("Nao existe movimentos possiveis");
		}
	}
	
	private void colocaNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna,linha).passaPosicaoXadrez());
	} 
	
	public void formacaoInicial() {
		colocaNovaPeca('c', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('c', 2, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('d', 2, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 2, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('d', 1, new Rei(tabuleiro, Cor.BRANCO));

		colocaNovaPeca('c', 7, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('c', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('d', 7, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('e', 7, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('e', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('d', 8, new Rei(tabuleiro, Cor.PRETO));
	}

}
