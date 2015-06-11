package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;

//This class knows how to display files
//TODO this class will display the readme, and test output files in a readable format
public class Marcov_file_display_panel extends JPanel{
	private static final long serialVersionUID = 1L;

	public Marcov_file_display_panel(){
		super();
		
		//TODO add all components
		this.add(new File_tabs());
	}
	
	
	private class File_tabs extends JTabbedPane{
		private static final long serialVersionUID = 1L;
		
		public File_tabs(){
			this.addTab("readme", new Readme_scroll_pane());
		}
		
		private class Readme_scroll_pane extends JScrollPane{
			public Readme_scroll_pane(){
				super(1000,1000);
				
				//add the readme text aread and add stuff to it
				this.setViewportView(new Readme_text_area());
			}
			
			
			private class Readme_text_area extends JViewport{
				public Readme_text_area(){
					super();
					//TODO read in the readme file
				}
			}
		}
	}
}
