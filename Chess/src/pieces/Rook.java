package pieces;

import game.*;

public class Rook extends Piece {

	private boolean canCastle;
	
	public Rook() {
		super(5);
		game = null;
		board = null;
		x = 0;
		y = 0;
		color = null;
		canCastle = true;
	}
	
	public Rook(int x, int y, Color color, Game game) {
		super(5);
		this.board = game.getBoard();
		this.x = x;
		this.y = y;
		this.color = color;
		this.game = game;
		canCastle = true;
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
		//for castling: check if there is a piece that even has a potential move (on the other team) for any of the
		// squares involved with the castling (inclusive)
		
		//check if the move is out of bounds, or if it is moving diagonally, or if move has same pos as the piece
		if(!move.getValidity() || (move.getX() != move.getPiece().getX() &&
				move.getY() != move.getPiece().getY())) {
			return false;
		}
		
		//see if the rook is moving horizontal or vertical
		if(move.getX() != move.getPiece().getX()) { //horizontal movement
			//if move x is greater than piece x, then for loop must iterate forward, and vice versa
			int iterateNum = move.getX() > move.getPiece().getX() ? 1 : -1;
			
			for(int i = move.getPiece().getX() + iterateNum; i != move.getX() + iterateNum; i += iterateNum) {
				if(board[i][move.getPiece().getY()] != null && board[i][move.getPiece().getY()].getColor() == this.color) {
					return false;
				}
				//enemy piece comes before intended movement? Can't happen.
				if(i != move.getX() && board[i][move.getPiece().getY()] != null && board[i][move.getPiece().getY()].getColor() != this.color) {
					return false;
				}
			}
		}
		else if (move.getY() != move.getPiece().getY()){ //vertical movement
			//if move y is greater than piece y, then for loop must iterate forward, and vice versa
			int iterateNum = move.getY() > move.getPiece().getY() ? 1 : -1;
			
			for(int i = move.getPiece().getY() + iterateNum; i != move.getY() + iterateNum; i += iterateNum) {
				if(board[move.getPiece().getX()][i] != null && board[move.getPiece().getX()][i].getColor() == this.color) {
					return false;
				}
				//enemy piece comes before intended movement? Can't happen.
				if(i != move.getY() && board[move.getPiece().getX()][i] != null && board[move.getPiece().getX()][i].getColor() != this.color) {
					return false;
				}
			}
		}
		if(isPrimaryPiece && putsMeInCheck(move)) {
			return false;
		}

		return true;
	}
	
	@Override
	public boolean move(int newX, int newY) {
		if(isMoveValid(new Move(x, y, newX, newY, this), true)) { 
			board[x][y] = null;
			this.setX(newX);
			this.setY(newY);
			board[x][y] = this;
			if(canCastle) canCastle = false;
			return true;
		}

		return false;
	}
	
	public boolean getCanCastle() {
		return canCastle;
	}

}
