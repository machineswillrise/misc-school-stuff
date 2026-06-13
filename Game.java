import java.util.Random;

// unfortunately, java chars cannot hold emojis, so we use strings
record Item(int row, int col, String symbol) {
	public Item {
		if (row < 0 || row > 2 || col < 0 || col > 2) {
			throw new IllegalArgumentException("Invalid position");
		}
	}
}

class Refrigerator {
	private boolean isOpen;
	private String[][] shelf;

	public Refrigerator() {
		this.isOpen = false;
		this.shelf = new String[3][3];
		empty();
	}

	public void empty() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				shelf[i][j] = "❌";
			}
		}
	}

	public void cycle() {
		isOpen = !isOpen;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public Item find(int row, int col) {
		return new Item(row, col, shelf[row][col]);
	}

	public void add(Item item) {
		shelf[item.row()][item.col()] = item.symbol();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				b.append(shelf[i][j]);
			}
			b.append('\n');
		}
		return b.toString();
	}
}

class Game {
	private static void addItems(Refrigerator refrigerator) {
		Random rand = new Random();

		Item[] items = {
			new Item(rand.nextInt(3), rand.nextInt(3), "🍎"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🍌"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🍇"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🍓"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🍊"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🍋"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🍉"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🍍"),
			new Item(rand.nextInt(3), rand.nextInt(3), "🥝")
		};

		for (Item item : items) {
			refrigerator.add(item);
		}
	}

	public static void main(String[] args) {
		Refrigerator refrigerator = new Refrigerator();
		addItems(refrigerator);
		refrigerator.cycle();

		System.out.println(refrigerator.toString());
		if (refrigerator.isOpen()) {
			System.out.println("The refrigerator is open");
		} else {
			System.out.println("The refrigerator is closed");
		}
	}
}