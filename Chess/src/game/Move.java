package game;

import pieces.*;

public class Move {
	private int x;
	private int y;
	private int previousX;
	private int previousY;
	private Piece p;
	private boolean moveValidity;
	
	public Move(int previousX, int previousY, int x, int y, Piece p) {
		this.previousX = previousX;
		this.previousY = previousY;
		this.x = x;
		this.y = y;
		this.p = p;
		moveValidity = (p.getX() == x && p.getY() == y) || x > 7 || x < 0 || y > 7 || y < 0 ? false : true;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getPreviousX() {
		return previousX;
	}
	
	public int getPreviousY() {
		return previousY;
	}
	
	public Piece getPiece() {
		return p;
	}
	
	public boolean getValidity() {
		return moveValidity;
	}
}
