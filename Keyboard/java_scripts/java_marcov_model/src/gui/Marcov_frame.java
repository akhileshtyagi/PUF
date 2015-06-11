package gui;

import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.JFrame;

public class Marcov_frame extends JFrame{
	private static final long serialVersionUID = 4877822104578473298L;

	public Marcov_frame(){
		super();
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//TODO set layout to look like this
		//-------------
		//|     |     |
		//| op  |file |
		//|     |     |
		//|     |     |
		//-------------
		//|  console  |
		//|           |
		//-------------
		LayoutManager layout = new GridLayout(0,1,10,10);
		this.setLayout(layout);
		
		//add components
		this.add(new Marcov_options_panel());
		this.add(new Marcov_file_display_panel());
		this.add(new Marcov_console_panel());
		
		//make it visible
		this.pack();
		this.setVisible(true);
	}
	
	
	public void close(){
		this.dispose();
	}
}
