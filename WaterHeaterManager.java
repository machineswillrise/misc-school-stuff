import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.util.LinkedHashMap;

class Tank extends JComponent {
	private int level = 0;
	private int temperature = 20; // 0 = cold, 100 = hot
	private final JLabel temperatureLabel;

	public Tank(int width, int height, JLabel temperatureLabel) {
		setPreferredSize(new Dimension(width, height));
		this.temperatureLabel = temperatureLabel;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		if (level > 100 || level < 0) {
			return;
		}

		this.level = level;
		repaint();
	}

	public void increaseLevel(int amount) {
		setLevel(this.level + amount);
	}

	public void decreaseLevel(int amount) {
		setLevel(this.level - amount);
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = Math.max(0, Math.min(100, temperature));
		temperatureLabel.setText("Temperature: " + this.temperature);
		repaint();
	}

	public void increaseTemperature(int amount) {
		setTemperature(this.temperature + amount);
	}

	public void decreaseTemperature(int amount) {
		setTemperature(this.temperature - amount);
	}

	private Color temperatureToColor() {
		float ratio = this.temperature / 100.0f;
		int r = Math.round(ratio * 255);
		int g = 0;
		int b = Math.round((1 - ratio) * 255);
		return new Color(r, g, b);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());

		// draw outline
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		// draw colored rectangle based on temperature at the bottom of the tank
		g.setColor(temperatureToColor());
		g.fillRect(0, getHeight() - level, getWidth(), level);
	}
}

public class WaterHeaterManager extends JFrame {
	private final JPanel waterPanel;
	private final JPanel temperaturePanel;

	public WaterHeaterManager() {
		super("Water Heater Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		waterPanel = new JPanel(new GridLayout(2, 1));
		temperaturePanel = new JPanel(new GridLayout(2, 1));
	}

	private static void initPanel(JPanel panel, LinkedHashMap<JButton, ActionListener> buttons) {
		for (var entry : buttons.entrySet()) {
			entry.getKey().addActionListener(entry.getValue());
			panel.add(entry.getKey());
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			WaterHeaterManager manager = new WaterHeaterManager();
			JLabel temperatureLabel = new JLabel();

			Tank tank = new Tank(100, 100, temperatureLabel);
			tank.setLevel(50);
			
			manager.add(tank, BorderLayout.CENTER);
			manager.add(temperatureLabel, BorderLayout.NORTH);
			temperatureLabel.setText("Temperature: " + tank.getTemperature());

			var waterButtons = new LinkedHashMap<JButton, ActionListener>() {{
				put(new JButton("Increase Water Level"), e -> tank.increaseLevel(10));
				put(new JButton("Decrease Water Level"), e -> tank.decreaseLevel(10));
			}};
			
			var temperatureButtons = new LinkedHashMap<JButton, ActionListener>() {{
				put(new JButton("Increase Temperature"), e -> tank.increaseTemperature(10));
				put(new JButton("Decrease Temperature"), e -> tank.decreaseTemperature(10));
			}};

			initPanel(manager.waterPanel, waterButtons);
			initPanel(manager.temperaturePanel, temperatureButtons);
			manager.add(manager.waterPanel, BorderLayout.WEST);
			manager.add(manager.temperaturePanel, BorderLayout.EAST);

			manager.pack();
			manager.setVisible(true);
		});
	}
}
