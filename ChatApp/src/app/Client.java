package app;

import java.awt.EventQueue;
import java.awt.List;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Client {
	
	static LoginWindow window;
	static FriendsWindow friends;
	static IndividualChatBox chatbox;
	static GroupChatBox groupchat;
	static String id = "";
	static ArrayList<String> prieteni;
	static final String LIMIT = "File exceeds 1MB";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String string1 = "Sending a test message";
		SocketTest test1 = new SocketTest(string1);
		Thread thread = new Thread(test1);
		thread.start();
		
		prieteni = new ArrayList<String>();
		prieteni.add("Ion");
		prieteni.add("Gheorghe");
		prieteni.add("Mihai");
		prieteni.add("Maria");
		prieteni.add("Vasile");
		
		window = new LoginWindow();
		friends = new FriendsWindow();
		chatbox = new IndividualChatBox();
		groupchat = new GroupChatBox();
		Client.window.run();
		//window.frame.setVisible(true);
		
		while(true) {
			if (!id.equals("")) {
				String toSend = "anythingForMe" + "/" + id;
				SocketTest sock = new SocketTest(toSend, "thread_secundar");
				Thread th = new Thread(sock);
				th.start();
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	static class SocketTest implements Runnable {

		private String message = "";
		private String message_secondary = "";
		private Selector selector;


		public SocketTest(String message){
			this.message = message;
		}
		
		public SocketTest(String message, String message_secondary) {
			this.message = message;
			this.message_secondary = message_secondary;
		}

		@Override
		public void run() {
			SocketChannel channel;
			try {
				selector = Selector.open();
				channel = SocketChannel.open();
				channel.configureBlocking(false);

				channel.register(selector, SelectionKey.OP_CONNECT);
				channel.connect(new InetSocketAddress("127.0.0.1", 8511));

				while (!Thread.interrupted()){

					selector.select(1000);
					
					Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

					while (keys.hasNext()){
						SelectionKey key = keys.next();
						keys.remove();

						if (!key.isValid()) continue;

						if (key.isConnectable()){
							//System.out.println("I am connected to the server");
							connect(key);
						}	
						if (key.isWritable()) {
							write(key);
						}
						if (key.isReadable()) {
							read(key);
							if (message_secondary.equals("thread_secundar")) {
								return;
							}
						}
					}	
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				close();
			}
		}
		
		private void close() {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void read (SelectionKey key) throws IOException {
			SocketChannel channel = (SocketChannel) key.channel();
			ByteBuffer readBuffer = ByteBuffer.allocate(1000);
			readBuffer.clear();
			int length;
			try{
			length = channel.read(readBuffer);
			} catch (IOException e){
				System.out.println("Reading problem, closing connection");
				key.cancel();
				channel.close();
				return;
			}
			if (length == -1){
				System.out.println("Nothing was read from server");
				channel.close();
				key.cancel();
				return;
			}
			readBuffer.flip();
			byte[] buff = new byte[1024];
			readBuffer.get(buff, 0, length);
			//System.out.println("Server said: "+(new String(buff)).trim());
			String msg = new String(buff);
			msg.trim();
			String[] sp = msg.split("/", 2);
			
			if (sp[0].trim().equals("New")) {
				sp[1].trim();
				String[] mesaj = sp[1].split("/", 3);
				System.out.println("Destinatar: adica nu cel ce trimite: " + mesaj[0]);
				System.out.println("Expeditor: " + mesaj[1]);
				System.out.println("Mesaj:" + mesaj[2]);
				Client.chatbox.messages.append(mesaj[1] + ": " + mesaj[2]);
			}
			
			if (sp[0].trim().equals("File")) {
				sp[1].trim();
				String[] fisier = sp[1].split(";", 2);
				String[] detalii = fisier[0].split("/", 3);
				System.out.println(detalii);
				Client.chatbox.download.setText(detalii[1] + " sent: " + detalii[2]);
				Client.chatbox.buffer = fisier[1];
				Client.chatbox.nume_fisier_download = detalii[2];
			}
			
			if (message_secondary.equals("thread_secundar")) {
				message_secondary = "thread";
				return;
			}
		}

		private void write(SelectionKey key) throws IOException {
			SocketChannel channel = (SocketChannel) key.channel();
			channel.write(ByteBuffer.wrap(message.getBytes()));

			// lets get ready to read.
			key.interestOps(SelectionKey.OP_READ);
		}

		private void connect(SelectionKey key) throws IOException {
			SocketChannel channel = (SocketChannel) key.channel();
			if (channel.isConnectionPending()){
				channel.finishConnect();
			}
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_WRITE);
		}

	}

	
								// CLIENT COMMUNICATION CODE
//--------------------------------------------------------------------------------------//
								// GUI CODE
	
/**
 * 					LOGIN WINDOW
 * @author Darius
 *
 */
	static class LoginWindow {

		private JFrame frame;
		private JTextField user_email;
		private JPasswordField user_password;
		private JLabel lblNewLabel;
		private JLabel lblNewLabel_1;
		private TextArea login_message;


		public void run() {
			try {
				//LoginWindow window = new LoginWindow();
				Client.window.frame.setVisible(true);
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
					String idd = user_email.getText();
					if (!idd.equals("")) {
						System.out.println("cevaaa");
						Client.id = idd;
						Client.prieteni.remove(idd);
						for (int i = 0; i < Client.prieteni.size(); i++) {
							Client.friends.friends.add(Client.prieteni.get(i));
						}
						Client.friends.frame.setTitle(Client.id);
						Client.friends.frame.setVisible(true);
						System.out.println(Client.friends.friends.toString());
						Client.window.frame.setVisible(false);
					}
					
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
	
	
	
/**
 * 						FRIENDS BOX
 * @author Darius
 *
 */
	static class FriendsWindow {

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
			
			friends.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mouseEvent) {
					if (mouseEvent.getClickCount() == 2) {
				        System.out.println("chat box");
				        Client.chatbox.frame.setVisible(true);
				        Client.chatbox.id_friend = friends.getSelectedItem();
				        Client.chatbox.frame.setTitle("Me: " + Client.id + "   Chatting with:" + friends.getSelectedItem());
					}
					
					try {
					} catch (Throwable e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			
			
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
	
	
	
	
/**
 * 					INDIVIDUAL CHAT BOX
 * @author Darius
 *
 */
	
	static class IndividualChatBox {

		private JFrame frame;
		private TextArea messages;
		private TextArea toSend;
		private JTextField filePath;
		private JButton btnShare;
		private JButton btnSend;
		String id_friend;
		private JTextField download;
		private JButton btnDownload;
		private JButton btnBrowse;
		private String buffer;
		private String nume_fisier_download;
		
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
					String msg = toSend.getText().toString();
					if (!msg.equals("")) {
						messages.append(Client.id + ": " + msg);
						messages.append("\n");
						System.out.println("Ce trimit: " + toSend.getText().toString());
						String string1 = "New/" + id_friend + "/" + Client.id + "/" + toSend.getText().toString();
						SocketTest test1 = new SocketTest(string1);
						Thread thread = new Thread(test1);
						thread.start();
					}
					toSend.setText("");
				}
			});
			btnSend.setBounds(335, 329, 89, 23);
			frame.getContentPane().add(btnSend);
			
			
			messages = new TextArea("", 3 , 100 , TextArea.SCROLLBARS_VERTICAL_ONLY);
			messages.setBounds(10, 10, 414, 233);
			frame.getContentPane().add(messages);
			messages.setEditable(false);
			messages.setText("");
			
			
			toSend = new TextArea("", 3 , 100 , TextArea.SCROLLBARS_VERTICAL_ONLY);
			toSend.setBounds(10, 302, 319, 50);
			frame.getContentPane().add(toSend);
			toSend.setEditable(true);
			
			
			filePath = new JTextField();
			filePath.setBounds(10, 276, 319, 20);
			frame.getContentPane().add(filePath);
			filePath.setText("");
			filePath.setColumns(10);
			
			
			btnShare = new JButton("Share");
			btnShare.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!filePath.equals("") && !filePath.equals(LIMIT)) {
						Path path = Paths.get(filePath.getText().toString());
						String nume = path.getFileName().toString();
						try {
							byte[] data = Files.readAllBytes(path);
							String s = new String(data);
							System.out.println("Cale: " + path.toString());
							System.out.println("File/" + id_friend + "/" + Client.id + "/" + nume + ";" + s);
							String string1 = "File/" + id_friend + "/" + Client.id + "/" + nume + ";" + s;
							SocketTest test1 = new SocketTest(string1);
							Thread thread = new Thread(test1);
							thread.start();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					filePath.setText("");
				}
			});
			btnShare.setBounds(335, 275, 89, 23);
			frame.getContentPane().add(btnShare);
			
			btnBrowse = new JButton("Browse");
			btnBrowse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
					fileChooser.setDialogTitle("Choose file");
					int result = fileChooser.showOpenDialog(btnBrowse);
					if (result == JFileChooser.APPROVE_OPTION) {
					    File selectedFile = fileChooser.getSelectedFile();
					    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
					    filePath.setText(selectedFile.getAbsolutePath().toString());
					    File f = new File(filePath.getText().toString());
					    if (f.length() >= 1000 * 1000) {
					    	filePath.setText(LIMIT);
					    } else {
					    	filePath.setText(selectedFile.getAbsolutePath().toString());
					    }
					}
				}
			});
			btnBrowse.setBounds(335, 302, 89, 23);
			frame.getContentPane().add(btnBrowse);
			
			download = new JTextField();
			download.setBounds(10, 249, 319, 20);
			frame.getContentPane().add(download);
			download.setText("");
			download.setColumns(10);
			
			btnDownload = new JButton("Download");
			btnDownload.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (!download.getText().toString().equals("")) {
						System.out.println(nume_fisier_download + buffer);
						try {
							Files.write(Paths.get("__" + nume_fisier_download), buffer.getBytes(), StandardOpenOption.CREATE);
						} catch (IOException e) {
							e.printStackTrace();
						}
						download.setText("");
						buffer = "";
					}
				}
			});
			btnDownload.setBounds(335, 249, 89, 20);
			frame.getContentPane().add(btnDownload);
		}
	}
	
	


/**
 * 					GROUP CHAT BOX	
 * @author Darius
 *
 */	
	static class GroupChatBox {

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
						//GroupChatBox window = new GroupChatBox();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		public void run() {
			try {
				//GroupChatBox window = new GroupChatBox();
				window.frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	
}
