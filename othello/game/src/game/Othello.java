package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Othello {
	public static class Position {
		public int row;
		public int col;

		public Position(int row, int col) {
			this.row = row;
			this.col = col;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Position) {
				Position target = (Position) obj;
				if (target.row == row && target.col == col) {
					return true;
				}
			}
			return false;
		}
	}

	private int[][] pad;
	private final int[][] direction = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
			{ -1, 1 } };

	public void init() {
		pad = new int[8][8];

		pad[3][3] = pad[4][4] = 1;
		pad[3][4] = pad[4][3] = -1;
	}

	public boolean setStone(int row, int col, int tern) {
		if (pad[row][col] != 0)
			return false;
		if (reverseStone(row, col, tern, true) == 0)
			return false;
		pad[row][col] = tern;
		reverseStone(row, col, tern, false);
		return true;
	}

	public List<Position> getNextPos(int tern) {
		List<Position> result = new LinkedList<>();

		for (int i = 0; i < pad.length; i++) {
			for (int j = 0; j < pad[i].length; j++) {
				if (pad[i][j] == 0) {
					for (int j2 = 0; j2 < direction.length; j2++) {
						int row = i + direction[j2][0];
						int col = j + direction[j2][1];
						if (row < 0 || row >= pad.length || col < 0 || col >= pad[0].length || pad[row][col] == -tern) {
							if (reverseStone(i, j, tern, true) > 0) {
								result.add(new Position(i, j));
								break;
							}
						}
					}
				}
			}
		}

		return result;
	}

	private int reverseStone(int row, int col, int tern, boolean isSimulation) {
		int cnt = 0;

		for (int i = 0; i < direction.length; i++) {
			boolean isClosed = false;
			boolean isCapture = false;
			int tmpRow = row;
			int tmpCol = col;

			while (true) {
				tmpRow += direction[i][0];
				tmpCol += direction[i][1];
				if (tmpRow < 0 || tmpRow >= pad.length || tmpCol < 0 || tmpCol >= pad[0].length
						|| pad[tmpRow][tmpCol] == 0)
					break;
				if (!isCapture && tern != pad[tmpRow][tmpCol]) {
					isCapture = true;
				}
				if (tern == pad[tmpRow][tmpCol]) {
					isClosed = true;
					break;
				}
			}
			if (isClosed && isCapture) {
				int reversCnt = Math.max(Math.abs(row - tmpRow), Math.abs(col - tmpCol));
				cnt += reversCnt;
				tmpRow = row;
				tmpCol = col;
				if (!isSimulation) {
					for (int j = 0; j < reversCnt; j++) {
						tmpRow += direction[i][0];
						tmpCol += direction[i][1];
						pad[tmpRow][tmpCol] = tern;
					}
				}
			}
		}

		return cnt;
	}

	public void printPad() {
		System.out.print("　");
		for (int i = 0; i < 8; i++) {
			System.out.print((char) (0xFF41 + i));
		}
		System.out.println();

		for (int i = 0; i < pad.length; i++) {
			System.out.print((char) (0xFF11 + i));
			for (int j = 0; j < pad[i].length; j++) {
				System.out.print(pad[i][j] == 0 ? "　" : pad[i][j] < 0 ? "●" : "○");
			}
			System.out.println((char) (0xFF11 + i));
		}

		System.out.print("　");
		for (int i = 0; i < 8; i++) {
			System.out.print((char) (0xFF41 + i));
		}
		System.out.println();
	}

	public static void main(String[] args) throws IOException {
		int tern = -1;
		int last_tern = 1;
		Othello othello = new Othello();
		othello.init();

		Random r = new Random();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			while (!othello.isFinished()) {
				othello.printPad();

				List<Position> l = othello.getNextPos(tern);

				if (l.size() > 0) {
					if (tern == -1) {
						for (Position is : l) {
							System.out.print("[" + (is.row + 1) + ", " + (char) (is.col + 'a') + "]\t");
						}
						String input = br.readLine();

						if (input.trim().length() == 0)
							continue;
						if (input.trim().substring(0, 1).equalsIgnoreCase("Q"))
							break;
						if (!input.matches("[1-8][a-hA-H]") && !input.matches("[a-hA-H][1-8]"))
							continue;

						int row = -1;
						int col = -1;
						if (input.charAt(0) > '0' && input.charAt(0) < '9') {
							row = input.charAt(0) - '1';
							col = input.toLowerCase().charAt(1) - 'a';
						} else {
							row = input.charAt(1) - '1';
							col = input.toLowerCase().charAt(0) - 'a';
						}

						if (!l.contains(new Position(row, col)))
							continue;

						if (!othello.setStone(row, col, tern))
							continue;
					} else {
						Position point = l.get(r.nextInt(l.size()));
						System.out.println(point);
						if (!othello.setStone(point.row, point.col, tern))
							continue;
					}
					last_tern = tern;
				} else if (last_tern == tern) {
					break;
				}

				tern = -tern;
			}
		}
	}

	private boolean isFinished() {

		return false;
	}
}
