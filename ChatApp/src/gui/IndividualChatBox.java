package gui;


import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class IndividualChatBox {

	private JFrame frame;
	private TextArea messages;
	private TextArea toSend;
	private JTextField filePath;
	private JButton btnShare;
	private JButton btnSend;
	private JTextField download;
	private JButton btnDownload;
	private JButton btnBrowse;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IndividualChatBox window = new IndividualChatBox();
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
	public IndividualChatBox() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(700, 100, 450, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setTitle("Individual Chat Box");
		
		
		btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		btnSend.setBounds(335, 329, 89, 23);
		frame.getContentPane().add(btnSend);
		
		
		messages = new TextArea("", 3 , 100 , TextArea.SCROLLBARS_VERTICAL_ONLY);
		messages.setBounds(10, 10, 414, 233);
		frame.getContentPane().add(messages);
		messages.setEditable(false);
		
		
		toSend = new TextArea("", 3 , 100 , TextArea.SCROLLBARS_VERTICAL_ONLY);
		toSend.setBounds(10, 302, 319, 50);
		frame.getContentPane().add(toSend);
		toSend.setEditable(true);
		
		
		filePath = new JTextField();
		filePath.setBounds(10, 276, 319, 20);
		frame.getContentPane().add(filePath);
		filePath.setColumns(10);
		
		
		btnShare = new JButton("Share");
		btnShare.setBounds(335, 249, 89, 20);
		frame.getContentPane().add(btnShare);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(335, 302, 89, 23);
		frame.getContentPane().add(btnBrowse);
		
		download = new JTextField();
		download.setBounds(10, 249, 319, 20);
		frame.getContentPane().add(download);
		download.setColumns(10);
		
		btnDownload = new JButton("Download");
		btnDownload.setBounds(335, 275, 89, 23);
		frame.getContentPane().add(btnDownload);
	}
}
