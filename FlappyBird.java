import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class FlappyBird extends JFrame{
	public FlappyBird() {
		setTitle("Flappy Bird");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(800, 600);
		setResizable(false);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				FlappyBird game =new FlappyBird();
				game.setVisible(true);
			}
		});
	}
}
