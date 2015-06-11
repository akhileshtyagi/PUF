package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import test.Main;
import test.Print_model;
import data_analysis.Model_compare;

public class Marcov_options_panel extends JPanel{
	private static final long serialVersionUID = 752127986893523050L;

	public Marcov_options_panel(){
		super();
		
		//TODO add all components
		this.add(new Test_speed_button());
		this.add(new Test_correct_button());
		this.add(new Print_model_button());
		this.add(new Compare_all_data_sets_button());
		this.add(new Exit_button());
	}
	
	
	private class Test_speed_button extends JButton{
		private static final long serialVersionUID = 467640578917331878L;
		
		public Test_speed_button(){
			super();
			
			this.setText("test speed");
			
			this.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Main.main(new String[]{"0"});
				}
			});
		}
	}
	
	
	private class Test_correct_button extends JButton{
		private static final long serialVersionUID = -806539564074572351L;
		
		public Test_correct_button(){
			super();
			
			this.setText("test correctness");
			
			this.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Main.main(new String[]{"1"});
				}
			});
		}
	}
	
	
	private class Print_model_button extends JButton{
		private static final long serialVersionUID = -6925310621000268374L;
		
		public Print_model_button(){
			super();
			
			this.setText("print model");
			
			this.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Print_model.main(null);
				}
			});
		}
	}
	
	
	private class Compare_all_data_sets_button extends JButton{
		private static final long serialVersionUID = -2269980099722196699L;
		
		public Compare_all_data_sets_button(){
			super();
			
			this.setText("compare all data sets");
			
			this.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					Model_compare.main(null);;
				}
			});
		}
	}
	
	
	private class Exit_button extends JButton{
		private static final long serialVersionUID = -2369980099722196699L;
		
		public Exit_button(){
			super();
			
			this.setText("exit");
			
			this.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					StartGUI.exit();
				}
			});
		}
	}
}
