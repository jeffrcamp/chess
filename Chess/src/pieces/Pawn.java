package pieces;

import game.Game;
import game.Move;

public class Pawn extends Piece {

	private boolean doingEnPassant = false;
	private boolean canDoubleMove;
	private Pawn enPassantPiece = null;

	public Pawn() {
		super(1);
		game = null;
		board = null;
		x = 0;
		y = 0;
		color = null;
		canDoubleMove = true;
	}

	public Pawn(int x, int y, Color color, Game game) {
		super(1);
		this.board = game.getBoard();
		this.x = x;
		this.y = y;
		this.color = color;
		this.game = game;
		canDoubleMove = true;
	}

	/**
	 * Verifies the validity of a move that is provided.
	 * @return if a move for a rook is valid and possible.
	 */
	@Override //STILL NEED TO INTEGRATE EN PASSANT AND TURNING INTO OTHER PIECES
	public boolean isMoveValid(Move move, boolean isPrimaryPiece) {
		//check if a friendly piece is in the path of the move X
		//check if an enemy piece is in the path of the move X
		//check if the move is out of bounds                         X
		//check if the move puts your own king in check        X

		boolean movingUpNumerically = move.getX() - x > 0 ? true : false;
		//en passant. Must meet strict requirements. 
		if(isPrimaryPiece && ((move.getY() == y + 1 || move.getY() == y - 1) && (move.getX() == x - 1 || move.getX() == x + 1)) && 
				board[move.getX()][move.getY()] == null && 
				//determine if pawn that you are moving past just moved two... but need to figure out
				// if pawn would be above or below the move numerically...
						((movingUpNumerically && board[move.getX() - 1][move.getY()] != null) ||
								(!movingUpNumerically && board[move.getX() + 1][move.getY()] != null)) &&
				((movingUpNumerically && board[move.getX() - 1][move.getY()].getClass() == Pawn.class) ||
						(!movingUpNumerically && board[move.getX() + 1][move.getY()].getClass() == Pawn.class)) &&
				(game.getGameMoves().size() > 0 && ((movingUpNumerically && board[move.getX() - 1][move.getY()] == game.getGameMoves().peek().getPiece()) ||
						(!movingUpNumerically && board[move.getX() + 1][move.getY()] == game.getGameMoves().peek().getPiece()))) &&
				(game.getGameMoves().size() > 0 && Math.abs(game.getGameMoves().peek().getX() - game.getGameMoves().peek().getPreviousX()) == 2)) {
			int xCoord = movingUpNumerically ? move.getX() - 1 : move.getX() + 1 ;
			if(!putsMeInCheck(move, new Move(xCoord, move.getY(), -100, -100, board[xCoord][move.getY()]))) {
				enPassantPiece = (Pawn) board[xCoord][move.getY()];
				doingEnPassant = true;
				return true;
			}
			return false;
		}
		
		//check if the move is out of bounds, or if it is moving diagonally, or if move has same pos as the piece
		//1. check out of bounds 2 & 3. check it isn't going backwards 4. check that the square it is going to isn't occupied
		//5 and 6. check it moves not too far forward 7, 8, 9. check that doubleMoves work as intended 
		//9. check that it can't take it's own color
		if(!move.getValidity() || (color == Color.BLACK && move.getX() <= move.getPiece().getX()) || 
				(color == Color.WHITE && move.getX() >= move.getPiece().getX()) ||
				((board[move.getX()][move.getPiece().getY()] != null) && move.getY() == y) || 
				(Math.abs(move.getPiece().getY() - move.getY()) > 1) || 
				Math.abs(move.getPiece().getX() - move.getX()) > 2 ||
				(Math.abs(move.getPiece().getX() - move.getX()) == 2 && canDoubleMove == false) ||
				(Math.abs(move.getPiece().getX() - move.getX()) == 2 && move.getPiece().getY() != move.getY()) ||
				(Math.abs(move.getPiece().getX() - move.getX()) == 2 && board[move.getX() - 1][move.getY()] != null) ||
				(move.getY() != move.getPiece().getY()	&& board[move.getX()][move.getY()] != null
				&& board[move.getX()][move.getY()].getColor() == color) || ( ((move.getY() == y + 1 ||
				move.getY() == y - 1) && (move.getX() == x - 1 || move.getX() == x + 1)) && 
					board[move.getX()][move.getY()] == null) ) {
			return false;
		}

		//if it isn't a primary piece, it doesn't matter if it puts itself in check or not, since it's turn is in the future
		if(isPrimaryPiece && putsMeInCheck(move)) {
			return false;
		}

		return true;
	}
	
	@Override
	public boolean move(int newX, int newY) {
		if(isMoveValid(new Move(x, y, newX, newY, this), true)) { 
			if(doingEnPassant) {
				board[x][y] = null;
				this.setX(newX);
				this.setY(newY);
				if(x != 7 || x != 0) board[x][y] = this;
				else {
					Queen q = new Queen(x, y, color, game);
					board[x][y] = q;
					q.setBoard(board);
				}
				if(canDoubleMove) canDoubleMove = false;
				board[enPassantPiece.getX()][enPassantPiece.getY()] = null;
				doingEnPassant = false;
				enPassantPiece = null;
				return true;
			}
			board[x][y] = null;
			this.setX(newX);
			this.setY(newY);
			if(x != 7 || x != 0) board[x][y] = this;
			else {
				Queen q = new Queen(x, y, color, game);
				board[x][y] = q;
				q.setBoard(board);
			}
			if(canDoubleMove) canDoubleMove = false;
			return true;
		}

		return false;
	}

}
