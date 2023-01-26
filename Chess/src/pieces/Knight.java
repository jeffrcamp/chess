package pieces;

import game.Game;
import game.Move;

public class Knight extends Piece {

	public Knight() {
		super(3);
		game = null;
		board = null;
		x = 0;
		y = 0;
		color = null;
	}

	public Knight(int x, int y, Color color, Game game) {
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

		if(Math.abs(move.getX() - move.getPiece().getX()) > 2 || Math.abs(move.getX() - move.getPiece().getX()) < 1) {
			return false;
		}
		if(Math.abs(move.getY() - move.getPiece().getY()) > 2 || Math.abs(move.getY() - move.getPiece().getY()) < 1) {
			return false;
		}
		if(Math.abs(Math.abs(move.getY() - y) - Math.abs(move.getX() - x)) != 1) {
			return false;
		}
		if(board[move.getX()][move.getY()] != null && board[move.getX()][move.getY()].getColor() == color) {
			return false;
		}
		
		//if it isn't a primary piece, it doesn't matter if it puts itself in check or not, since it's turn is in the future
		if(isPrimaryPiece && putsMeInCheck(move)) {
			return false;
		}

		return true;
	}

}
