package game;

import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

import pieces.*;

/**
 * Hosts all of the Piece data on a single board. This class dictates the wider scope of the game.
 * @author Jeffrey A. Camp
 *
 */
public class Game {

	/*
	 * TODO list
	 * 
	 * 1. Implement material count.
	 * 2. Pawn turns into ANY other piece when getting to the end (other than queen)
	 */
	private boolean gameOver;
	private boolean whiteInCheck;
	private boolean blackInCheck;
	private Piece[][] board;
	private Color turnColor;
	private Color otherColor;
	private King whiteKing;
	private King blackKing;
	private Stack<Move> gameMoves;
	private PriorityQueue<Piece> whiteCapturedPieces;
	private PriorityQueue<Piece> blackCapturedPieces;


	//every turn, both colors should change ******
	public Game() {
		board = new Piece[8][8];
		turnColor = Color.WHITE;
		otherColor = Color.BLACK;
		whiteCapturedPieces = new PriorityQueue<Piece>(new SortPiece());
		blackCapturedPieces = new PriorityQueue<Piece>(new SortPiece());
		gameMoves = new Stack<Move>();
		gameOver = false;
	}

	/**
	 * Copy constructor
	 * @param game
	 */
	public Game(Game existingGame) {
		gameOver = false;
		this.turnColor = existingGame.turnColor;
		this.otherColor = existingGame.otherColor;

		gameMoves = existingGame.gameMoves;

		board = new Piece[8][8];

		//iterate through the entire board and deep copy every piece
		//TODO: 1. pawn double move 2. rook castling boolean 3. special pawn attributes (which direction to move)
		for(int i = 0; i < 8; i++) {
			for(int k = 0; k < 8; k++) {
				if(existingGame.getBoard()[i][k] != null) {
					try {
						Class c = existingGame.getBoard()[i][k].getClass();
						board[i][k] = (Piece) c.newInstance();
						board[i][k].setX(existingGame.getBoard()[i][k].getX());
						board[i][k].setY(existingGame.getBoard()[i][k].getY());
						board[i][k].setGame(this);
						board[i][k].setBoard(this.board);
						board[i][k].setColor(existingGame.getBoard()[i][k].getColor());

						if(c == King.class && board[i][k].getColor() == Color.WHITE) {
							whiteKing = (King) board[i][k];
						}
						else if(c == King.class && board[i][k].getColor() == Color.BLACK) {
							blackKing = (King) board[i][k];
						}

					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	/**
	 * Moves a selected piece on the board.
	 * @param x
	 * @param y
	 * @param newX
	 * @param newY
	 * @return if the move could be completed.
	 */
	public boolean move(int x, int y, int newX, int newY) {
		if(board[y][x] != null && board[y][x].getColor() == turnColor && 
				board[y][x].move(newY, newX)) {
			Color temp = turnColor;
			turnColor = otherColor;
			otherColor = temp;
			if(isKingInCheck(Color.WHITE)) whiteInCheck = true;
			if(isKingInCheck(Color.BLACK)) blackInCheck = true;
			gameMoves.add(new Move(y, x, newY, newX, board[newY][newX]));
			return true;
		}

		return false;
	}

	public void turns() {
		if(isKingInCheck(Color.BLACK)) blackInCheck = true;
		if(isKingInCheck(Color.WHITE)) whiteInCheck = true;
		
		while(!gameOver) {

			if(turnColor == Color.WHITE) printBoard();
			else printBoardFlip();
						
			if(otherColorVictory()) {
				System.out.println(otherColor + " has won by checkmate.");
				gameOver = true;
			}
			else if(stalemate()) {
				System.out.println("The game is drawn by stalemate.");
				gameOver = true;
			}
			else {
				boolean validMove = inputMove(true);
				while(!validMove) {
					validMove = inputMove(false);
				}
			}
		}
	}

	public void startGame() {
		System.out.println("Move Input Format: no commas or spaces needed! \n original x, original y, new x, new y example:\n\"1224\"");
		turns();
	}

	public boolean inputMove(boolean firstTry) {
		if(firstTry) System.out.println(Color.getName(turnColor) + ", input your move!");
		if(!firstTry) System.out.println(Color.getName(turnColor) + ", that input was not valid. Try again.");
		Scanner scnr = new Scanner(System.in);
		String move = scnr.nextLine();

		if(move.length() == 4) {
			int x = 0;
			int y = 0;
			int newY = 0;
			int newX = 0;

			for(int i = 0; i < move.length(); i++) {
				if(i == 0) x = Character.getNumericValue(move.charAt(0));
				else if(i == 1) y = Character.getNumericValue(move.charAt(1));
				else if(i == 2) newX = Character.getNumericValue(move.charAt(2));
				else if(i == 3) newY = Character.getNumericValue(move.charAt(3));
			}

			if(move(x, y, newX, newY)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * A movement that moves no matter what... NOT INTENDED FOR GAMEPLAY
	 * @param m
	 */
	public void authMove(Move m) {
		if(m.getX() == -100 && m.getY() == -100) {
			board[m.getPreviousX()][m.getPreviousY()] = null;
		}
		else {
			board[m.getX()][m.getY()] = board[m.getPiece().getX()][m.getPiece().getY()];
			board[m.getPiece().getX()][m.getPiece().getY()] = null;
			board[m.getX()][m.getY()].setX(m.getX());
			board[m.getX()][m.getY()].setY(m.getY());
			Color temp = otherColor;
			otherColor = turnColor;
			otherColor = temp;
		}
	}

	/**
	 * Returns a piece on the board given an x and y coordinates. If a piece is not at the 
	 * provided position, null is returned.
	 * @param x
	 * @param y
	 * @return piece
	 */
	public Piece findPiece(int x, int y) {
		Piece piece = null;

		try {
			piece = board[x][y];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Recall that a chess board is an 8x8 square... try again");
		}

		return piece;
	}

	/**
	 * Initializes a normal chess game.
	 */
	public void initializeBoard() {
		blackKing = new King(0, 4, Color.BLACK, this);
		board[0][0] = new Rook(0, 0, Color.BLACK, this);
		board[0][1] = new Knight(0, 1, Color.BLACK, this);
		board[0][2] = new Bishop(0, 2, Color.BLACK, this);
		board[0][3] = new Queen(0, 3, Color.BLACK, this);
		board[0][4] = blackKing;
		board[0][5] = new Bishop(0, 5, Color.BLACK, this);
		board[0][6] = new Knight(0, 6, Color.BLACK, this);
		board[0][7] = new Rook(0, 7, Color.BLACK, this);

		board[1][0] = new Pawn(1, 0, Color.BLACK, this);
		board[1][1] = new Pawn(1, 1, Color.BLACK, this);
		board[1][2] = new Pawn(1, 2, Color.BLACK, this);
		board[1][3] = new Pawn(1, 3, Color.BLACK, this);
		board[1][4] = new Pawn(1, 4, Color.BLACK, this);
		board[1][5] = new Pawn(1, 5, Color.BLACK, this);
		board[1][6] = new Pawn(1, 6, Color.BLACK, this);
		board[1][7] = new Pawn(1, 7, Color.BLACK, this);

		whiteKing = new King(7, 3, Color.WHITE, this);
		board[7][0] = new Rook(7, 0, Color.WHITE, this);
		board[7][1] = new Knight(7, 1, Color.WHITE, this);
		board[7][2] = new Bishop(7, 2, Color.WHITE, this);
		board[7][3] = whiteKing;
		board[7][4] = new Queen(7, 4, Color.WHITE, this);
		board[7][5] = new Bishop(7, 5, Color.WHITE, this);
		board[7][6] = new Knight(7, 6, Color.WHITE, this);
		board[7][7] = new Rook(7, 7, Color.WHITE, this);

		board[6][0] = new Pawn(6, 0, Color.WHITE, this);
		board[6][1] = new Pawn(6, 1, Color.WHITE, this);
		board[6][2] = new Pawn(6, 2, Color.WHITE, this);
		board[6][3] = new Pawn(6, 3, Color.WHITE, this);
		board[6][4] = new Pawn(6, 4, Color.WHITE, this);
		board[6][5] = new Pawn(6, 5, Color.WHITE, this);
		board[6][6] = new Pawn(6, 6, Color.WHITE, this);
		board[6][7] = new Pawn(6, 7, Color.WHITE, this);
	}

	public void customDevInitializeBoard() {
		blackKing = new King(0, 4, Color.BLACK, this);
		board[6][2] = new Rook(6, 2, Color.BLACK, this);
		board[0][1] = new Knight(0, 1, Color.BLACK, this);
		board[0][2] = new Bishop(0, 2, Color.BLACK, this);
		board[0][3] = new Queen(0, 3, Color.BLACK, this);
		board[0][4] = blackKing;
		board[0][5] = new Bishop(0, 5, Color.BLACK, this);
		board[0][6] = new Knight(0, 6, Color.BLACK, this);
		board[6][4] = new Rook(6, 4, Color.BLACK, this);

		whiteKing = new King(7, 3, Color.WHITE, this);
		board[7][3] = whiteKing;
	}


	/**
	 * Checks if one player is in check.
	 * @param color
	 * @return
	 */
	public boolean isKingInCheck(Color color) {
		Piece king = color == Color.WHITE ? whiteKing : blackKing;

		for(int i = 0; i < 8; i++) {
			for(int k = 0; k < 8; k++) {
				if(board[i][k] != null && board[i][k].getColor() != color) {
					for(Move m : board[i][k].availableMoves()) {
						if(m.getX() == king.getX() && m.getY() == king.getY()) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public Vector<Move> getPossibleColorMoves(Color color) {
		Vector<Move> moves = new Vector<Move>();

		for(int i = 0; i < board.length; i++) {
			for(int k = 0; k < board.length; k++) {
				if(board[i][k] != null && board[i][k].getColor() == color) {
					Vector<Move> availableMoves = board[i][k].availableMoves();
					if(availableMoves != null || availableMoves.size() != 0) {
						for(Move m : availableMoves) {
							moves.add(m);
						}
					}
				}
			}
		}

		return moves;
	}

	//check if the color who has a turn is in check, and if so, if they have any way to escape. 
	//If not the other guys win
	public boolean otherColorVictory() {
		if(turnColor == Color.WHITE ? whiteInCheck : blackInCheck &&
				getPossibleColorMoves(turnColor).size() == 0) {
			return true;
		}
		return false;
	}

	public boolean stalemate() {
		if(getPossibleColorMoves(Color.WHITE).size() == 0 && !whiteInCheck && !blackInCheck) {
			return true;
		}
		else if(getPossibleColorMoves(Color.BLACK).size() == 0 && !whiteInCheck && !blackInCheck) {
			return true;
		}
		return false;
	}

	public void printBoard() {
		System.out.println("       0          1          2          3          4          5          6          7");
		for(int i = 0; i < 9; i++) {
			if(i < 8) {
				System.out.print(i + " ");
				for(int k = 0; k < board.length; k++) {
					System.out.print(board[i][k] != null ? board[i][k].toString() : "     -     ");
				}
				System.out.print(i + " ");
			}
			else {
				System.out.println("       0          1          2          3          4          5          6          7");
			}
			System.out.println();
		}
		System.out.println("\n");
	}
	
	public void printBoardFlip() {
		System.out.println("       0          1          2          3          4          5          6          7");
		for(int i = 7; i > -2; i--) {
			if(i > -1) {
				System.out.print(i + " ");
				for(int k = 0; k < board.length; k++) {
					System.out.print(board[i][k] != null ? board[i][k].toString() : "     -     ");
				}
				System.out.print(i + " ");
			}
			else {
				System.out.println("       0          1          2          3          4          5          6          7");
			}
			System.out.println();
		}
		System.out.println("\n");
	}

	public Piece[][] getBoard() {
		return board;
	}

	public Color getTurnColor() {
		return turnColor;
	}

	public King getKing(Color kingColor) {
		return kingColor == Color.WHITE ? whiteKing : blackKing;
	}

	/**
	 * Retrieves the stack which stores all game moves.
	 * @return gameMoves
	 */
	public Stack<Move> getGameMoves() {
		return gameMoves;
	}
	
	/**
	 * Prints all possible moves in a readable manner. For debugging purposes.
	 */
	public void crazyTest() {
		for(int i = 0; i < board.length; i++) {
			for(int k = 0; k < board.length; k++) {
				if(board[i][k] != null) {
					for(Move m : board[i][k].availableMoves())
					System.out.println(m.getPiece().toString() + "Orig X: " + m.getPreviousY() +
					", Orig Y: " + m.getPreviousX() + " new X: " + m.getY() + ", new Y: " + m.getX());
				}
			}
		}
		
	}
	
	public Move algebraicNotationInput() {
		return null;
	}

	public boolean addMaterial(Piece piece) {
		PriorityQueue<Piece> q = piece.getColor() == Color.WHITE ? blackCapturedPieces : whiteCapturedPieces;
		if(q.contains(piece)) {
			return false;
		}
		q.add(piece);
		return true;
	}
}
