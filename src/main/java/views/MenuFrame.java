package views;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

import auth.ConnectionMongo;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Component;

public class MenuFrame extends JFrame {

	private JPanel contentPane;
	private ConnectionMongo connection;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					MenuFrame frame = new MenuFrame();
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
	

	
	
	public MenuFrame(final ConnectionMongo connection) {
		this.connection = connection;
		setTitle("Welcome "+ connection.username());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		final JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, contentPane);
		JButton btnNewButton = new JButton("Diagnostic Tweets");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MongoCollection<Document> collection = connection.database().getCollection("diagnosticTweets");
				DiagnosticTweetsFrame diagnosticFrame = new DiagnosticTweetsFrame(collection,connection);
				f.dispose();
				diagnosticFrame.setVisible(true);
				
			}
		});
		btnNewButton.setBounds(12, 98, 184, 60);
//		btnNewButton.setBorder(null);
		btnNewButton.setMargin(new Insets(0,0,0,0));
		contentPane.add(btnNewButton);
		
		JButton btnSleep = new JButton("Sleep Related Tweets");
		btnSleep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MongoCollection<Document> collection = connection.database().getCollection("testCollection");
				SleepTweetsFrame sleepTweetsFrame = new SleepTweetsFrame(collection,connection);
				f.dispose();
				sleepTweetsFrame.setVisible(true);
			}
		});
		btnSleep.setActionCommand("Sleep");
		btnSleep.setMargin(new Insets(0, 0, 0, 0));
		btnSleep.setBounds(232, 98, 184, 60);
		contentPane.add(btnSleep);
	}
}