package pieces;

import game.Game;
import game.Move;

public class Bishop extends Piece {

	public Bishop() {
		super(3);
		game = null;
		board = null;
		x = 0;
		y = 0;
		color = null;
	}

	public Bishop(int x, int y, Color color, Game game) {
		super(3);
		this.board = game.getBoard();
		this.x = x;
		this.y = y;
		this.color = color;
		this.game = game;
	}

	/**
	 * Verifies the validity of a move that is provided.
	 * @return if a move for a rook is valid and possible.
	 */
	@Override
	public boolean isMoveValid(Move move, boolean isPrimaryPiece) {
		//check if a friendly piece is in the path of the move X
		//check if an enemy piece is in the path of the move X
		//check if the move is out of bounds                         X
		//check if the move puts your own king in check        X

		//check if the move is out of bounds or if move has same pos as the piece
		if(!move.getValidity()) return false;

		//is this path even plausible for a bishop
		if(Math.abs(Math.abs(move.getX() - x) - Math.abs(move.getY() - y)) != 0 || 
				move.getX() == x || move.getY() == y) {
			return false;
		}
		
		//if move x is greater than piece x, then for loop must iterate forward, and vice versa
		int iterateNumX = 0;
		if(move.getX() > move.getPiece().getX()) iterateNumX = 1;
		else if (move.getX() < move.getPiece().getX()) iterateNumX = -1;

		int iterateNumY = 0;
		if(move.getY() > move.getPiece().getY()) iterateNumY = 1;
		else if (move.getY() < move.getPiece().getY()) iterateNumY = -1;
		
		for(int i = move.getPiece().getX() + iterateNumX, k = move.getPiece().getY() + iterateNumY; 
				i != move.getX() + iterateNumX; i += iterateNumX, k += iterateNumY) {
			if(board[i][k] != null && board[i][k].getColor() == this.color) {
				return false;
			}
			//enemy piece comes before intended movement? Can't happen.
			if(i != move.getX() && board[i][k] != null && board[i][k].getColor() != this.color) {
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
