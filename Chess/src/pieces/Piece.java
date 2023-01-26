package pieces;

import java.util.Comparator;
import java.util.Vector;

import game.Game;
import game.Move;

/*
public interface Piece {
	public boolean move(int newX, int newY);
	public Vector<Move> availableMoves();
	public boolean isMoveValid(Move move, boolean isPrimaryPiece);
	public int getX();
	public int getY();
	public Color getColor();
	public void setX(int newX);
	public void setY(int newY);
	public void setGame(Game newGame);
	public void setColor(Color newColor);
	public void setBoard(Piece[][] board);
}
 */



public abstract class Piece {

	protected int x;

	protected int y;

	protected Color color;

	protected Piece[][] board;

	protected Game game;
	
	protected final int materialWorth;

	public Piece(int materialWorth) {
		this.materialWorth = materialWorth;
	}
	
	public boolean move(int newX, int newY) {
		Move m = new Move(x, y, newX, newY, this);
		if(isMoveValid(m, true)) {
			if(board[x][y] != null) {
				game.addMaterial(board[x][y]);
			}
			board[x][y] = null;
			this.setX(newX);
			this.setY(newY);
			board[x][y] = this;
			game.getGameMoves().add(m);
			return true;
		}

		return false;
	}

	public Vector<Move> availableMoves() {
		Vector<Move> vect = new Vector<Move>();

		for(int i = 0; i < board.length; i++) {
			for(int k = 0; k < board.length; k++) {
				Move m = new Move(x, y, i, k, this);
				//if the piece has the current turnColor, it should be considered a primary piece, since it shouldn't be
				//available to put yourself in check.
				if(game.getTurnColor() == color && isMoveValid(m, true)) {
					vect.add(m);
				}
				else if(game.getTurnColor() != color && isMoveValid(m, false)) {
					vect.add(m);
				}
			}
		}

		return vect;
	}

	public abstract boolean isMoveValid(Move move, boolean isPrimaryPiece);

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public void setX(int newX) {
		x = newX;
	}

	public void setY(int newY) {
		y = newY;
	}

	public void setGame(Game newGame) {
		game = newGame;
	}

	public void setColor(Color newColor) {
		color = newColor;
	}

	public void setBoard(Piece[][] board) {
		this.board = board;
	}
	
	protected boolean putsMeInCheck(Move move) {
		Game g = new Game(game);
		
		g.authMove(move);
		
		for(int i = 0; i < board.length; i++) {
			for(int k = 0; k < board.length; k++) {
				if(g.getBoard()[i][k] != null && g.getBoard()[i][k].getColor() == (game.getTurnColor() == Color.WHITE ? Color.BLACK : Color.WHITE)) {
					for(Move m : g.getBoard()[i][k].availableMoves()) {
						if(m.getX() == g.getKing(g.getTurnColor()).getX() && 
								m.getY() == g.getKing(g.getTurnColor()).getY()) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	protected boolean putsMeInCheck(Move kingMove, Move rookMove) {
		Game g = new Game(game);
		
		g.authMove(kingMove);
		g.authMove(rookMove);
		
		for(int i = 0; i < board.length; i++) {
			for(int k = 0; k < board.length; k++) {
				if(g.getBoard()[i][k] != null && g.getBoard()[i][k].getColor() == (game.getTurnColor() == Color.WHITE ? Color.BLACK : Color.WHITE)) {
					for(Move m : g.getBoard()[i][k].availableMoves()) {
						return m.getX() == g.getKing(g.getTurnColor()).getX() && 
								m.getY() == g.getKing(g.getTurnColor()).getY() ? true : false;
					}
				}
			}
		}
		
		return false;
	}
	
	public String toString() {
		if(this.getClass() == Pawn.class) {
			return "   " + Color.getName(color).charAt(0) + "Pawn   ";
		}
		else if(this.getClass() == King.class) {
			return "   " + Color.getName(color).charAt(0) + "King   ";
		}
		else if(this.getClass() == Rook.class) {
			return "   " + Color.getName(color).charAt(0) + "Rook   ";
		}
		else if(this.getClass() == Queen.class) {
			return "   " + Color.getName(color).charAt(0) + "Queen  ";
		}
		else if(this.getClass() == Bishop.class) {
			return "  " + Color.getName(color).charAt(0) + "Bishop  ";
		}
		else if(this.getClass() == Knight.class) {
			return "  " + Color.getName(color).charAt(0) + "Knight  ";
		}
		return "  " + Color.getName(color).charAt(0) + this.getClass().getSimpleName() + "  ";
	}
	
	public int getMaterialWorth() {
		return materialWorth;
	}

}