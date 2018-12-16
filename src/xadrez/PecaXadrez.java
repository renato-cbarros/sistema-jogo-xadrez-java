package xadrez;

import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;

public abstract class PecaXadrez extends Peca{
	
	private Cor cor;
	private int contadorMovimentos;

	public PecaXadrez(Tabuleiro tabuleiro, Cor cor) {
		super(tabuleiro);
		this.cor = cor;
	}

	public Cor getCor() {
		return cor;
	}
	
	public int getContadorMovimentos() {
		return contadorMovimentos;
	}
	
	public void incrementaContadorMovimentos() {
		contadorMovimentos++;
	}
	
	public void decrementaContadorMovimentos() {
		contadorMovimentos--;
	}
		
	
	public PosicaoXadrez getPosicaoXadrez() {
		return PosicaoXadrez.dePosicaoXadrez(posicao);
	}
	
	protected boolean pecaDoOponente(Posicao posicao) {
		PecaXadrez p = (PecaXadrez)getTabuleiro().peca(posicao);
		return p != null && p.getCor() != cor;
	}

}
