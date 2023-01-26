package pieces;

import game.Game;
import game.Move;

public class Queen extends Piece {

	public Queen() {
		super(9);
		game = null;
		board = null;
		x = 0;
		y = 0;
		color = null;
	}

	public Queen(int x, int y, Color color, Game game) {
		super(9);
		this.board = game.getBoard();
		this.x = x;
		this.y = y;
		this.color = color;
		this.game = game;
	}

	/**
	 * Verifies the validity of a move that is provided.
	 * @return if a move for a queen is valid and possible.
	 */
	@Override
	public boolean isMoveValid(Move move, boolean isPrimaryPiece) {
		//check if a friendly piece is in the path of the move X
		//check if an enemy piece is in the path of the move X
		//check if the move is out of bounds                         X
		//check if the move puts your own king in check        X

		//check if the move is out of bounds or if move has same pos as the piece
		if(!move.getValidity()) return false;

		//is the path plausible considering how a queen really moves?
		//not both increasing... but if they are... they are both the same 
		//absolute value disparity from original coords
		if((Math.abs(move.getX() - x) != 0 && Math.abs(move.getY() - y) != 0) && 
				!((	(Math.abs(move.getX() - x)) == (Math.abs(move.getY() - y))))) {
			return false;
		}
		
		//if move x is greater than piece x, then for loop must iterate forward, and vice versa
		int iterateNumX = 0, iterateNumY = 0;
		if(move.getX() > move.getPiece().getX()) {
			iterateNumX = 1;
		}
		else if(move.getX() < move.getPiece().getX()) {
			iterateNumX = -1;
		}

		if(move.getY() > move.getPiece().getY()) {
			iterateNumY = 1;
		}
		else if(move.getY() < move.getPiece().getY()) {
			iterateNumY = -1;
		}

		for(int i = move.getPiece().getX() + iterateNumX, k = move.getPiece().getY() + iterateNumY; 
				i != move.getX() + iterateNumX || k != move.getY() + iterateNumY; i += iterateNumX, k += iterateNumY) {
			if(board[i][k] != null && board[i][k].getColor() == this.color) {
				return false;
			}
			//enemy piece comes before intended movement? Can't happen.
			if((i != move.getX() || k != move.getY()) && board[i][k] != null && board[i][k].getColor() != this.color) {
				return false;
			}
		}
		//if it isn't a primary piece, it doesn't matter if it puts itself in check or not, since it's turn is in the future
		if(isPrimaryPiece && putsMeInCheck(move)) {
			return false;
		}

		return true;
	}
}
