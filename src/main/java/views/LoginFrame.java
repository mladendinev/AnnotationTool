package views;

import auth.ConnectionMongo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame{

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
		btnLogin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
		        if(e.getKeyCode()== KeyEvent.VK_ENTER)
					try {
						establishConnection();
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, "Wrong Credentials or missing VPN connection");
//						e1.printStackTrace();
					}
			}
		});
		
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{

					establishConnection();
				}	
				catch (Exception error){
					JOptionPane.showMessageDialog(null, "Wrong Credentials or missing VPN connection");
//					throw new RuntimeException();
					
				}
			}
		});
		btnLogin.setBounds(95, 111, 117, 25);
		contentPane.add(btnLogin);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(132, 66, 114, 19);
		contentPane.add(passwordField);
	}
	
	private void establishConnection() throws Exception{
		String getUserName = loginField.getText();
		char[] getPassword = passwordField.getPassword();
		ConnectionMongo connection = new ConnectionMongo(getUserName, getPassword);
		connection.authenticate();
		JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, contentPane);
		f.setVisible(false);
		
		MenuFrame menuFrame = new MenuFrame(connection);
		menuFrame.setVisible(true);
	}
	
	
	  private class CustomKeyListener extends KeyAdapter{
		  
	      public void keyTyped(KeyEvent e) {           
	      }
	      @Override
	      public void keyPressed(KeyEvent e) {
	         if(e.getKeyCode() == KeyEvent.VK_ENTER){
	            System.out.println("Heurrica!");
	         }
	      }

	      public void keyReleased(KeyEvent e) {            
	      }    
	   } 
}
