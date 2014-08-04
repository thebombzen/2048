public class Tile implements Comparable<Tile> {
	private int x;
	private int y;
	private int type;
	public Tile (int x, int y, int type){
		this.x = x;
		this.y = y;
		this.type = type;
	}

	public Tile (Tile t){
		this.x = t.x;
		this.y = t.y;
		this.type = t.type;
	}

	public Tile (Tile t, int type){
		this(t);
		this.type = type;
	}

	public Tile(int x, int y){
		this.x = x;
		this.y = y;
		this.type = 0;
	}

	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public int getType(){
		return type;
	}

	public void setX(int x){
		this.x = x;
	}

	public void setY(int y){
		this.y = y;
	}

	public void setLocation(int x, int y){
		this.x = x;
		this.y = y;
	}

	public void setType(int type){
		this.type = type;
	}

	public boolean isEmpty(){
		return type == 0;
	}

	public boolean isNonEmpty(){
		return type != 0;
	}

	public boolean equals(Object o){
		if (!(o instanceof Tile)){
			return false;
		}
		Tile t = (Tile)o;
		return this.x == t.x && this.y == t.y && this.type == t.type;
	}

	public int hashCode(){
		int n = 31;
		n *= n + x;
		n *= n + y;
		n *= n + type;
		return n;
	}

	public String toString(){
		return String.format("[%d, %d, %d]", x, y, type);
	}

	public int compareTo(Tile tile){
		if (x < tile.x){
			return -1;
		} else if (x > tile.x){
			return 1;
		}
		if (y < tile.y){
			return -1;
		} else if (y > tile.y){
			return 1;
		}
		if (type < tile.type){
			return -1;
		} else if (type > tile.type){
			return 1;
		}
		return 0;
	}

}