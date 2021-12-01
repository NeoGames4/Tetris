import java.awt.Color;
import java.util.ArrayList;

public class Tetris {
	
	ArrayList<Cell> cells = new ArrayList<>();
	
	public final int tilesX; // Field size (tiles)
	public final int tilesY;
	
	public long score = 0, level = 0, clearedLines = 0, highscore = 0;
	
	public Tetris(int tilesX, int tilesY) {
		this.tilesX = tilesX;
		this.tilesY = tilesY;
	}
	
	public void moveFallingCells(int x, int y) {
		ArrayList<Cell> canBeMoved = new ArrayList<>();
		for(Cell c : cells) {
			if(c.falling && canBeMoved(c, x, y)) canBeMoved.add(c);
		} for(Cell c : canBeMoved) {
			c.addX(x);
			c.addY(y);
		}
	}
	
	public boolean isSet(int x, int y) {
		return get(x, y) != null;
	}
	
	public Cell getFallingCell() {
		for(Cell c : cells) {
			if(c.falling) return c;
		} return null;
	}
	
	public Cell get(int x, int y) {
		for(Cell c : cells) {
			if(c.x == x && c.y == y) return c;
		} return null;
	}
	
	public boolean isPartner(Cell a, Cell b) {
		for(Cell c : a.partners) {
			if(c == b) return true;
		} return false;
	}
	
	public boolean canBeMoved(Cell c, int x, int y) {
		for(Cell cell : c.partners) {
			Cell below = get(cell.x+x, cell.y+y);
			if(cell.x+x < 0 || cell.x+x > tilesX-1) return false;
			else if(cell.y+y > tilesY-1) return false;
			else if(below != null && !isPartner(c, below)) return false;
		} return true;
	}
	
	public static final int L = 0;
	public static final int S = 1;
	public static final int O = 2;
	public static final int I = 3;
	public static final int T = 4;
	public static final int J = 5;
	public static final int Z = 6;
	public static final Color[] colors = {Color.green, Color.red, Color.blue, new Color(250, 250, 5), new Color(5, 180, 255), new Color(130, 10, 230)};
	public void addNewTetromino(int x, int structure, int rotation, Color color) {
		Cell[] partners = new Cell[0];
		switch(structure) {
			case L:
				partners = new Cell[4];
				partners[0] = new Cell(1, 0, 0, 1);
				partners[1] = new Cell(1, 1, 0, 0);
				partners[2] = new Cell(1, 2, 0, -1);
				partners[3] = new Cell(2, 2, -1, -1); break;
			case S:
				partners = new Cell[4];
				partners[0] = new Cell(1, 0, 0, 1);
				partners[1] = new Cell(2, 0, -1, 1);
				partners[2] = new Cell(1, 1, 0, 0);
				partners[3] = new Cell(0, 1, 1, 0); break;
			case O:
				partners = new Cell[4];
				partners[0] = new Cell(1, 0, 0, 1);
				partners[1] = new Cell(2, 0, -1, 1);
				partners[2] = new Cell(1, 1, 0, 0);
				partners[3] = new Cell(2, 1, -1, 0); break;
			case I:
				partners = new Cell[4];
				partners[0] = new Cell(1, 0, 0, 1);
				partners[1] = new Cell(1, 1, 0, 0);
				partners[2] = new Cell(1, 2, 0, -1);
				partners[3] = new Cell(1, 3, 0, -2); break;
			case T:
				partners = new Cell[4];
				partners[0] = new Cell(0, 1, 1, 0);
				partners[1] = new Cell(1, 1, 0, 0);
				partners[2] = new Cell(2, 1, -1, 0);
				partners[3] = new Cell(1, 2, 0, -1); break;
			case J:
				partners = new Cell[4];
				partners[0] = new Cell(1, 0, 0, 1);
				partners[1] = new Cell(1, 1, 0, 0);
				partners[2] = new Cell(1, 2, 0, -1);
				partners[3] = new Cell(0, 2, 1, -1); break;
			case Z:
				partners = new Cell[4];
				partners[0] = new Cell(0, 0, 1, 1);
				partners[1] = new Cell(1, 0, 0, 1);
				partners[2] = new Cell(1, 1, 0, 0);
				partners[3] = new Cell(2, 1, -1, 0); break;
		}
		for(Cell c : partners) {
			c.addX(x);
			c.addY(-3);
			if(color != null) c.color = color;
			c.partners = partners;
			cells.add(c);
		}
	}
	
	public void update() {
		ArrayList<Cell> canBeMoved = new ArrayList<>();
		for(Cell c : cells) {
			if(c.falling && canBeMoved(c, 0, 1)) canBeMoved.add(c);
			else if(c.falling && c.y < 0) {
				cells.clear();
				level = score = clearedLines = 0;
				return;
			}
			else if(c.falling) c.falling = false;
		} for(Cell c : canBeMoved) c.addY(1);
		boolean f = false;
		for(Cell c : cells) {
			if(c.falling) f = true;
		}
		if(!f) {
			clearFullRows();
			addNewTetromino((int)(Math.random() * (tilesX - 3)), (int)(Math.random() * 7), 0, colors[(int)(colors.length * Math.random())]);
		} level = (int) (clearedLines / 2);
		highscore = Math.max(score, highscore);
	}
	
	public void clearFullRows() {
		int cleared = 0;
		int lowest = 0;
		for(int i = 0; i<tilesY; i++) {
			boolean full = true;
			for(int j = 0; j<tilesX; j++) {
				if(get(j, i) == null) full = false;
			}
			final int y = i;
			if(full) {
				cells.removeIf(c -> c.y == y);
				cleared++;
				lowest = Math.max(i, lowest);
			}
		} if(cleared > 0) gravity(lowest, cleared);
		if(cleared == 1) score += 40 * (level+1); // Original Nintendo Scoring System
		else if(cleared == 2) score += 100 * (level+1);
		else if(cleared == 3) score += 300 * (level+1);
		else if(cleared >= 4) score += 1200 * (level+1);
		clearedLines += cleared;
	}
	
	public void gravity(int y, int steps) {
		for(int i = y; i>=0; i--) {
			for(int j = 0; j<tilesX; j++) {
				Cell c = get(j, i);
				for(int k = 0; k<steps && c != null && !c.falling && !isSet(j, c.y+1) && c.y < tilesY-1; k++) c.addY(1);
			}
		}
	}
	
	public boolean canBeRotated(Cell cell) {
		for(Cell c : cell.partners) {
			int x = c.center[0] + c.x + c.center[1];
			int y = c.center[1] + c.y - c.center[0];
			Cell there = get(x, y);
			if(x < 0 || x >= tilesX || y >= tilesY || (there != null && !isPartner(c, there))) return false;
		} return true;
	}
	
	public void rotateTetromino(Cell cell) {
		if(!canBeRotated(cell)) return;
		for(Cell c : cell.partners) {
			int previousCenterX = c.center[0];
			
			c.setX(c.center[0] + c.x + c.center[1]);
			c.setY(c.center[1] + c.y - previousCenterX);
		}
	}
	
	class Cell {
		
		public int x;
		public int y;
		public boolean falling = true;
		public Cell[] partners;
		public int[] center; // how to get from x / y to center of tretromino
		public Color color = Color.green;
		
		public Cell(int x, int y, int centerX, int centerY) {
			this.x = x;
			this.y = y;
			center = new int[] {centerX, centerY};
		}
		
		public void setX(int x) {
			center[0] = (this.x+center[0])-x;
			this.x = x;
		}
		
		public void setY(int y) {
			center[1] = (this.y+center[1])-y;
			this.y = y;
		}
		
		public void addX(int x) {
			this.x += x;
		}
		
		public void addY(int y) {
			this.y += y;
		}
		
	}

}
