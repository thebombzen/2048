import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

public class BoardPanel extends JPanel {

	public static int SIZE = 4;
	public static int MODE = 0;

	private Board board = new Board(SIZE);
	private BufferedImage image = new BufferedImage(60 + 110 * SIZE, 60 + 110 * SIZE , BufferedImage.TYPE_INT_ARGB);
	private Graphics2D g2 = image.createGraphics();

	public BoardPanel(){
		for (int i = 0; i < 2; i++){
			Tile tile = board.getComputerMove();
			board.set(tile);
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setBackground(new Color(0xFAF8EF));
		g2.clearRect(0, 0, 60 + 110 * SIZE, 60 + 110 * SIZE);
		g2.setColor(new Color(0xBBABA0));
		g2.fillRoundRect(25, 25, 10 + 110 * SIZE, 10 + 110 * SIZE, 10, 10);
		g2.setColor(new Color(0xCDC0B4));
		for (int x = 0; x < SIZE; x++){
			for (int y = 0; y < SIZE;  y++){
				g2.fillRoundRect(35 + 110 * x, 35 + 110 * y, 100, 100, 10, 10);
			}
		}
		g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 36));

		getInputMap().put(KeyStroke.getKeyStroke("UP"),
                            "moveUp");
		getActionMap().put("moveUp", new AbstractAction(){
			public void actionPerformed(ActionEvent ae){
				boolean did = board.move(Board.UP);
				if (did){
					Tile tile = board.getComputerMove();
					board.set(tile);
					repaint();
				}
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("DOWN"),
                            "moveDown");
		getActionMap().put("moveDown", new AbstractAction(){
			public void actionPerformed(ActionEvent ae){
				boolean did = board.move(Board.DOWN);
				if (did){
					Tile tile = board.getComputerMove();
					board.set(tile);
					repaint();
				}
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("LEFT"),
                            "moveLeft");
		getActionMap().put("moveLeft", new AbstractAction(){
			public void actionPerformed(ActionEvent ae){
				boolean did = board.move(Board.LEFT);
				if (did){
					Tile tile = board.getComputerMove();
					board.set(tile);
					repaint();
				}
			}
		});
		getInputMap().put(KeyStroke.getKeyStroke("RIGHT"),
                            "moveRight");
		getActionMap().put("moveRight", new AbstractAction(){
			public void actionPerformed(ActionEvent ae){
				boolean did = board.move(Board.RIGHT);
				if (did){
					Tile tile = board.getComputerMove();
					board.set(tile);
					repaint();
				}
			}
		});

	}

	public Dimension getPreferredSize(){
		return new Dimension(60 + 110 * SIZE, 60 + 110 * SIZE);
	}

	private int getTileColor(int type){
		switch (type) {
			case 0:
				return 0xCDC0B4;
			case 1:
				return 0xEEE4DA;
			case 2:
				return 0xede0c8;
			case 3:
				return 0xf2b179;
			case 4:
				return 0xf59563;
			case 5:
				return 0xf67c5f;
			case 6:
				return 0xf65e3b;
			case 7:
				return 0xedcf72;
			case 8:
				return 0xedcc61;
			case 9:
				return 0xedc850;
			case 10:
				return 0xedc53f;
			default:
				return 0xedc22e;
		}
	}

	private int getTextColor(int type){
		switch (type) {
			case 1:
			case 2:
				return 0x776e65;
			default:
				return 0xf9f6f2;
		}
	}

	public void paint(Graphics g){
		for (int x = 0; x < SIZE; x++){
			for (int y = 0; y < SIZE; y++){
				int type = board.get(x, y).getType();
				g2.setColor(new Color(getTileColor(type)));
				g2.fillRoundRect(35 + 110 * x, 35 + 110 * y, 100, 100, 10, 10);
				if (type != 0){
					g2.setColor(new Color(getTextColor(type)));
					String text = Integer.toString(1 << type);
					int width = g2.getFontMetrics().stringWidth(text);
					int height = g2.getFontMetrics().getAscent();
					g2.drawString(text, 35 + (100 - width) / 2 + 110 * x, 34 + (100 + height) / 2 + 110 * y);
				}
			}
		}
		g.drawImage(image, 0, 0, null);
	}

	public void update(Graphics g){
		paint(g);
	}

	public static void main(String[] args){
		if (args.length != 0) {
			try {
				SIZE = Integer.parseInt(args[0]);
			} catch (NumberFormatException e){
				SIZE = 4;
			}
		}
		if (args.length >= 2){
			try {
				MODE = Integer.parseInt(args[1]);
			} catch (NumberFormatException e){
				MODE = 0;
			}
		}
		JFrame frame = new JFrame();
		frame.add(new BoardPanel());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((size.width - frame.getWidth()) / 2, (size.height - frame.getHeight()) / 2);
		frame.setTitle("2048");
		frame.setResizable(false);
		frame.setVisible(true);
	}

}