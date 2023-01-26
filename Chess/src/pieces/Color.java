package pieces;

public enum Color {
	BLACK, WHITE;
	
	public static String getName(Color color) {
		return color == BLACK ? "Black" : "White" ;
	}
}
