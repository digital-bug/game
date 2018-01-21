package game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Othello {
	private static class FotmatErrorException extends Exception {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * 게임에 사용되는 좌표 행렬의 좌표 (0 to 7) 및 코드(Row is 1 to 8 and column is a to h)
	 * 
	 * @author home
	 *
	 */
	private static class Point {
		public int row;
		public int col;

		public Point(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public Point(String code) throws FotmatErrorException {
			if (code.matches("[1-8][a-hA-H]")) {
				row = code.charAt(0) - '1';
				col = code.toLowerCase().charAt(1) - 'a';

			} else if (code.matches("[a-hA-H][1-8]")) {
				row = code.charAt(1) - '1';
				col = code.toLowerCase().charAt(0) - 'a';
			} else
				throw new FotmatErrorException();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Point) {
				Point target = (Point) obj;
				if (target.row == row && target.col == col) {
					return true;
				}
			}
			return false;
		}

		@Override
		public String toString() {
			return "[" + row + "," + col + "]";
		}

		public String toFormatedString() {
			return "[" + (row + 1) + "," + (char) (col + 'a') + "]";
		}

	}

	private int[][] pad;
	private List<Point> blackChain;
	private List<Point> whiteChain;

	private final int[][] direction = { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
			{ -1, 1 } };

	public void init() {
		pad = new int[8][8];

		pad[3][3] = pad[4][4] = 1;
		pad[3][4] = pad[4][3] = -1;

		blackChain = new LinkedList<>();
		whiteChain = new LinkedList<>();
	}

	public boolean setStone(int row, int col, int tern) {
		if (tern == -1) {
			blackChain.add(new Point(row, col));
		} else {
			whiteChain.add(new Point(row, col));
		}
		if (pad[row][col] != 0)
			return false;
		if (reverseStone(row, col, tern, true) == 0)
			return false;
		pad[row][col] = tern;
		reverseStone(row, col, tern, false);
		return true;
	}

	private boolean setStone(Point p, int tern) {
		return setStone(p.row, p.col, tern);
	}

	public List<Point> getNextPos(int tern) {
		List<Point> result = new LinkedList<>();

		for (int i = 0; i < pad.length; i++) {
			for (int j = 0; j < pad[i].length; j++) {
				if (pad[i][j] == 0) {
					for (int j2 = 0; j2 < direction.length; j2++) {
						int row = i + direction[j2][0];
						int col = j + direction[j2][1];
						if (row < 0 || row >= pad.length || col < 0 || col >= pad[0].length || pad[row][col] == -tern) {
							if (reverseStone(i, j, tern, true) > 0) {
								result.add(new Point(i, j));
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

	private int checkWinner() {
		int blackCnt = 0;
		int whiteCnt = 0;

		for (int i = 0; i < pad.length; i++) {
			for (int j = 0; j < pad[i].length; j++) {
				switch (pad[i][j]) {
				case -1:
					blackCnt++;
					break;
				case 1:
					whiteCnt++;
					break;
				}
			}
		}

		return blackCnt > whiteCnt ? -1 : blackCnt < whiteCnt ? 1 : 0;
	}

	/**
	 * 종료 조건 확인 - handled in main method
	 * 
	 * @return
	 */
	private boolean isFinished() {

		return false;
	}

	public static void main(String[] args) throws IOException {
		int[][] weight = new int[8][8];

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("othello_win_weight.map"))) {
			weight = (int[][]) ois.readObject();
		} catch (FileNotFoundException | ClassNotFoundException e) {
			for (int i = 0; i < weight.length; i++) {
				for (int j = 0; j < weight[i].length; j++) {
					if ((i == 3 || i == 4) && (j == 3 || j == 4))
						continue;
					weight[i][j] = 1;
				}
			}
		}

		int blackWinCnt = 0;
		int whiteWinCnt = 0;
		int drawCnt = 0;

		for (int x = 0; x < 1000; x++) {

			int tern = -1;
			int last_tern = 1;
			Othello othello = new Othello();
			othello.init();

			Random r = new Random();

			try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
				while (!othello.isFinished()) {
					// try {
					// othello.printPad();

					// System.out.println(">> tern: " + tern);

					List<Point> l = othello.getNextPos(tern);

					if (l.size() > 0) {
						if (tern == -1) {
							// for (Position is : l) {
							// System.out.print(is.toFormatedString() + "\t");
							// }
							// String input = br.readLine();
							//
							// if (input.trim().length() == 0)
							// continue;
							// if (input.trim().substring(0, 1).equalsIgnoreCase("Q"))
							// break;
							//
							// Position p = new Position(input);
							// if (!l.contains(p))
							// continue;
							//
							// if (!othello.setStone(p, tern))
							// continue;
							// List<Integer> weightList = new ArrayList<>(l.size());
							// int tot = 0;
							// for (Point p : l) {
							// tot += weight[p.row][p.col];
							// weightList.add(tot);
							// }
							// int total = weightList.get(weightList.size() - 1);
							//
							// int rnd = r.nextInt(total);
							//
							// int size = weightList.size();
							// int idx;
							// for (idx = 0; idx < size; idx++) {
							// if (rnd < weightList.get(idx))
							// break;
							// }
							// // System.out.println(idx + " / " + weightList.size());
							//
							// Point point = l.get(idx);
							Point point = l.get(r.nextInt(l.size()));
							// System.out.println(point);
							if (!othello.setStone(point.row, point.col, tern))
								continue;
						} else {
							// List<Integer> weightList = new ArrayList<>(l.size());
							// int tot = 0;
							// for (Point p : l) {
							// tot += weight[p.row][p.col];
							// weightList.add(tot);
							// }
							// int total = weightList.get(weightList.size() - 1);
							//
							// int rnd = r.nextInt(total);
							//
							// int size = weightList.size();
							// int idx;
							// for (idx = 0; idx < size; idx++) {
							// if (rnd < weightList.get(idx))
							// break;
							// }
							// // System.out.println(idx + " / " + weightList.size());
							//
							// Point point = l.get(idx);
							Point point = l.get(r.nextInt(l.size()));
							// System.out.println(point);
							if (!othello.setStone(point.row, point.col, tern))
								continue;
						}
						last_tern = tern;
					} else if (last_tern == tern) {
						break;
					}

					tern = -tern;
					// } catch (FotmatErrorException e) {
					// }
				}
			}
			switch (othello.checkWinner()) {
			case -1:
				System.out.println("Black Win.");
				for (Point p : othello.blackChain) {
					weight[p.row][p.col] += 2;
				}
				for (Point p : othello.whiteChain) {
					weight[p.row][p.col]--;
				}
				blackWinCnt++;
				break;
			case 0:
				System.out.println("Draw.");
				drawCnt++;
				break;
			case 1:
				System.out.println("White Win.");
				for (Point p : othello.whiteChain) {
					weight[p.row][p.col] += 2;
				}
				for (Point p : othello.blackChain) {
					weight[p.row][p.col]--;
				}
				whiteWinCnt++;
				break;
			}
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (int i = 0; i < weight.length; i++) {
				for (int j = 0; j < weight[i].length; j++) {
					if ((i == 3 || i == 4) && (j == 3 || j == 4))
						continue;
					min = Double.min(min, weight[i][j]);
					max = Double.max(max, weight[i][j]);
				}
			}

			System.out.println(max + " / " + min);

			for (int i = 0; i < weight.length; i++) {
				for (int j = 0; j < weight[i].length; j++) {
					if ((i == 3 || i == 4) && (j == 3 || j == 4))
						continue;
					weight[i][j] = (int) (((double) weight[i][j] - min) * 10000.0 / (max - min) + 1.0);
				}
			}
			for (int i = 0; i < weight.length; i++) {
				System.out.println(Arrays.toString(weight[i]));
			}
		}

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("othello_win_weight.map"));
		oos.writeObject(weight);
		oos.close();

		System.out.println("Black: " + blackWinCnt + ", Draw: " + drawCnt + ", white: " + whiteWinCnt);
	}
}
