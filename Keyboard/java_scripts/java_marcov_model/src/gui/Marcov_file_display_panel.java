package gui;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

//This class knows how to display files
//TODO this class will display the readme, and test output files in a readable format
public class Marcov_file_display_panel extends JPanel{
	private static final long serialVersionUID = 1L;

	public Marcov_file_display_panel(){
		super();
		
		// add all components
		this.add(new File_tabs());
	}
	
	
	///read a file into a string given File object
	private String file_to_string(File file){
		Scanner readme_scan;
		String text="";
		
		//read in the readme file into a string
		try {
			readme_scan = new Scanner(file);
			
			while(readme_scan.hasNextLine()){
				text += (readme_scan.nextLine() + "\n");
			}
			
			readme_scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return text;
	}
	
	
	private class File_tabs extends JTabbedPane{
		private static final long serialVersionUID = 1L;
		
		public File_tabs(){
			super(JTabbedPane.LEFT);
			
			this.setPreferredSize(new Dimension(700,300));
			
			//add components
			this.addTab("readme", new File_scroll_pane("readme.md"));
			this.addTab("model_compare_output", new File_scroll_pane("model_compare_output.txt"));
			this.addTab("print_model_output", new File_scroll_pane("print_model_output.txt"));
			this.addTab("test_model_construction", new File_scroll_pane("test_model_construction.txt"));
		}

		private class File_scroll_pane extends JScrollPane{
			public File_scroll_pane(String file_name){
				super();
				
				//add the readme text aread and add stuff to it
				this.setViewportView(new Readme_text_area(file_name));
			}
			
			
			private class Readme_text_area extends JTextArea{
				String file_name;
				
				public Readme_text_area(String file_name){
					super();
					this.file_name = file_name;
					String text="";
					File readme_file = new File(file_name);
					
					text = file_to_string(readme_file); 
					
					// set the text of this text area based on the string
					this.setText(text);
				}
				
				
				public void refresh_data(){
					String text="";
					File readme_file = new File(file_name);
					
					text = file_to_string(readme_file); 
					
					// set the text of this text area based on the string
					this.setText(text);
				}
			}
		}
	}
}
