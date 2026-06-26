import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

class OnScreenKeyboard extends JFrame {
	private JPanel panel;
	private JTextField textField;

	public OnScreenKeyboard() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		textField = new JTextField(20);
		panel.add(textField, BorderLayout.NORTH);

		JPanel keyboardPanel = new JPanel();
		keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));

		// jagged array to save space
		char[][] rows = {
			{'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'}, // 10
			{'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'},      // 9
                  {'Z', 'X', 'C', 'V', 'B', 'N', 'M'}                 // 7
		};

		for (int i = 0; i < rows.length; i++) {
			JPanel rowPanel = new JPanel();
			rowPanel.setLayout(new FlowLayout());

			for (int j = 0; j < rows[i].length; j++) {
				String s = String.valueOf(rows[i][j]);
				JButton button = new JButton(s);
				button.addActionListener(e -> {
					textField.setText(textField.getText() + s);
				});

				rowPanel.add(button);
			}

			keyboardPanel.add(rowPanel);
		}

		panel.add(keyboardPanel, BorderLayout.CENTER);
		add(panel);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			OnScreenKeyboard kb = new OnScreenKeyboard();
			kb.setTitle("On-Screen Keyboard");
			kb.setSize(512, 192);
			kb.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			kb.setVisible(true);
		});
	}
}
