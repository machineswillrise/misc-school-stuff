import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Supplier;

enum Cell {
	X('X'),
	O('O'),
	BLANK(' ');

	private final char symbol;

	Cell(char symbol) {
		this.symbol = symbol;
	}

	public char getSymbol() {
		return symbol;
	}
}

record Position(int row, int col) {
	public boolean isValid() {
		if (row() < 0 || row() >= 3) {
			return false;
		}
		if (col() < 0 || col() >= 3) {
			return false;
		}

		return true;
	}
}

class Board {
	private final char[][] board;

	//Create a Tic-Tac-Toe board by filling it with spaces
	public Board () {
		board = new char[3][3];
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				board[row][col] = Cell.BLANK.getSymbol();
			}
		}
	}

	public void setPiece(Position pos, Cell cell) {
		board[pos.row()][pos.col()] = cell.getSymbol();
	}

	public char getPiece(Position pos) {
		return board[pos.row()][pos.col()];
	}

	public Position randomPiece() {
		Random rand = new Random();
		int row,col;
		do {
			row = rand.nextInt(3);
			col = rand.nextInt(3);
		} while (isOccupied(new Position(row, col)));
		return new Position(row, col);
	}

	public boolean isOccupied(Position pos) {
		return board[pos.row()][pos.col()] != Cell.BLANK.getSymbol();
	}

	public boolean isFull() {
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				if (board[row][col] == Cell.BLANK.getSymbol()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isSameCell(char cell1, char cell2, char cell3) {
		// Ignore blank cells with spaces, only ones with X or O will count
		return cell1 != Cell.BLANK.getSymbol() && cell1 == cell2 && cell2 == cell3;
	}

	public Optional<Character> theWinner() {
		//Look at each row
		for (int row = 0; row < 3; row++) {
			// X X X
			if (isSameCell(board[row][0], board[row][1], board[row][2])) {
				return Optional.of(board[row][0]);
			}
		}

		// Look at each column
		for (int col = 0; col < 3; col++) {
			// X
			// X
			// X
			if (isSameCell(board[0][col], board[1][col], board[2][col])) {
				return Optional.of(board[0][col]);
			}
		}

		// Look Diagonally
		//   X
		//  X
		// X
		if (isSameCell(board[0][0], board[1][1], board[2][2])) {
			return Optional.of(board[0][0]);
		}

		// X
		//  X
		//   X
		if (isSameCell(board[0][2], board[1][1], board[2][0])) {
			return Optional.of(board[0][2]);
		}
		return Optional.empty();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				s.append(board[row][col]);
			}
			s.append("\n");
		}

		return s.toString();
	}
}

class CPU {
	private Board board;
	private static final Random RANDOM = new Random();
	
	public CPU(Board board) {
		this.board = board;
	}

	public void refreshCPU(Board board) {
		this.board = board;
	}

	// Try to fill in the center piece; it's the best piece.
	private Optional<Position> determineCenterPiece() {
		Position centerPiece = new Position(1, 1);
		if (!board.isOccupied(centerPiece)) {
			return Optional.of(centerPiece);
		}
		return Optional.empty();
	}

	//Helper for finding unoccupied positions
	private Position[] findUnoccupiedPositions(Position[] positions) {
		int count = 0;
		for (Position position : positions) {
			if (!board.isOccupied(position)) {
				count++;
			}
		}

		Position[] unoccupiedPositions = new Position[count];
		int index = 0;
		for (Position position : positions) {
			if (!board.isOccupied(position)) {
				unoccupiedPositions[index++] = position;
			}
		}
		return unoccupiedPositions;
	}

	// Find a corner which is the next best option after the center
	private Optional<Position> findACorner() {
		Position[] corners = {
			new Position(0, 0),
			new Position(0, 2),
			new Position(2, 0),
			new Position(2, 2)
		};

		Position[] unoccupiedCorners = findUnoccupiedPositions(corners);
		if (unoccupiedCorners.length > 0) {
			return Optional.of(unoccupiedCorners[
				RANDOM.nextInt(unoccupiedCorners.length)]);
		}

		return Optional.empty();
	}

	// Choose a side space which will be the last option besides a random one
	private Optional<Position> getSideSpace() {
		Position[] sides = {
			new Position(0, 1),
			new Position(1, 0),
			new Position(1, 2),
			new Position(2, 1)
		};

		Position[] unoccupiedSides = findUnoccupiedPositions(sides);
		if (unoccupiedSides.length > 0) {
			return Optional.of(unoccupiedSides[
				RANDOM.nextInt(unoccupiedSides.length)]);
		}

		return Optional.empty();
	}

	// Generate a random position on the grid without anything in it
	private Optional<Position> randNotOccupied() {
		int attempts = 0;
		while (attempts < 10) {
			int row = RANDOM.nextInt(3);
			int col = RANDOM.nextInt(3);
			Position pos = new Position(row, col);
			if (!board.isOccupied(pos)) {
				return Optional.of(pos);
			}
			attempts++;
		}
		return Optional.empty();
	}

	//Find an attempt to block the O player
	private Optional<Position> findBlock() {
		// Check rows
		for (int row = 0; row < 3; row++) {
			if (checkLineForBlock(board.getPiece(new Position(row, 0)),
				board.getPiece(new Position(row, 1)),
				board.getPiece(new Position(row, 2)))) {
				return findEmptyInRow(row);
			}
		}
		// Check columns
		for (int col = 0; col < 3; col++) {
			if (checkLineForBlock(board.getPiece(new Position(0, col)),
				board.getPiece(new Position(1, col)),
				board.getPiece(new Position(2, col)))) {
				return findEmptyInCol(col);
			}
		}
		// Check diagonals
		if (checkLineForBlock(board.getPiece(new Position(0, 0)),
				board.getPiece(new Position(1, 1)),
				board.getPiece(new Position(2, 2)))) {
			return findEmptyInDiagonal(true);
		}
		if (checkLineForBlock(board.getPiece(new Position(0, 2)),
				board.getPiece(new Position(1, 1)),
				board.getPiece(new Position(2, 0)))) {
			return findEmptyInDiagonal(false);
		}
		return Optional.empty();
	}

	private Optional<Position> findEmptyInRow(int row) {
		for (int col = 0; col < 3; col++) {
			Position pos = new Position(row, col);
			if (!board.isOccupied(pos)) {
				return Optional.of(pos);
			}
		}
		return Optional.empty();
	}

	private Optional<Position> findEmptyInCol(int col) {
		for (int row = 0; row < 3; row++) {
			Position pos = new Position(row, col);
			if (!board.isOccupied(pos)) {
				return Optional.of(pos);
			}
		}
		return Optional.empty();
	}

	private Optional<Position> findEmptyInDiagonal(boolean mainDiagonal) {
		if (mainDiagonal) {
			for (int i = 0; i < 3; i++) {
				Position pos = new Position(i, i);
				if (!board.isOccupied(pos)) {
					return Optional.of(pos);
				}
			}
		} else {
			Position[] positions = {new Position(0, 2), new Position(1, 1), new Position(2, 0)};
			for (Position pos : positions) {
				if (!board.isOccupied(pos)) {
					return Optional.of(pos);
				}
			}
		}
		return Optional.empty();
	}

	private boolean checkLineForBlock(char c1, char c2, char c3) {
		// Return true if two are 'O' and one is blank
		int oCount = 0, blankCount = 0;
		for (char c : new char[]{c1, c2, c3}) {
			if (c == 'O') oCount++;
			if (c == ' ') blankCount++;
		}
		return oCount == 2 && blankCount == 1;
	}

	public Position determineCPUBehavior() {
		List<Supplier<Optional<Position>>> strategies = Arrays.asList(
			this::determineCenterPiece,
			this::findACorner,
			this::getSideSpace,
			this::findBlock,
			this::randNotOccupied
		);

		for (Supplier<Optional<Position>> strategy : strategies) {
			Optional<Position> piece = strategy.get();
			if (piece.isPresent()) {
				return piece.get();
			}
		}

		throw new IllegalStateException("No available positions to fill");
	}
}

public class TicTacToe {
	private static final Scanner scanner = new Scanner(System.in);
	private static final Board board = new Board();

	private static Position askPosition() throws NumberFormatException {
		System.out.print("Enter position (row, col): ");
		String input = scanner.nextLine();

		String[] parts;
		if (input.contains(", ")) {
			parts = input.split(", ");
		} else if (input.contains(",")) {
			parts = input.split(",");
		} else {
			throw new NumberFormatException("You forgot a comma.");
		}

		int firstPart = Integer.parseInt(parts[0]);
		int secondPart = Integer.parseInt(parts[1]);

		// Arrays are indexed by zero but the user probably wants to use 1-based indexing
		Position position = new Position(firstPart - 1, secondPart - 1);
		if (position.isValid() && !board.isOccupied(position)) {
			return position;
		}

		throw new NumberFormatException("Your position is outside the board!");
	}

	private static Position getValidPosition() {
		while (true) {
			try {
				return askPosition();
			} catch (NumberFormatException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		try (scanner) {
			Cell player = Cell.X;
			CPU cpu = new CPU(board);

			while (true) {
				Position newPosition;
				if (player == Cell.X) {
					System.out.println(board);
					newPosition = getValidPosition();
				} else {
					System.out.println("Computer did their turn.");
					newPosition = cpu.determineCPUBehavior();
					cpu.refreshCPU(board);
				}

				board.setPiece(newPosition, player);
				if (board.theWinner().isPresent()) {
					System.out.println(board.theWinner().get() + " wins!");
					break;
				}

				if (board.isFull()) {
					System.out.println("It's a draw!");
					break;
				}

				// Swap the player
				player = player == Cell.X ? Cell.O : Cell.X;
			}
		}
	}
}
