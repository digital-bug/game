package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Othello {
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

	public List<int[]> getNextPos(int tern) {
		List<int[]> result = new LinkedList<>();

		for (int i = 0; i < pad.length; i++) {
			for (int j = 0; j < pad[i].length; j++) {
				if (pad[i][j] == 0) {
					for (int j2 = 0; j2 < direction.length; j2++) {
						try {
							int row = i + direction[j2][0];
							int col = j + direction[j2][1];
							if (pad[row][col] == -tern) {
								if (reverseStone(i, j, tern, true) > 0) {
									result.add(new int[] { i, j });
									continue;
								}
							}
						} catch (ArrayIndexOutOfBoundsException e) {
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
		System.out.print("¡¡");
		for (int i = 0; i < 8; i++) {
			System.out.print((char) (0xFF41 + i));
		}
		System.out.println();

		for (int i = 0; i < pad.length; i++) {
			System.out.print((char) (0xFF11 + i));
			for (int j = 0; j < pad[i].length; j++) {
				System.out.print(pad[i][j] == 0 ? "¡¡" : pad[i][j] < 0 ? "¡Ü" : "¡Û");
			}
			System.out.println((char) (0xFF11 + i));
		}

		System.out.print("¡¡");
		for (int i = 0; i < 8; i++) {
			System.out.print((char) (0xFF41 + i));
		}
		System.out.println();
	}

	public static void main(String[] args) throws IOException {
		int tern = -1;
		Othello othello = new Othello();
		othello.init();

		Random r = new Random();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				othello.printPad();

				if (tern == -1) {
					List<int[]> l = othello.getNextPos(tern);
					for (int[] is : l) {
						System.out.print("[" + (is[0] + 1) + ", " + (char) (is[1] + 'a') + "]\t");
					}
					String input = br.readLine();

					if (input.trim().length() == 0)
						continue;
					if (input.trim().substring(0, 1).equalsIgnoreCase("Q"))
						break;
					if (!input.matches("[1-8][a-hA-H]") && !input.matches("[a-hA-H][1-8]"))
						continue;

					int pos1 = -1;
					int pos2 = -1;
					if (input.charAt(0) > '0' && input.charAt(0) < '9') {
						pos1 = input.charAt(0) - '1';
						pos2 = input.toLowerCase().charAt(1) - 'a';
					} else {
						pos1 = input.charAt(1) - '1';
						pos2 = input.toLowerCase().charAt(0) - 'a';
					}
					if (!othello.setStone(pos1, pos2, tern))
						continue;
				} else {
					int pos1 = r.nextInt(8);
					int pos2 = r.nextInt(8);
					if (!othello.setStone(pos1, pos2, tern))
						continue;
				}

				tern = -tern;
			}
		}
	}
}
