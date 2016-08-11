package page_rank;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ParseFile {
	
	/**
	 * Find terms of current file.
	 * @param path file
	 * @return
	 */
	public Pages parse(String path) {
		InputStream is = null;
		Pages pages=new Pages();
		try {
			is = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 512);
			String l=reader.readLine();
			int docNum=Integer.parseInt(l);
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				String[] names=line.split(" ");
				pages.put(names[0], names[1]);
			}
			return pages;
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
