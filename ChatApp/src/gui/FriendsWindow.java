package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.List;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FriendsWindow {

	private JFrame frame;
	private JButton logout;
	private JButton deleteSelection;
	private JButton startConversation;
	List friends;
	List selection;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FriendsWindow window = new FriendsWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public FriendsWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Friends Window");
		
		friends = new List();
		friends.setBounds(10, 10, 285, 442);
		frame.getContentPane().add(friends);
		
		selection = new List();
		selection.setBounds(301, 10, 173, 217);
		frame.getContentPane().add(selection);
		
		
		/**
		 *  START CONVERSATION BUTTON
		 */
		startConversation = new JButton("Start Conversation");
		startConversation.setBounds(301, 233, 173, 39);
		frame.getContentPane().add(startConversation);
		
		
		/**
		 *  DELETE FROM SELECTION BUTTON
		 */
		deleteSelection = new JButton("Delete From Selection");
		deleteSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		deleteSelection.setBounds(301, 283, 173, 39);
		frame.getContentPane().add(deleteSelection);
		
		
		/**
		 *  LOGOUT BUTTON
		 */
		logout = new JButton("Logout");
		logout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		logout.setBounds(301, 413, 173, 39);
		frame.getContentPane().add(logout);
	}

}
