package views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.MongoSecurityException;
import com.mongodb.client.MongoCollection;

import auth.ConnectionMongo;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

	private JPanel contentPane;
	private JTextField loginField;
	private JPasswordField passwordField;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginFrame() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Annotation tool");
		setBounds(100, 100, 312, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel usernameText = new JLabel("Username");
		usernameText.setBounds(32, 41, 87, 15);
		contentPane.add(usernameText);
		
		JLabel passwordText = new JLabel("Password");
		passwordText.setBounds(32, 68, 97, 15);
		contentPane.add(passwordText);
		
		loginField = new JTextField();
		loginField.setBounds(132, 39, 114, 19);
		contentPane.add(loginField);
		loginField.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{	
					String getUserName = loginField.getText();
					char[] getPassword = passwordField.getPassword();
					ConnectionMongo connection = new ConnectionMongo(getUserName, getPassword);
					connection.authenticate();
					
					MongoCollection<Document> collection = connection.database().getCollection("testEncrypt");
										ViewFrame viewFrame = new ViewFrame(0, collection);
				viewFrame.setVisible(true);
					collection.count();
				}

					
				catch (Exception error){
					JOptionPane.showMessageDialog(null, "Try Again");
					throw new RuntimeException();
					
				}
			}
		});
		btnLogin.setBounds(95, 111, 117, 25);
		contentPane.add(btnLogin);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(132, 66, 114, 19);
		contentPane.add(passwordField);
	}
}
