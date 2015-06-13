package gui;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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

		File_scroll_pane readme_pane;
		File_scroll_pane model_compare_pane;
		File_scroll_pane print_model_output_pane;
		File_scroll_pane model_construction_pane;
		
		public File_tabs(){
			super(JTabbedPane.LEFT);
			
			this.setPreferredSize(new Dimension(700,300));
			
			//create components
			readme_pane = new File_scroll_pane("readme.md");
			model_compare_pane = new File_scroll_pane("model_compare_output.txt");
			print_model_output_pane = new File_scroll_pane("print_model_output.txt");
			model_construction_pane = new File_scroll_pane("test_model_construction.txt");
			
			//add components
			this.addTab("readme", readme_pane);
			this.addTab("model_compare_output", model_compare_pane);
			this.addTab("print_model_output", print_model_output_pane);
			this.addTab("test_model_construction", model_construction_pane);
			
			//TODO find a way to tell when a tab is clicked. When the tab is clicked, call update data in tab;
			this.addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent arg0) {
					// TODO Auto-generated method stub
					refresh_data();
				}
			});
		}
		
		
		///called when tabs are changed; refreshes data from the files
		private void refresh_data(){
			//ideally this would happen only if we switched to the tab containing the individual file, but this is not an ideal world.
			readme_pane.refresh_data();
			model_compare_pane.refresh_data();
			print_model_output_pane.refresh_data();
			model_construction_pane.refresh_data();
		}
		

		private class File_scroll_pane extends JScrollPane{
			Readme_text_area text_area;
			
			public File_scroll_pane(String file_name){
				super();
				
				text_area = new Readme_text_area(file_name);
				
				//add the readme text aread and add stuff to it
				this.setViewportView(text_area);
			}
			
			
			//calls refresh data on the text area
			public void refresh_data(){
				text_area.refresh_data();
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
