import java.awt.Point;

public class Pair implements Comparable<Pair> {

    private Board board;
    private Tile t1;
    private Tile t2;

	public Pair(Board board, Tile t1, Tile t2){
		this.board = board;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public Pair(Pair pair){
		this.board = pair.board;
		this.t1 = new Tile(t1);
		this.t2 = new Tile(t2);
	}
	
	public Tile getFirst(){
		return t1;
	}
	
	public Tile getSecond(){
		return t2;
	}
	
	public Board getBoard(){
		return board;
	}
	
	public boolean isStrongly1Combinable(){
		return t1.getX() == t2.getX() && (t1.getY() == t2.getY() || t1.getY() + 1 == t2.getY() || t2.getY() + 1 == t1.getY()) || t1.getY() == t2.getY() && (t1.getX() + 1 == t2.getX() || t2.getX() + 1 == t1.getX());
	}

	public int getType(){
		return t1.getType();
	}
	
	public boolean isWeakly1Combinable(){
		if (t1.getX() == t2.getX()){
			if (t1.getY() < t2.getY()){
				for (int y = t1.getY() + 1; y <= t2.getY() - 1; y++){
					if (board.get(t1.getX(), y).isNonEmpty()){
						return false;
					}
				}
				return true;
			} else {
				for (int y = t2.getY() + 1; y <= t1.getY() - 1; y++){
					if (board.get(t1.getX(), y).isNonEmpty()){
						return false;
					}
				}
				return true;
			}
		} else if (t1.getY() == t2.getY()){
			if (t1.getX() < t2.getX()){
				for (int x = t1.getX() + 1; x <= t2.getX() - 1; x++){
					if (board.get(x, t1.getY()).isNonEmpty()){
						return false;
					}
				}
				return true;
			} else {
				for (int x = t2.getX() + 1; x <= t1.getX() - 1; x++){
					if (board.get(x, t1.getY()).isNonEmpty()){
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	public boolean intersects(Pair pair){
		return (getFirst().equals(pair.getFirst()) || getSecond().equals(pair.getSecond()) || getFirst().equals(pair.getSecond()) || getSecond().equals(pair.getFirst()));
	}

	public int compareTo(Pair pair){
		int c = getFirst().compareTo(pair.getFirst());
		if (c != 0){
			return c;
		}
		return getSecond().compareTo(pair.getSecond());
	}
    
    public String toString(){
        return String.format("[(%d, %d), (%d, %d), %d]", t1.getX(), t1.getY(), t2.getX(), t2.getY(), t1.getType());
    }

}