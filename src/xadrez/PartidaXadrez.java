package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {
	
	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	
	private List<Peca> pecasNoTabuleiro = new ArrayList<>();
	private List<Peca> pecasCapturadas = new ArrayList<>();


	public PartidaXadrez() {
		tabuleiro = new Tabuleiro(8, 8);
		turno = 1;
		jogadorAtual = Cor.BRANCO;
		formacaoInicial();
	}
	
	public int getTurno() {
		return turno;
	}
	
	public Cor getJogadorAtual() {
		return jogadorAtual;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
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
	
	public boolean[][] possiveisMovimentos(PosicaoXadrez posicaoInicial){
		Posicao posicao = posicaoInicial.passaPosicaoXadrez();
		validaPosicaoInicial(posicao);
		return tabuleiro.peca(posicao).possiveisMovimentos();
	}
	
	public PecaXadrez moveXadrez(PosicaoXadrez posicaoInicial, PosicaoXadrez posicaoFinal) {
		Posicao inicial = posicaoInicial.passaPosicaoXadrez();
		Posicao fim = posicaoFinal.passaPosicaoXadrez();
		validaPosicaoInicial(inicial);
		validaPosicaoFinal(inicial, fim);
		Peca capturaPeca = fazMover(inicial,fim);
		
		if (testaCheck(jogadorAtual)) {
			desfazMovimento(inicial, fim, capturaPeca);
			throw new XadrezException("Voce nao pode se colocar em check");
		}
		
		check = (testaCheck(oponente(jogadorAtual))) ? true : false;
		
		if (testaCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		}
		else {
			proximoTurno();
		}
		return (PecaXadrez)capturaPeca;
	} 
	
	private Peca fazMover(Posicao inicial, Posicao fim) {
		Peca p = tabuleiro.removePeca(inicial);
		Peca pecaCapturada = tabuleiro.removePeca(fim);
		tabuleiro.colocaPeca(p, fim);
		
		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		return pecaCapturada;
	}
	
	private void desfazMovimento(Posicao inicial, Posicao fim, Peca pecaCapturada) {
		Peca p = tabuleiro.removePeca(fim);
		tabuleiro.colocaPeca(p, inicial);
		
		if (pecaCapturada != null) {
			tabuleiro.colocaPeca(pecaCapturada, fim);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
	}
	
	public void validaPosicaoInicial(Posicao posicao) {
		if (!tabuleiro.temUmaPeca(posicao)) {
			throw new XadrezException("Posição inicial sem peça");
		}
		if (jogadorAtual != ((PecaXadrez)tabuleiro.peca(posicao)).getCor()) {
			throw new XadrezException("Peca nao pertence ao jogador do turno");
		}
		if (!tabuleiro.peca(posicao).temUmMovimentoPossivel()) {
			throw new XadrezException("Nao existe movimentos possiveis");
		}
	}
	
	public void validaPosicaoFinal(Posicao inicial, Posicao fim) {
		if (!tabuleiro.peca(inicial).possiveisMovimentos(fim)) {
			throw new XadrezException("Nao e possivel mover a peca para o destino");
		}
	}
	
	private void proximoTurno() {
		turno++;
		jogadorAtual = (jogadorAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private void colocaNovaPeca(char coluna, int linha, PecaXadrez peca) {
		tabuleiro.colocaPeca(peca, new PosicaoXadrez(coluna,linha).passaPosicaoXadrez());
		pecasNoTabuleiro.add(peca);
	} 
	
	private Cor oponente(Cor cor) {
		return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
	}
	
	private PecaXadrez rei(Cor cor) {
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peca p : list) {
			if (p instanceof Rei) {
				return (PecaXadrez)p;
			}
		}
		throw new IllegalStateException("Nao existe o rei da cor " + cor + " no tabuleiro");
	}
	
	private boolean testaCheck(Cor cor){
		Posicao reiPosicao = rei(cor).getPosicaoXadrez().passaPosicaoXadrez();
		List<Peca> oponentePecas = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == oponente(cor)).collect(Collectors.toList());
		for (Peca p : oponentePecas) {
			boolean[][] mat = p.possiveisMovimentos();
			if (mat[reiPosicao.getLinha()][reiPosicao.getColuna()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testaCheckMate(Cor cor){
		if (!testaCheck(cor)) {
			return false;
		}
		List<Peca> list = pecasNoTabuleiro.stream().filter(x -> ((PecaXadrez)x).getCor() == cor).collect(Collectors.toList());
		
		for (Peca p : list) {
			boolean[][] mat = p.possiveisMovimentos();
			for (int i = 0; i < tabuleiro.getLinhas(); i++) {
				for (int j = 0; j < tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posicao inicial = ((PecaXadrez)p).getPosicaoXadrez().passaPosicaoXadrez();
						Posicao fim = new Posicao(i, j);	
						Peca pecaCapturada = fazMover(inicial, fim);
						boolean testaCheck = testaCheck(cor);
						desfazMovimento(inicial, fim, pecaCapturada);
						if (!testaCheck) {
							return false;
						}
					}
				}
				
			}
		}
		
		return true;
	}
	
	public void formacaoInicial() {
		colocaNovaPeca('h', 7, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('d', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO));

		colocaNovaPeca('b', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('a', 8, new Rei(tabuleiro, Cor.PRETO));

	}

}
