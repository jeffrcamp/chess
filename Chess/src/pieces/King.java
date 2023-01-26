package pieces;

import game.Game;
import game.Move;

public class King extends Piece {

	private boolean canCastle;

	public King() {
		super(69);
		game = null;
		board = null;
		x = 0;
		y = 0;
		color = null;
		canCastle = true;
	}

	public King(int x, int y, Color color, Game game) {
		super(69);
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
		//check if a friendly piece is in the path of the move 
		//check if an enemy piece is in the path of the move 
		//check if the move is out of bounds                         X
		//check if the move puts your own king in check        X

		//check if the move is out of bounds or if move has same pos as the piece
		if(!move.getValidity()) return false;

		//special case for castling
		if( isPrimaryPiece && ( (move.getX() == 7 && move.getY() == 7) || (move.getX() == 7 && move.getY() == 0) || 
				(move.getX() == 0 && move.getY() == 7) || (move.getX() == 0 && move.getY() == 0) ) &&
				board[move.getX()][move.getY()] != null && (board[move.getX()][move.getY()].getClass() == Rook.class) &&
				((Rook)(board[move.getX()][move.getY()])).getCanCastle() && canCastle) {

			//see if board is clear between rook and king
			int iterateNum = move.getY() - y > 0 ? 1 : -1;
			for(int z = y + iterateNum; z < board.length - iterateNum && z > (-1) - iterateNum; z += iterateNum) {
				if(board[x][z] != null) {
					return false;
				}
			}
			
			for(int i = 0; i < board.length; i++) {
				for(int k = 0; k < board.length; k++) {
					if(board[i][k] != null && board[i][k].getColor() != color) {
						for(Move m : board[i][k].availableMoves()) {
							//make sure none of the squares involved in castling can be hit at all
							if(m.getX() == move.getX()) {
								if(move.getY() > y && (m.getY() >= y)) {
									return false;
								}
								else if(move.getY() < y && (m.getY() <= y)) {
									return false;
								}
							}
						}
					}
				}
			}

			int yMove = 2;
			int rookYMove = 1;
			if(move.getY() < y) {
				yMove = -2;
				rookYMove = -1;
			}
			
			if(putsMeInCheck(new Move(x, y, x, y + yMove, this), new Move(x, y, x, y + rookYMove, board[move.getX()][move.getY()])) ) {
				return false;
			}

			return true;
		}
		if(Math.abs(move.getX() - move.getPiece().getX()) > 1 || Math.abs(move.getY() - move.getPiece().getY()) > 1) {
			return false;
		}
		if(move.getX() == move.getPiece().getX() && move.getPiece().getY() == move.getY()) return false;
		if(board[move.getX()][move.getY()] != null && board[move.getX()][move.getY()].getColor() == this.color) {
			return false;
		}

		//if it isn't a primary piece, it doesn't matter if it puts itself in check or not, since it's turn is in the future
		// AKA if it is a secondary piece... and the piece actually moving puts itself in check... it won't 
		// matter if the secondary piece is in check, it'll just kill the king of the guy who just moved
		if(isPrimaryPiece && putsMeInCheck(move)) {
			return false;
		}

		return true;
	}

	@Override
	public boolean move(int newX, int newY) {
		if(isMoveValid(new Move(x, y, newX, newY, this), true)) { 
			if(board[newX][newY] != null && board[newX][newY].getClass() == Rook.class && 
					board[newX][newY].getColor() == color) {
				if(newY < y) {
					board[x][y - 2] = this;
					board[x][y - 1] = board[newX][newY];
					board[x][y] = null;
					board[newX][newY] = null;
					board[x][y - 1].setY(y - 1);
					this.setY(newY - 2);
				}
				else {
					board[x][y +2] = this;
					board[x][y + 1] = board[newX][newY];
					board[x][y] = null;
					board[newX][newY] = null;
					board[x][y + 1].setY(y + 1);
					this.setY(newY + 2);
				}
			}
			else {
				board[x][y] = null;
				this.setX(newX);
				this.setY(newY);
				board[x][y] = this;
			}
			if(canCastle) canCastle = false;
			return true;
		}

		return false;
	}

}
