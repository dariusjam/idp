package app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// bin file: java -cp . bin.Server

public class Server implements Runnable {
	
	public static ArrayList<String> messages_to_send;
	public static ArrayList<String> files_to_send;

	public final static String ADDRESS = "127.0.0.1";
	public final static int PORT = 8511;
	public final static long TIMEOUT = 10000;
	
	private ServerSocketChannel serverChannel;
	private Selector selector;
	/**
	 * This hashmap is important. It keeps track of the data
	 */
	private Map<SocketChannel,byte[]> dataTracking = new HashMap<SocketChannel, byte[]>();

	public Server() {
		messages_to_send = new ArrayList<String>();
		files_to_send = new ArrayList<String>();
		init();
	}

	private void init(){
		System.out.println("initializing server");
		// We do not want to call init() twice and recreate the selector or the serverChannel.
		if (selector != null) return;
		if (serverChannel != null) return;

		try {
			// This is how you open a Selector
			selector = Selector.open();
			// This is how you open a ServerSocketChannel
			serverChannel = ServerSocketChannel.open();
			// You MUST configure as non-blocking or else you cannot register the serverChannel to the Selector.
			serverChannel.configureBlocking(false);
			// bind to the address that you will use to Serve.
			serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT));

			/**
			 * Here you are registering the serverSocketChannel to accept connection, thus the OP_ACCEPT.
			 * This means that you just told your selector that this channel will be used to accept connections.
			 * We can change this operation later to read/write, more on this later.
			 */
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Now accepting connections...");
		try{
			// A run the server as long as the thread is not interrupted.
			while (!Thread.currentThread().isInterrupted()){
				/**
				 * selector.select(TIMEOUT) is waiting for an OPERATION to be ready and is a blocking call.
				 * For example, if a client connects right this second, then it will break from the select()
				 * call and run the code below it. The TIMEOUT is not needed, but its just so it doesn't 
				 * block undefinitely.
				 */
				selector.select(TIMEOUT);

				/**
				 * If we are here, it is because an operation happened (or the TIMEOUT expired).
				 * We need to get the SelectionKeys from the selector to see what operations are available.
				 * We use an iterator for this. 
				 */
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()){
					SelectionKey key = keys.next();
					// remove the key so that we don't process this OPERATION again.
					keys.remove();

					// key could be invalid if for example, the client closed the connection.
					if (!key.isValid()){
						continue;
					}
					/**
					 * In the server, we start by listening to the OP_ACCEPT when we register with the Selector.
					 * If the key from the keyset is Acceptable, then we must get ready to accept the client
					 * connection and do something with it. Go read the comments in the accept method.
					 */
					if (key.isAcceptable()){
						//System.out.println("Accepting connection");
						accept(key);
					}
					/**
					 * If you already read the comments in the accept method then you understand that 
					 */
					if (key.isReadable()){
						//System.out.println("Reading connection");
						read(key);
					}
					if (key.isWritable()){
						//System.out.println("Writing...");
						write(key);
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally{
			closeConnection();
		}

	}

	private void write(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		byte[] data = dataTracking.get(channel);
		dataTracking.remove(channel);
		
		channel.write(ByteBuffer.wrap(data));
		
		key.interestOps(SelectionKey.OP_READ);
		
	}
	// Nothing special, just closing our selector and socket.
	private void closeConnection(){
		System.out.println("Closing server down");
		if (selector != null){
			try {
				selector.close();
				serverChannel.socket().close();
				serverChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Since we are accepting, we must instantiate a serverSocketChannel by calling key.channel().
	 * We use this in order to get a socketChannel (which is like a socket in I/O) by calling
	 *  serverSocketChannel.accept() and we register that channel to the selector to listen 
	 *  to a WRITE OPERATION. I do this because my server sends a hello message to each
	 *  client that connects to it. I then 
	 */
	private void accept(SelectionKey key) throws IOException{
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		
		socketChannel.register(selector, SelectionKey.OP_WRITE);
		byte[] hello = new String("Hello from server").getBytes();
		dataTracking.put(socketChannel, hello);
	}

	private void read(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		readBuffer.clear();
		int read;
		try {
			read = channel.read(readBuffer);
		} catch (IOException e) {
			System.out.println("Reading problem, closing connection");
			key.cancel();
			channel.close();
			return;
		}
		if (read == -1){
			System.out.println("Nothing was there to be read, closing connection");
			channel.close();
			key.cancel();
			return;
		}
		readBuffer.flip();
		byte[] data = new byte[1000];
		readBuffer.get(data, 0, read);
		//System.out.println("Received: "+ (new String(data)).trim());
		String msg = new String(data);
		String[] sp = msg.split("/", 2);
		
		
		// Daca este request de update mesaj
		if (sp[0].equals("anythingForMe")) {
			for (int i = 0; i < files_to_send.size(); i++) {
				if (files_to_send.get(i).trim().startsWith(sp[1].trim())) {
					data = ("File/" + files_to_send.get(i).trim() + "\n").getBytes();
					files_to_send.remove(i);
					break;
				}
			}
			
			for (int i = 0; i < messages_to_send.size(); i++) {
				if (messages_to_send.get(i).trim().startsWith(sp[1].trim())) {
					data = ("New/" + messages_to_send.get(i).trim() + "\n").getBytes();
					System.out.println("Mesaj trimis:" + messages_to_send.get(i).trim());
					messages_to_send.remove(i);
					break;
				}
			}
			
			echo(key,data);
		}
		
		if (sp[0].equals("New")) {
			messages_to_send.add(sp[1].trim());
			//key.interestOps(SelectionKey.OP_READ);
		}
		
		if (sp[0].equals("File")) {
			System.out.println("cevaaaa");
			System.out.println(sp[1]);
			files_to_send.add(sp[1].trim().toString());
			System.out.println(sp[1]);
			String[] spp = sp[1].split(";", 2);
			String[] sppp = spp[0].split("/", 3);
			System.out.println(sppp);
			Files.write(Paths.get("_" + sppp[2]), spp[1].getBytes(), StandardOpenOption.CREATE);
			
		}

	}

	private void echo(SelectionKey key, byte[] data){
		SocketChannel socketChannel = (SocketChannel) key.channel();
		dataTracking.put(socketChannel, data);
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
	// Metoda main care lanseaza serverul
    public static void main(String[] args) {
    	//PORT = Integer.parseInt(args[0]);
    	Server S = new Server();
    	S.run();
    }

}
