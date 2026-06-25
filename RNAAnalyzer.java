import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class RNA {
	private final String sequence;

	public RNA(String sequence) {
		this.sequence = sequence;
	}

	public String getSequence() {
		return sequence;
	}

	public List<String> toDnaSegments() {
		List<String> result = new ArrayList<>();
		int length = sequence.length();

		for (int i = 0; i < length; i += 3) {
			result.add(sequence.substring(i, Math.min(length, i + 3)));
		}

		return result;
	}
}

enum Codon {
	PHENYALANINE("Phenylanine"),
	LEUCINE("Leucine"),
	ISOLEUCINE("Isoleucine"),
	METHIONINE("Methionine"),
	VALINE("Valine"),
	TYROSINE("Tyrosine"),
	STOP("Stop"),
	HISTIDINE("Histidine"),
	GLUTAMINE("Glutamine"),
	ASPARAGINE("Asparagine"),
	LYSINE("Lysine"),
	ASPARTIC_ACID("Aspartic acid"),
	GLUTAMIC_ACID("Glutamic acid"),
	CYSTEINE("Cysteine"),
	ARGININE("Arginine"),
	GLYCINE("Glycine"),
	UNKNOWN("Unknown");

	public static Codon fromDnaSegment(String dnaSegment) {
		return switch (dnaSegment) {
			case "UUU", "UUC"               -> PHENYALANINE;
			case "UUA", "UUG"               -> LEUCINE;
			case "CUU", "CUC", "CUA", "CUG" -> LEUCINE;
			case "AUU", "AUC", "AUA"        -> ISOLEUCINE;
			case "AUG"                      -> METHIONINE;
			case "GUU"                      -> VALINE;
			case "UAU", "UAC"               -> TYROSINE;
			case "UAA", "UAG", "UGA"        -> STOP;
			case "CAU", "CAC"               -> HISTIDINE;
			case "CAA", "CAG"               -> GLUTAMINE;
			case "AAU", "AAC"               -> ASPARAGINE;
			case "AAA", "AAG"               -> LYSINE;
			case "GAU", "GAC"               -> ASPARTIC_ACID;
			case "GAA", "GAG"               -> GLUTAMIC_ACID;
			case "UGU", "UGC"               -> CYSTEINE;
			case "CGU", "CGC", "CGA", "CGG" -> ARGININE;
			case "GGU", "GGC", "GGA", "GGG" -> GLYCINE;	
			default                         -> UNKNOWN;
		};
	}

	private final String name;

	private Codon(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

class RNAAnalyzer {
	private static final Map<Character, Character> CONVERSIONS = new HashMap<>() {{
		put('A', 'U');
		put('C', 'G');
		put('T', 'A');
		put('G', 'C');
	}};

	private static final JFrame frame = new JFrame();
	private static final JPanel panel = new JPanel();

	private static String formatCodons(List<String> segments) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < segments.size(); i++) {
			String segment = segments.get(i);
			String codon = Codon.fromDnaSegment(segment).getName();
			result.append(codon);

			if (codon.equals(Codon.STOP.getName())) {
				result.append("\n");
				break;
			}

			if (i < segments.size() - 1) {
				result.append(" - ");
			} else {
				result.append("\n");
			}
		}

		return result.toString();
	}

	private static void add(Box box, JComponent... components) {
		for (JComponent component : components) {
			box.add(component);
		}
	}

	private static void setUpPanel(JPanel panel) {
		Box box = Box.createVerticalBox();
		JTextField rna = new JTextField(20);
		JButton ok = new JButton("OK");
		JLabel dna = new JLabel("DNA sequence: ");
		JLabel segments = new JLabel("Segments: ");

		add(box, rna, ok, dna, segments);
		panel.add(box);

		ok.addActionListener(e -> {
			RNA rnaObject = new RNA(rna.getText());
			char[] sequence = rnaObject.getSequence().toCharArray();
			char[] result = new char[sequence.length];

			boolean valid = true;
			for (int i = 0; i < sequence.length; i++) {
				char current = sequence[i];
				if (!CONVERSIONS.containsKey(current)) {	
					dna.setText("Invalid DNA sequence");
					valid = false;
					break;
				}

				result[i] = CONVERSIONS.get(current);
			}

			String fullResult = new String(result);
			if (valid) {
				dna.setText(fullResult);
			}

			List<String> segmentsList = rnaObject.toDnaSegments();
			segments.setText("Segments: " + formatCodons(segmentsList));
		});
	}

	public RNAAnalyzer() {
		frame.setSize(384, 128);
		frame.setTitle("RNA Analyzer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setUpPanel(panel);
		frame.add(panel);
	}

	private void makeVisible() {
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			RNAAnalyzer RNA = new RNAAnalyzer();
			RNA.makeVisible();
		});
	}
}
