package xadrez;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import tabuleirojogo.Peca;
import tabuleirojogo.Posicao;
import tabuleirojogo.Tabuleiro;
import xadrez.pecas.Bispo;
import xadrez.pecas.Cavalo;
import xadrez.pecas.Peao;
import xadrez.pecas.Rainha;
import xadrez.pecas.Rei;
import xadrez.pecas.Torre;

public class PartidaXadrez {
	
	private int turno;
	private Cor jogadorAtual;
	private Tabuleiro tabuleiro;
	private boolean check;
	private boolean checkMate;
	private PecaXadrez enPassantVunerabilidade;
	private PecaXadrez promocao;
	
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
	
	public PecaXadrez getEnPassantVunerabilidade() {
		return enPassantVunerabilidade;
	}
	
	public PecaXadrez getPromocao() {
		return promocao;
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
		
		PecaXadrez pecaMovida = (PecaXadrez)tabuleiro.peca(fim);
		
		// jogada especial promocao
		promocao = null;
		if (pecaMovida instanceof Peao) {
			if (pecaMovida.getCor() == Cor.BRANCO && fim.getLinha() == 0 || pecaMovida.getCor() == Cor.PRETO && fim.getLinha() == 7) {
				promocao = (PecaXadrez)tabuleiro.peca(fim);
				promocao = substituiPecaPromovida("A");
			}
		}
		
		check = (testaCheck(oponente(jogadorAtual))) ? true : false;
		
		if (testaCheckMate(oponente(jogadorAtual))) {
			checkMate = true;
		}
		else {
			proximoTurno();
		}
		
		//movimento especial en passant
		if (pecaMovida instanceof Peao && (fim.getLinha() == inicial.getLinha() - 2 || fim.getLinha() == inicial.getLinha() + 2)) {
			enPassantVunerabilidade = pecaMovida;
		}
		else {
			enPassantVunerabilidade = null;
		}
		
		return (PecaXadrez)capturaPeca;
	} 
	
	public PecaXadrez substituiPecaPromovida(String tipo) {
		if (promocao == null) {
			throw new IllegalStateException("Nao ha pecas para promover");
		}
		if (!tipo.equals("B") && !tipo.equals("A") && !tipo.equals("R") && !tipo.equals("C")) {
			throw new InvalidParameterException("Tipo invalido");
		}
		
		Posicao pos = promocao.getPosicaoXadrez().passaPosicaoXadrez();
		Peca p = tabuleiro.removePeca(pos);
		pecasNoTabuleiro.remove(p);
		
		PecaXadrez novaPeca = novaPeca(tipo, promocao.getCor());
		tabuleiro.colocaPeca(novaPeca, pos);
		pecasNoTabuleiro.add(novaPeca);
		
		return novaPeca;
	}
	
	private PecaXadrez novaPeca(String tipo, Cor cor) {
		if (tipo.equals("B")) return new Bispo(tabuleiro, cor);
		if (tipo.equals("C")) return new Cavalo(tabuleiro, cor);
		if (tipo.equals("A")) return new Rainha(tabuleiro, cor);
		return new Torre(tabuleiro, cor);
	}
	
	private Peca fazMover(Posicao inicial, Posicao fim) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removePeca(inicial);
		p.incrementaContadorMovimentos();
		Peca pecaCapturada = tabuleiro.removePeca(fim);
		tabuleiro.colocaPeca(p, fim);
		
		if (pecaCapturada != null) {
			pecasNoTabuleiro.remove(pecaCapturada);
			pecasCapturadas.add(pecaCapturada);
		}
		
		//jogada especial roque pequeno
		if (p instanceof Rei && fim.getColuna() == inicial.getColuna() + 2) {
			Posicao inicialT = new Posicao(inicial.getLinha(), inicial.getColuna() + 3);
			Posicao finalT = new Posicao(inicial.getLinha(), inicial.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(inicialT);
			tabuleiro.colocaPeca(torre, finalT);
			torre.incrementaContadorMovimentos();
		}
		//jogada especial roque grande
		if (p instanceof Rei && fim.getColuna() == inicial.getColuna() - 2) {
			Posicao inicialT = new Posicao(inicial.getLinha(), inicial.getColuna() - 4);
			Posicao finalT = new Posicao(inicial.getLinha(), inicial.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(inicialT);
			tabuleiro.colocaPeca(torre, finalT);
			torre.incrementaContadorMovimentos();
		}
		
		//jogada especial en passant
		if (p instanceof Peao) {
			if (inicial.getColuna() != fim.getColuna() && pecaCapturada == null) {
				Posicao peaoPosicao;
				if (p.getCor() == Cor.BRANCO) {
					peaoPosicao = new Posicao(fim.getLinha() + 1, fim.getColuna());
				}
				else {
					peaoPosicao = new Posicao(fim.getLinha() - 1, fim.getColuna());
				}
				pecaCapturada = tabuleiro.removePeca(peaoPosicao);
				pecasCapturadas.add(pecaCapturada);
				pecasNoTabuleiro.remove(pecaCapturada);
			}
		}
		
		return pecaCapturada;
	}
	
	private void desfazMovimento(Posicao inicial, Posicao fim, Peca pecaCapturada) {
		PecaXadrez p = (PecaXadrez)tabuleiro.removePeca(fim);
		p.decrementaContadorMovimentos();
		tabuleiro.colocaPeca(p, inicial);
		
		if (pecaCapturada != null) {
			tabuleiro.colocaPeca(pecaCapturada, fim);
			pecasCapturadas.remove(pecaCapturada);
			pecasNoTabuleiro.add(pecaCapturada);
		}
		
		//jogada especial roque pequeno
		if (p instanceof Rei && fim.getColuna() == inicial.getColuna() + 2) {
			Posicao inicialT = new Posicao(inicial.getLinha(), inicial.getColuna() + 3);
			Posicao finalT = new Posicao(inicial.getLinha(), inicial.getColuna() + 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(finalT);
			tabuleiro.colocaPeca(torre, inicialT);
			torre.decrementaContadorMovimentos();
		}
		//jogada especial roque grande
		if (p instanceof Rei && fim.getColuna() == inicial.getColuna() - 2) {
			Posicao inicialT = new Posicao(inicial.getLinha(), inicial.getColuna() - 4);
			Posicao finalT = new Posicao(inicial.getLinha(), inicial.getColuna() - 1);
			PecaXadrez torre = (PecaXadrez)tabuleiro.removePeca(finalT);
			tabuleiro.colocaPeca(torre, inicialT);
			torre.decrementaContadorMovimentos();
		}	
		
		//jogada especial en passant
		if (p instanceof Peao) {
			if (inicial.getColuna() != fim.getColuna() && pecaCapturada == enPassantVunerabilidade) {
				PecaXadrez peao = (PecaXadrez)tabuleiro.removePeca(fim);
				Posicao peaoPosicao;
				if (p.getCor() == Cor.BRANCO) {
					peaoPosicao = new Posicao(3, fim.getColuna());
				}
				else {
					peaoPosicao = new Posicao(4, fim.getColuna());
				}
				tabuleiro.colocaPeca(peao, peaoPosicao);
			}
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
		colocaNovaPeca('a', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('b', 1, new Cavalo(tabuleiro, Cor.BRANCO));		
		colocaNovaPeca('c', 1, new Bispo(tabuleiro, Cor.BRANCO));		
		colocaNovaPeca('d', 1, new Rainha(tabuleiro, Cor.BRANCO));		
		colocaNovaPeca('e', 1, new Rei(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('f', 1, new Bispo(tabuleiro, Cor.BRANCO));				
		colocaNovaPeca('g', 1, new Cavalo(tabuleiro, Cor.BRANCO));				
		colocaNovaPeca('h', 1, new Torre(tabuleiro, Cor.BRANCO));
		colocaNovaPeca('a', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('b', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('c', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('d', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('e', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('f', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('g', 2, new Peao(tabuleiro, Cor.BRANCO, this));
		colocaNovaPeca('h', 2, new Peao(tabuleiro, Cor.BRANCO, this));

		
		colocaNovaPeca('a', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('b', 8, new Cavalo(tabuleiro, Cor.PRETO));		
		colocaNovaPeca('c', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('d', 8, new Rainha(tabuleiro, Cor.PRETO));		
		colocaNovaPeca('e', 8, new Rei(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('f', 8, new Bispo(tabuleiro, Cor.PRETO));
		colocaNovaPeca('g', 8, new Cavalo(tabuleiro, Cor.PRETO));				
		colocaNovaPeca('h', 8, new Torre(tabuleiro, Cor.PRETO));
		colocaNovaPeca('a', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('b', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('c', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('d', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('e', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('f', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('g', 7, new Peao(tabuleiro, Cor.PRETO, this));
		colocaNovaPeca('h', 7, new Peao(tabuleiro, Cor.PRETO, this));

	}

}
