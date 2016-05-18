package gui;

import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginWindow {

	private JFrame frame;
	private JTextField user_email;
	private JPasswordField user_password;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private TextArea login_message;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginWindow window = new LoginWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void run() {
		try {
			LoginWindow window = new LoginWindow();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public LoginWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Login Window");
		
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(150, 150, 130, 30);
		frame.getContentPane().add(btnLogin);
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("cevaaa");
				try {
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		user_email = new JTextField();
		user_email.setBounds(150, 50, 250, 20);
		frame.getContentPane().add(user_email);
		user_email.setColumns(10);
		
		
		user_password = new JPasswordField();
		user_password.setBounds(150, 100, 250, 20);
		frame.getContentPane().add(user_password);
		
		
		lblNewLabel = new JLabel("Email:");
		lblNewLabel.setBounds(50, 50, 80, 20);
		frame.getContentPane().add(lblNewLabel);
		
		
		lblNewLabel_1 = new JLabel("Password:");
		lblNewLabel_1.setBounds(50, 100, 80, 20);
		frame.getContentPane().add(lblNewLabel_1);
		
		
		login_message = new TextArea("", 3 , 100 , TextArea.SCROLLBARS_NONE);
		login_message.setBounds(150, 188, 151, 20);
		frame.getContentPane().add(login_message);
		login_message.setEditable(false);
	}
}
