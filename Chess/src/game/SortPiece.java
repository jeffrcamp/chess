package game;

import java.util.Comparator;
import pieces.Piece;

public class SortPiece implements Comparator<Piece>{

	@Override
	public int compare(Piece p1, Piece p2) {
		if(p1.getMaterialWorth() > p2.getMaterialWorth()) return 1;
		else if(p1.getMaterialWorth() < p2.getMaterialWorth()) return -1;
		
		if(p1.getClass() == p2.getClass()) return 0;
		
		else if((int)p1.getClass().getSimpleName().charAt(0) < (int)p2.getClass().getSimpleName().charAt(0)) return 1;
		else if((int)p1.getClass().getSimpleName().charAt(0) > (int)p2.getClass().getSimpleName().charAt(0)) return -1;
		
		return 0;
	}

}
