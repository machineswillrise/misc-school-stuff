import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

class ScaledButton extends JButton {
	public ScaledButton(int width, int height) {
		Dimension d = new Dimension(width, height);
		setPreferredSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
	}
}

class Canvas extends JPanel {
	private boolean drawing;
	private Color selectedColor;

	private List<Point> points;
	private List<Color> colors;

	public Canvas() {
		points = new LinkedList<>();
		colors = new LinkedList<>();
		setBackground(Color.WHITE);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				drawing = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				drawing = false;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (drawing) {
					points.add(e.getPoint());
					colors.add(selectedColor);
					repaint();
				}
			}
		});
	}

	public void setSelectedColor(Color newColor) {
		selectedColor = newColor;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON
		);

		for (int i = 0; i < points.size(); i++) {
			g2d.setColor(colors.get(i));
			g2d.fillOval(points.get(i).x, points.get(i).y, 6, 6);
		}
	}
}

class ColorPanel extends JToolBar {
	private Color selectedColor;

	public ColorPanel(Canvas canvas) {
		List<Color> colors = new LinkedList<>() {{
			add(Color.RED);
			add(Color.ORANGE);
			add(Color.YELLOW);
			add(Color.GREEN);
			add(Color.BLUE);
			add(Color.MAGENTA);
		}};

		for (Color color : colors) {
			ScaledButton button = new ScaledButton(64, 64);
			button.setBackground(color);
			button.addActionListener(e -> {
				selectedColor = color;
				canvas.setSelectedColor(color);
			});

			add(button);
		}

		// default
		selectedColor = Color.BLACK;
		canvas.setSelectedColor(Color.BLACK);
	}

	public Color getSelectedColor() {
		return selectedColor;
	}
}

public class ImageEditor extends JFrame {
	private JPanel mainPanel;
	private JPanel topLeftPanel;
	private ColorPanel colorPanel;
	private Canvas canvas;

	public ImageEditor(String title) {
		setSize(1024, 768);
		setTitle(title);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		canvas = new Canvas();
		colorPanel = new ColorPanel(canvas);
		topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topLeftPanel.add(colorPanel);

		mainPanel.add(topLeftPanel, BorderLayout.NORTH);
		mainPanel.add(canvas, BorderLayout.CENTER);

		add(mainPanel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ImageEditor imageEditor = new ImageEditor("Image Editor");
			imageEditor.setVisible(true);
		});
	}
}
