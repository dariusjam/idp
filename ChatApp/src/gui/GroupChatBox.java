package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.TextArea;
import javax.swing.JButton;
import java.awt.List;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GroupChatBox {

	private JFrame frame;
	private JTextField filePath;
	private List groupList;
	private JButton btnSend;
	private JButton btnShare;
	private TextArea messages;
	private TextArea toSend;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GroupChatBox window = new GroupChatBox();
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
	public GroupChatBox() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Group Chat");
		
		
		/**
		 *  RECEIVED MESSAGES
		 */
		messages = new TextArea("", 3 , 100 , TextArea.SCROLLBARS_VERTICAL_ONLY);
		messages.setBounds(10, 10, 300, 255);
		frame.getContentPane().add(messages);
		messages.setEditable(false);
		
		
		/**
		 *  MESSAGE TO SEND
		 */
		toSend = new TextArea("", 3 , 100 , TextArea.SCROLLBARS_VERTICAL_ONLY);
		toSend.setBounds(10, 297, 359, 55);
		frame.getContentPane().add(toSend);
		
		
		/**
		 *  SEND BUTTON
		 */
		btnSend = new JButton("Send");
		btnSend.setBounds(375, 297, 99, 55);
		frame.getContentPane().add(btnSend);
		
		
		/**
		 *  THE LIST WITH THE GROUP MEMBERS
		 */
		groupList = new List();
		groupList.setBounds(316, 10, 158, 255);
		frame.getContentPane().add(groupList);
		groupList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		
		/**
		 *  FILE PATH
		 */
		filePath = new JTextField();
		filePath.setBounds(10, 271, 359, 20);
		frame.getContentPane().add(filePath);
		filePath.setColumns(10);
		
		
		/**
		 *  SHARE BUTTON
		 */
		btnShare = new JButton("Share");
		btnShare.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnShare.setBounds(375, 270, 99, 22);
		frame.getContentPane().add(btnShare);
	}
}
