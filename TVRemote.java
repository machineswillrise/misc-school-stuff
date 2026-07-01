import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.border.Border;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

class Beep implements Runnable {
	@Override
	public void run() {
		Synthesizer synth = null;

		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();

			MidiChannel[] channels = synth.getChannels();

			channels[0].noteOn(60, 100);
			Thread.sleep(1000);
			channels[0].noteOff(60);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if (synth != null) {
				synth.close();
			}
		}
	}
}

class RemoteButton extends JButton {
	protected void enlargeFont(int pts) {
		Font currentFont = getFont();
		Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), pts);
		setFont(newFont);
	}

	protected void changeSize(int width, int height) {
		Dimension d = new Dimension(width, height);

		setPreferredSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
	}

	private void setColors(Color background, Color foreground) {
		setBackground(background);
		setForeground(foreground);
	}

	public RemoteButton(String text) {
		super(text);

		enlargeFont(16);
		changeSize(70, 40);
		setColors(Color.decode("#266DD3"), Color.WHITE);

		// Disable the outline when it's clicked
		setFocusPainted(false);

		// Add beep listener
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Beep beep = new Beep();
				Thread beepThread = new Thread(beep);
				beepThread.start();
			}
		});
	}
}

class HintedButton extends RemoteButton {
	public HintedButton(String text, String tooltip) {
		super(text);
		setToolTipText(tooltip);
	}

	public HintedButton(String text) {
		this(text, text);
	}
}

class SmallButton extends HintedButton {
	public SmallButton(String text, String tooltip) {
		super(text, tooltip);
		changeSize(48, 8);
		enlargeFont(12);
	}

	public SmallButton(String text) {
		this(text, text);
	}
}

class RemotePanel extends JPanel {
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Color darkGray = Color.DARK_GRAY;
		Color darkerGray = new Color(
			darkGray.getRed() / 2,
			darkGray.getGreen() / 2,
			darkGray.getBlue() / 2
		);

		g.setColor(darkerGray);
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}

class TopButtons extends RemotePanel {
	private HintedButton powerButton, sourceButton;

	public TopButtons() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

		powerButton = new HintedButton("⏻", "Power");
		sourceButton = new HintedButton("↴", "Source");

		add(powerButton);
		add(Box.createHorizontalGlue());
		add(sourceButton);
	}
}

class SecondarySection extends RemotePanel {
	public SecondarySection() {
		setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
	}
}

class GridPanel extends SecondarySection {
	public GridPanel(int rows, int cols) {
		setLayout(new GridLayout(rows, cols, 5, 5));
	}

	protected void addMultidimensional(HintedButton[][] buttons) {
		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons[i].length; j++) {
				HintedButton button = buttons[i][j];
				add(button);
			}
		}
	}
}

class Keypad extends GridPanel {
	public Keypad(int width) {
		super(4, 3);
		setMaximumSize(new Dimension(width, 200));

		HintedButton[][] buttons =  {
			{ new HintedButton("1"), new HintedButton("2"), new HintedButton("3") },
			{ new HintedButton("4"), new HintedButton("5"), new HintedButton("6") },
			{ new HintedButton("7"), new HintedButton("8"), new HintedButton("9") },
			{ new HintedButton("-"), new HintedButton("0"), new HintedButton("←") }
		};

		addMultidimensional(buttons);
	}
}

class VolumeChanger extends SecondarySection {
	private RemotePanel createVerticalPanel(HintedButton... buttons) {
		RemotePanel panel = new RemotePanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		for (HintedButton button : buttons) {
			button.setAlignmentX(CENTER_ALIGNMENT);
			button.setAlignmentY(CENTER_ALIGNMENT);

			Border border = BorderFactory.createEmptyBorder();
			button.setBorder(border);

			panel.add(button);
		}

		return panel;
	}

	private void addPanels(RemotePanel... panels) {
		for (int i = 0; i < panels.length; i++) {
			add(panels[i]);

			if (i != panels.length - 1) {
				add(Box.createHorizontalGlue());
			}
		}
	}

	public VolumeChanger() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		RemotePanel volumeButtons = createVerticalPanel(
			new HintedButton("+", "Volume Up"),
			new HintedButton("VOL", "Volume"),
			new HintedButton("-", "Volume Down")
		);

		RemotePanel centeredButtons = createVerticalPanel(
			new HintedButton("🔇", "Mute"),
			new HintedButton("📝", "Channel List")
		);

		centeredButtons.add(Box.createVerticalStrut(25), 0);
		centeredButtons.add(Box.createVerticalStrut(25));

		RemotePanel channelButtons = createVerticalPanel(
			new HintedButton("+", "Channel Up"),
			new HintedButton("CH", "Channel"),
			new HintedButton("-", "Channel Down")
		);

		addPanels(volumeButtons, centeredButtons, channelButtons);
	}
}

class NavigationPad extends SecondarySection {
	private RemotePanel menuRow, dpad;

	private void setupDPad() {
		dpad.add(new HintedButton("🛠️", "Tools"));
		dpad.add(new HintedButton("↑", "Up"));
		dpad.add(new HintedButton("ℹ️", "Info"));

		dpad.add(new HintedButton("←", "Left"));
		dpad.add(new HintedButton("OK", "Select"));
		dpad.add(new HintedButton("→", "Right"));

		dpad.add(new HintedButton("⟳", "Cycle"));
		dpad.add(new HintedButton("↓", "Down"));
		dpad.add(new HintedButton("➜]", "Exit"));
	}

	public NavigationPad() {
		menuRow = new RemotePanel();
		menuRow.setLayout(new FlowLayout());

		menuRow.add(new HintedButton("☰", "Menu"));
		menuRow.add(new HintedButton("🏠", "Smart Hub"));
		menuRow.add(new HintedButton("📺", "Guide"));

		dpad = new RemotePanel();
		dpad.setLayout(new GridLayout(3, 3, 5, 5));
		setupDPad();

		add(menuRow);
		add(dpad);
	}
}

class BottomButtons extends GridPanel {
	public BottomButtons() {
		super(3, 4);
		SmallButton[][] buttons = {
			{ new SmallButton("A"), new SmallButton("B"), new SmallButton("C"), new SmallButton("D") },
			{ new SmallButton("📝", "E-Manual"), new SmallButton("🏈", "Sports"), new SmallButton("💬", "Closed-Caption"), new SmallButton("⏹️", "Stop") },
			{ new SmallButton("⏪", "Rewind"), new SmallButton("▶️", "Play"), new SmallButton("⏸️", "Pause"), new SmallButton("⏩", "Fast-Forward") }
		};

		addMultidimensional(buttons);
	}
}
class TVRemote extends JFrame {
	private RemotePanel mainPanel;

	private void addPanels(RemotePanel... panels) {
		for (int i = 0; i < panels.length; i++) {
			mainPanel.add(panels[i]);

			if (i != panels.length - 1) {
				mainPanel.add(Box.createVerticalStrut(10));
			}
		}
	}

	public TVRemote() {
		setTitle("TV Remote");
		setSize(384, 768);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		mainPanel = new RemotePanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel);

		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(500);
		ttm.setDismissDelay(5000);
		ttm.setReshowDelay(0);

		addPanels(
			new TopButtons(),
			new Keypad(getWidth()),
			new VolumeChanger(),
			new NavigationPad(),
			new BottomButtons()
		);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TVRemote remote = new TVRemote();
				remote.setVisible(true);
			}
		});
	}
}
