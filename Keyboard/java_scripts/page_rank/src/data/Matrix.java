package data;

import java.util.ArrayList;
import java.util.List;

public class Matrix<E> {
	List<List<E>> matrix;

	public Matrix() {
		matrix = new ArrayList<List<E>>();
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
}
