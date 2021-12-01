import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Frame extends JFrame implements KeyListener {
	
	int tileSize = 16; // in pixels
	
	Tetris tetris = new Tetris(12, 34); // Creates Tetris field with size 12x34
	
	Panel panel = new Panel();
	
	int c = 0;
	
	public Frame() {
		setTitle("Tetris");
		setBounds(0, 0, tetris.tilesX*tileSize, tetris.tilesY*tileSize+22); // + 22 correction !
		setLocationRelativeTo(null);
		setFocusable(true);
		setFocusTraversalKeysEnabled(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(panel);
		addKeyListener(this);
		
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
			repaint();
			if(c > 25 - tetris.level) { // When the level gets higher, the tiles update faster
				tetris.update();
				c = 0;
			}
			c++;
		}, 0, 10, TimeUnit.MILLISECONDS);
	}
	
	@Override
    public void keyTyped(KeyEvent e) {}

	@Override
    public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A: tetris.moveFallingCells(-1, 0); break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D: tetris.moveFallingCells(1, 0); break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
			case KeyEvent.VK_SPACE: tetris.moveFallingCells(0, 1); break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W: tetris.rotateTetromino(tetris.getFallingCell()); break;
		}
	}

	@Override
    public void keyReleased(KeyEvent e) {}
	
	class Panel extends JPanel {
		
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			
			g2.setColor(Color.black); // Background
			g2.fillRect(0, 0, getWidth(), getHeight());
			
			for(Tetris.Cell c : tetris.cells) { // Tetrominoes
				g2.setColor(c.color);
				g2.fillRect(c.x * tileSize, c.y * tileSize, tileSize, tileSize);
			}
						
			g2.setColor(Color.gray); // Grid
			g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
			for(int i = 1; i<tetris.tilesX; i++) g2.drawLine(i*tileSize, 0, i*tileSize, getHeight());
			for(int i = 1; i<tetris.tilesY; i++) g2.drawLine(0, i*tileSize, getWidth(), i*tileSize);
			
			g2.setColor(Color.white); // Score
			g2.setFont(new Font(getFont().getName(), Font.BOLD, 15));
			String scoreString = tetris.score + "";
			g2.drawString(scoreString, getWidth()/2 - g2.getFontMetrics().stringWidth(scoreString)/2, 20);
			String levelString = "Level: " + tetris.level;
			g2.drawString(levelString, getWidth()/2 - g2.getFontMetrics().stringWidth(levelString)/2, 20 + g2.getFont().getSize());
			String highscoreString = "Highscore: " + tetris.highscore;
			g2.drawString(highscoreString, getWidth()/2 - g2.getFontMetrics().stringWidth(highscoreString)/2, 20 + 2 * g2.getFont().getSize());
		}
		
	}

}
