package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;

/** Display frame to contain buttons for running test code and panels to view results. */
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
		//LayoutManager layout = new GridLayout(0,1,10,10);
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.setLayout(layout);
		
		//define constraints
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx=1.0;
		constraints.weighty=1.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
	    constraints.insets = new Insets(10, 10, 10, 10);
	    constraints.anchor = GridBagConstraints.WEST;
		
		//add components
		this.add(new Marcov_options_panel(), constraints);
		
		constraints.gridy=1;
		this.add(new Marcov_file_display_panel(), constraints);
		
		constraints.gridy=2;
		this.add(new Marcov_console_panel(), constraints);
		
		//make it visible
		this.pack();
		this.setVisible(true);
	}
	
	
	public void close(){
		this.dispose();
	}
}
