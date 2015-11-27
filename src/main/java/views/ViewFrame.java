package views;

import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicArrowButton;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import auth.ConnectionMongo;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ViewFrame extends JFrame {

	private JPanel contentPane;
	public int count;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					ViewFrame frame = new ViewFrame();
//					
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public ViewFrame(int counter, MongoCollection<Document> coll) {
		count = counter;
		long files = coll.count();
		final JFrame frame = new JFrame("TRY");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 588, 342);	
		setTitle("Annotate data from Mongo");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		
		EtchedBorder border = new EtchedBorder();
		final JTextArea textArea = new JTextArea();
		textArea.setBorder(border);
		Font arialBolditalic12 = new Font("Arial", Font.BOLD + Font.ITALIC, 12);
		textArea.setFont(arialBolditalic12);
		textArea.setBounds(99, 49, 388, 160);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setText(Integer.toString(counter) + files);
		contentPane.add(textArea);
		
		final BasicArrowButton next = new BasicArrowButton(BasicArrowButton.EAST);
		next.setBounds(425, 12, 117, 25);
		contentPane.add(next);
		final BasicArrowButton previous = new BasicArrowButton(BasicArrowButton.WEST);
		previous.setBounds(35, 12, 117, 25);	
		contentPane.add(previous);
		previous.setVisible(false);
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				count++;
				if(count >= 0 && count<15)	
				{
					  System.out.println(count);
					  previous.setVisible(true);
					  previous.validate();
					  textArea.setText("next" + count);
				}
				else{
					System.out.println(count);
					next.setVisible(false);
					next.invalidate();
				}

			}
		});

		previous.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				count--;
				if (count<=0) 
				{
			      System.out.println("previous");
					previous.setVisible(false);
					previous.invalidate();
		
				}
				else {
					  textArea.setText("previous" + count);
					 if(count<15){
						  next.setVisible(true);
						  next.validate();
					      }
				}
				
			}
		});

		contentPane.setLayout(null);
		
		JButton postiveButton = new JButton("Positive");
		postiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("opi");
			}
		});
		postiveButton.setBounds(35, 233, 117, 25);
		contentPane.add(postiveButton);
		
		JButton negativeButton = new JButton("Negative");
		negativeButton.setBounds(440, 233, 117, 25);
		contentPane.add(negativeButton);
		
		JButton neutralButton = new JButton("Neutral");
		neutralButton.setBounds(231, 233, 117, 25);
		contentPane.add(neutralButton);
	}
	
	public int bound(){
		return count;
	}
	
	public void kurec(int numb){
		 
	     this.count = numb;

	}
}
