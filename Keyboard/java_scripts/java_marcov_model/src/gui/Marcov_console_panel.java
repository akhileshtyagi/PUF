package gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//TODO this class will display all message going to std_out
public class Marcov_console_panel extends JPanel {
	private static final long serialVersionUID = 8300665647341796612L;

	public Marcov_console_panel(){
		super();
		
		//TODO add all components
		this.add(new Console_scroll_pane());
	}
	
	
	private class Console_scroll_pane extends JScrollPane{
		private static final long serialVersionUID = 1251450592537563775L;
		
		public Console_scroll_pane(){
			super();
			this.setViewportView(new Console_text_area());
		}
		
		
		private class Console_text_area extends JTextArea{
			Updating_output_stream text_stream;
			
			public Console_text_area(){
				super(1000,1000);
				
				//redirect std_out to text_stream
				//text_stream= new Updating_output_stream(this);
				//System.setOut(new PrintStream(text_stream));
				
				//System.out.println("hi");
				//System.setOut(System.out);
				//System.out.println(text_stream.toString());
				//this.setEditable(false);
			}
	
			
			private class Updating_output_stream extends OutputStream {
			    private JTextArea textArea;
			     
			    public Updating_output_stream(JTextArea textArea) {
			        this.textArea = textArea;
			    }
			     
			    @Override
			    public void write(int b) throws IOException {
			        // redirects data to the text area
			        textArea.append(String.valueOf((char)b));
			        // scrolls the text area to the end of data
			        textArea.setCaretPosition(textArea.getDocument().getLength());
			    }
			}
		}
	}
}
