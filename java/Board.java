import java.util.*;
import java.awt.Point;

public class Board {
    
    public static final Random RAND = new Random();

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    
    private final int size;
    private Tile[][] slots;

    private int mostRecentMove = 0;
    private int mostRecentOrthogonalMove = 0;

    public Board(int size){
        this.size = size;
        slots = new Tile[size][size];
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                slots[x][y] = new Tile(x, y);
            }
        }
    }

    public Board(Board board){
        this(board.size);
        for (int i = 0; i < size; i++){
            System.arraycopy(board.slots[i], 0, slots[i], 0, size);
        }
    }
    
    public Tile get(int x, int y){
        return slots[x][y];
    }
    
    public void set(int x, int y, int value){
        slots[x][y] = new Tile(x, y, value);
    }

    public void unSet(int x, int y){
        set(x, y, 0);
    }

    public void set(Tile tile){
        slots[tile.getX()][tile.getY()] = tile;
    }

    public void unSet(Tile tile){
        unSet(tile.getX(), tile.getY());
    }
    
    private boolean bubbleZeros(int dir, int start, int line){
        boolean did = false;
        int p;
        switch (dir){
            case LEFT:
                p = start - 1;
                for (int x = start; x < size; x++){
                    if (slots[x][line].isNonEmpty() && ++p != x){
                        slots[p][line] = slots[x][line];
                        slots[x][line] = new Tile(x, line);
                        did = true;
                    }
                    
                }
                break;
            case RIGHT:
                p = start + 1;
                for (int x = start; x >= 0; x--){
                    if (slots[x][line].isNonEmpty() && --p != x){
                        slots[p][line] = slots[x][line];
                        slots[x][line] = new Tile(x, line);
                        did = true;
                    }
                }
                break;
            case DOWN:
                p = start + 1;
                for (int y = start; y >= 0; y--){
                    if (slots[line][y].isNonEmpty() && --p != y){
                        slots[line][p] = slots[line][y];
                        slots[line][y] = new Tile(line, y);
                        did = true;
                    }
                }
                break;
            case UP:
                p = start - 1;
                for (int y = start; y < size; y++){
                    if (slots[line][y].isNonEmpty() && ++p != y){
                        slots[line][p] = slots[line][y];
                        slots[line][y] = new Tile(line, y);
                        did = true;
                    }
                }
                break;
        }
        return did;
    }
    
    private boolean moveLine(int dir, int line){
        boolean did = false;
        switch (dir){
            case LEFT:
                for (int x = 0; x < size - 1; x++){
                    did |= bubbleZeros(dir, x, line);
                    if (slots[x][line].isNonEmpty() && slots[x][line].getType() == slots[x+1][line].getType()){
                        slots[x][line] = new Tile(x, line, slots[x][line].getType()+1);
                        slots[x+1][line] = new Tile(x + 1, line);
                        did = true;
                    }
                }
                break;
            case RIGHT:
                for (int x = size - 1; x > 0; x--){
                    did |= bubbleZeros(dir, x, line);
                    if (slots[x][line].isNonEmpty() && slots[x][line].getType() == slots[x-1][line].getType()){
                        slots[x][line] = new Tile(x, line, slots[x][line].getType() + 1);
                        slots[x-1][line] = new Tile(x - 1, line);
                        did = true;
                    }
                }
                break;
            case DOWN:
                for (int y = size - 1; y > 0; y--){
                    did |= bubbleZeros(dir, y, line);
                    if (slots[line][y].isNonEmpty() && slots[line][y].getType() == slots[line][y-1].getType()){
                        slots[line][y] = new Tile(line, y, slots[line][y].getType() + 1);
                        slots[line][y-1] = new Tile(line, y - 1);
                        did = true;
                    }
                }
                break;
            case UP:
                for (int y = 0; y < size - 1; y++){
                    did |= bubbleZeros(dir, y, line);
                    if (slots[line][y].isNonEmpty() && slots[line][y].getType() == slots[line][y+1].getType()){
                        slots[line][y] = new Tile(line, y, slots[line][y].getType() + 1);
                        slots[line][y+1] = new Tile(line, y + 1);
                        did = true;
                    }
                }
                break;
        }
        return did;
    }
    
    private void fixTiles(){
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                slots[x][y] = new Tile(x, y, slots[x][y].getType());
            }
        }
    }

    private boolean moveWithoutFixing(int dir){
        boolean did = false;
        for (int line = 0; line < size; line++){
            did |= moveLine(dir, line);
        }
        if ((dir & 2) != (mostRecentMove & 2)){
            mostRecentOrthogonalMove = mostRecentMove;
        }
        mostRecentMove = dir;
        return did;
    }

    public boolean move(int dir){
        boolean did = moveWithoutFixing(dir);
        if (did){
            fixTiles();
        }
        return did;
    }

    public List<Pair> moveAndGetShiftedTiles(int dir){
        boolean did = moveWithoutFixing(dir);
        if (!did){
            return new ArrayList<Pair>();
        }
        List<Pair> tiles = new ArrayList<Pair>();
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                if (slots[x][y].getX() != x || slots[x][y].getY() != y){
                    tiles.add(new Pair(this, slots[x][y], new Tile(x, y, slots[x][y].getType())));
                }
            }
        }
        fixTiles();
        return tiles;
    }
    
    /*public void bubble(int dir){
        for (int line = 0; line < 4; line++){
            bubbleZeros(dir, dir == RIGHT || dir == DOWN ? 3 : 0, line);
        }
    }*/
    
    public String toString(){
        return Arrays.deepToString(slots);
    }
    
    public List<Pair> getAllPairs(){
		List<Pair> ret = new ArrayList<Pair>();
		for (int x = 0; x < size; x++){
		for (int y = 0; y < size; y++){
			for (int i = x; i < size; i++){
			for (int j = y; j < size; j++){
				if ((x != i || y != j) && slots[x][y].isNonEmpty() && slots[x][y].getType() == slots[i][j].getType()){
					ret.add(new Pair(this, slots[x][y], slots[i][j]));
					//ret.add(new Pair(this, x, y, i, j, true));
				}
			}
			}
		}
		}
		return ret;
	}

    /*public List<Tile> getAllPairsWith(Tile tile){
        List<Tile> ret = new ArrayList<Tile>();
        for (int x = 0; x < size; x++){
        for (int y = 0; y < size; y++){
            if (x != tile.getX() || y != tile.getY()){
                if (tile.getType() == slots[x][y].getType()){
                    ret.add(slots[x][y]);
                }
            }
        }
        }
        return ret;
    }*/

    public List<Tile> getAvailableTiles() {
        List<Tile> left = new ArrayList<Tile>();
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                if (slots[x][y].isEmpty()){
                    left.add(new Tile(x, y));
                }
            }
        }
        return left;
    }
	
    public Tile getRandomAvailableTile(){
        List<Tile> left = getAvailableTiles();
        Tile tile = left.get(RAND.nextInt(left.size()));
        return tile;
    }

    public Tile getRandomMove(){
        Tile tile = getRandomAvailableTile();
        if (RAND.nextInt(10) == 0){
            tile.setType(2);
        } else {
            tile.setType(1);
        }
        return tile;
    }

    private int getMaximumNumberOfPairsSatisfying(BooleanTester<Pair> tester){

        List<Pair> pairs = getAllPairs();

        // The sorted part is crucial because otherwise it fails
        SortedSet<Pair> found = new TreeSet<Pair>();

        // This stores the degree of the vertex
        Map<Tile, Integer> used = new HashMap<Tile, Integer>();

        for (Pair pair : pairs){
            if (tester.test(pair)){
                found.add(pair);
                if (used.containsKey(pair.getFirst())){
                    used.put(pair.getFirst(), used.get(pair.getFirst()) + 1);
                } else {
                    used.put(pair.getFirst(), 1);
                }
                if (used.containsKey(pair.getSecond())){
                    used.put(pair.getSecond(), used.get(pair.getSecond()) + 1);
                } else {
                    used.put(pair.getSecond(), 1);
                }
            }
        }

        int numFound = found.size();
        int actuallyFound = 0;
        int killed = 0;

        boolean discovered;


        do {
            do {
                discovered = false;
                Pair discPair = null;
                for (Pair pair : found){
                    if (((used.get(pair.getFirst()) | used.get(pair.getSecond())) & 1) != 0){
                        actuallyFound++;
                        discovered = true;
                        discPair = pair;
                        break;
                    }
                }
                if (discovered){
                    Iterator<Pair> iter = found.iterator();
                    while (iter.hasNext()){
                        Pair testPair = iter.next();
                        if (testPair.intersects(discPair)){
                            used.put(testPair.getFirst(), used.get(testPair.getSecond()) - 1);
                            used.put(testPair.getSecond(), used.get(testPair.getSecond()) - 1);
                            iter.remove();
                            killed++;
                            continue;
                        }
                    }
                }
            } while (discovered);

            if (!found.isEmpty()){
                Pair pair = found.first();
                actuallyFound++;
                Iterator<Pair> iter = found.iterator();
                while (iter.hasNext()){
                    Pair testPair = iter.next();
                    if (testPair.intersects(pair)){
                        used.put(testPair.getFirst(), used.get(testPair.getSecond()) - 1);
                        used.put(testPair.getSecond(), used.get(testPair.getSecond()) - 1);
                        iter.remove();
                        killed++;
                        continue;
                    }
                }
            }

        } while (!found.isEmpty());
        
        assert (killed == numFound);

        return actuallyFound;
        
    }

    private boolean doesBoardCombineImmediately(){
        List<Pair> pairs = getAllPairs();
        for (Pair pair : pairs){
            if (pair.isWeakly1Combinable()){
                return true;
            }
        }
        return false;
    }

    private int getBoardCombinability(){
        BooleanTester<Pair> tester = new BooleanTester<Pair>(){
            public boolean test(Pair pair){
                return pair.isWeakly1Combinable();
            }
        };
        return getMaximumNumberOfPairsSatisfying(tester);
    }

    private boolean doesBoardCombineBlockably(){
        List<Pair> pairs = getAllPairs();
        int n = 0;
        for (Pair pair : pairs){
            if (!pair.isStrongly1Combinable() && pair.isWeakly1Combinable()){
                if (++n >= 2){
                    return false;
                }
            }
        }
        return n == 1;
    }

    private int getBoardBlockableCombinability(){
        BooleanTester<Pair> tester = new BooleanTester<Pair>(){
            public boolean test(Pair pair){
                return !pair.isStrongly1Combinable() && pair.isWeakly1Combinable();
            }
        };
        return getMaximumNumberOfPairsSatisfying(tester);
    }

    private boolean doesBoardCombineSecondarily(){
        if (this.doesBoardCombineImmediately()){
            return true;
        }
        for (int dir = 0; dir < 4; dir++){
            Board board = new Board(this);
            board.move(dir);
            if (board.doesBoardCombineImmediately() && !board.doesBoardCombineBlockably()){
                return true;
            }
        }
        return false;
    }

    private int getBoardSecondaryCombinability(){
        int n = 0;
        for (int dir = 0; dir < 4; dir++){
            int currN = 0;
            Board board = new Board(this);
            board.move(dir);
            currN = board.getBoardCombinability() - (board.getBoardBlockableCombinability() > 0 ? 1 : 0);
            if (currN > n){
                n = currN;
            }
        }
        return n;
    }

    private boolean doesBoardNCombineImmediately(int n){
        List<Pair> pairs = getAllPairs();
        for (Pair pair : pairs){
            if (pair.getType() == n && pair.isWeakly1Combinable()){
                return true;
            }
        }
        return false;
    }

    private int getBoardNCombinability(final int n){
        BooleanTester<Pair> tester = new BooleanTester<Pair>(){
            public boolean test(Pair pair){
                return pair.getType() == n && pair.isWeakly1Combinable();
            }
        };
        return getMaximumNumberOfPairsSatisfying(tester);
    }

    private boolean doesBoardNCombineBlockably(int n){
        List<Pair> pairs = getAllPairs();
        int num = 0;
        for (Pair pair : pairs){
            if (pair.getType() == n && !pair.isStrongly1Combinable() && pair.isWeakly1Combinable()){
                if (++num >= 2){
                    return false;
                }
            }
        }
        return num == 1;
    }

    private int getBoardBlockableNCombinability(final int n){
        BooleanTester<Pair> tester = new BooleanTester<Pair>(){
            public boolean test(Pair pair){
                return pair.getType() == n && !pair.isStrongly1Combinable() && pair.isWeakly1Combinable();
            }
        };
        return getMaximumNumberOfPairsSatisfying(tester);
    }

    private boolean doesBoardNCombineSecondarily(int n){
        if (this.doesBoardNCombineImmediately(n)){
            return true;
        }
        for (int dir = 0; dir < 4; dir++){
            Board board = new Board(this);
            board.move(dir);
            if (board.doesBoardNCombineImmediately(n) && !board.doesBoardNCombineBlockably(n)){
                return true;
            }
        }
        return false;
    }

    private int getBoardSecondaryNCombinability(final int n){
        int num = 0;
        for (int dir = 0; dir < 4; dir++){
            int currNum = 0;
            Board board = new Board(this);
            board.move(dir);
            currNum = board.getBoardNCombinability(n) - (board.getBoardBlockableNCombinability(n) > 0 ? 1 : 0);
            if (currNum > num){
                num = currNum;
            }
        }
        return num;
    }

    public Tile getMeanComputerMove(){
        //return getRandomMove();
        List<Tile> prevAvail = new ArrayList<Tile>();
        List<Tile> currAvail = new ArrayList<Tile>();

        for (Tile t : getAvailableTiles()){
            prevAvail.add(new Tile(t, 1));
            prevAvail.add(new Tile(t, 2));
        }

        System.out.println("=================");

        int best = Integer.MAX_VALUE;
        // Which tiles can we place without any twos combining?
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardNCombinability(1);
            System.out.println(tile + ", Two1, " + num);
            if (num < best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        best = Integer.MAX_VALUE;
        // Which tiles can we place without twos secondarily combining?
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardSecondaryNCombinability(1);
            System.out.println(tile + ", Two2, " + num);
            if (num < best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        // Which tiles are twos?
        for (Tile tile : prevAvail){
            if (tile.getType() == 1){
                currAvail.add(tile);
            }
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        best = Integer.MAX_VALUE;
        // Which tiles can we place without anything combining?
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardCombinability();
            System.out.println(tile + ", Any1, " + num);
            if (num < best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        best = Integer.MAX_VALUE;
        // Which tiles prevent anything from combining secondarily? 
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardSecondaryCombinability();
            System.out.println(tile + ", Any2, " + num);
            if (num < best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        return getMostExtreme(getMostExtreme(prevAvail, mostRecentOrthogonalMove, true), mostRecentMove, true).get(0);
        
    }

    public List<Tile> getMostExtreme(List<Tile> tiles, int dir, boolean close){
        int best;
        if ((dir == LEFT || dir == UP) == close){
            best = size; // this is actually the worst
        } else {
            best = -1;
        }
        List<Tile> ret = new ArrayList<Tile>();
        switch (dir){
            case LEFT:
                for (Tile tile : tiles){
                    if (close && (tile.getX() < best) || !close && (tile.getX() > best)){
                        best = tile.getX();
                        ret.clear();
                        ret.add(tile);
                    } else if (tile.getX() == best){
                        ret.add(tile);
                    }
                }
                return ret;
            case RIGHT:
                for (Tile tile : tiles){
                    if (close && (tile.getX() > best) || !close && (tile.getX() < best)){
                        best = tile.getX();
                        ret.clear();
                        ret.add(tile);
                    } else if (tile.getX() == best){
                        ret.add(tile);
                    }
                }
                return ret;
            case DOWN:
                for (Tile tile : tiles){
                    if (close && (tile.getY() > best) || !close && (tile.getX() < best)){
                        best = tile.getY();
                        ret.clear();
                        ret.add(tile);
                    } else if (tile.getY() == best){
                        ret.add(tile);
                    }
                }
                return ret;
            case UP:
                for (Tile tile : tiles){
                    if (close && (tile.getY() < best) || !close && (tile.getX() > best)){
                        best = tile.getY();
                        ret.clear();
                        ret.add(tile);
                    } else if (tile.getY() == best){
                        ret.add(tile);
                    }
                }
                return ret;
            default:
                return ret; // will never happen
        }
    }

    public Tile getComputerMove(){
        if (BoardPanel.MODE == 1){
            return getNiceComputerMove();
        } else if (BoardPanel.MODE == 2){
            return getMeanComputerMove();
        }
        
        int total = 0;
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                if (slots[x][y].isEmpty()){
                    continue;
                }
                total += 1 << slots[x][y].getType();
                if (slots[x][y].getType() == 10 || total > (BoardPanel.MODE != 0 ? BoardPanel.MODE : 1536)){
                    return getMeanComputerMove();
                }
            }
        }
        return getNiceComputerMove();
    }

    public Tile getNiceComputerMove(){
        //return getRandomMove();
        List<Tile> prevAvail = new ArrayList<Tile>();
        List<Tile> currAvail = new ArrayList<Tile>();

        for (Tile t : getAvailableTiles()){
            prevAvail.add(new Tile(t, 1));
            prevAvail.add(new Tile(t, 2));
        }

        int best = -1;
        // Which tiles can we place without any twos combining?
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardNCombinability(1);
            if (num > best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        best = -1;
        // Which tiles can we place without twos secondarily combining?
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardSecondaryNCombinability(1);
            if (num > best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        // Which tiles are twos?
        for (Tile tile : prevAvail){
            if (tile.getType() == 1){
                currAvail.add(tile);
            }
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        best = -1;
        // Which tiles can we place without anything combining?
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardCombinability();
            if (num > best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        best = -1;
        // Which tiles prevent anything from combining secondarily? 
        for (Tile tile : prevAvail){
            set(tile);
            int num = getBoardSecondaryCombinability();
            if (num > best){
                best = num;
                currAvail.clear();
                currAvail.add(tile);
            } else if (num == best) {
                currAvail.add(tile);
            }
            unSet(tile);
        }
        if (currAvail.size() == 1){
            return currAvail.get(0);
        } else if (currAvail.size() > 1) {
            prevAvail = new ArrayList<Tile>(currAvail);
            currAvail.clear();
        }

        return getMostExtreme(getMostExtreme(prevAvail, mostRecentOrthogonalMove, false), mostRecentMove, false).get(0);
    }

    /*public static void main(String[] args){
        Board board = new Board();
		board.set(0, 0, 1);
		board.set(1, 1, 2);
        board.set(1, 2, 1);
        System.out.println(board);
		System.out.println(board.isStronglyNCombinable(2));
    }*/
    
}
