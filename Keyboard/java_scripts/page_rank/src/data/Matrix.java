package data;

import java.util.ArrayList;
import java.util.List;

public class Matrix<E> {
	List<List<E>> matrix;

	public Matrix() {
		matrix = new ArrayList<List<E>>();
	}

	/**
	 * sets a specific element in the matrix to value.
	 */
	public void set(int row, int column, E value) {
		matrix.get(row).set(column, value);
	}

	/**
	 * add a row to the matrix
	 */
	public void add_row(List<E> row) {
		matrix.add(row);
	}

	/**
	 * returns the number of rows in the matrix
	 */
	public int get_rows() {
		return matrix.size();
	}

	/**
	 * returns the ith row in the matrix
	 */
	public List<E> get_row(int index) {
		return matrix.get(index);
	}

	@Override
	public String toString() {
		String s_rep = "";

		// for each row, create a line
		for (List<E> row : matrix) {
			s_rep += "[ ";

			for (E item : row) {
				s_rep += item.toString();
				s_rep += ", ";
			}

			s_rep += "]\n";
		}

		return s_rep;
	}
}
