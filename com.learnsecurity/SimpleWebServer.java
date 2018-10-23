/*Though this code is written in protected virtual environment like java, which provides memory mgmt.,
 * arrayOutOfbounds exception and overflow protection. 
 * However Java framework also calls into OS libraries that are vulnerable to buffer overflow(c/c++), 
 * which can occur when passing unvalidated data into the framework’s API that 
 * interface with the operating system*/
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class SimpleWebServer {

	/* Run the HTTP server on this TCP port*/
	private static final int PORT=8080;
	
	
	/*The socket used to process incoming connections from webclients*/
	private static ServerSocket dServerSocket;
	BufferedReader br;

	public SimpleWebServer() throws Exception{
		dServerSocket=new ServerSocket(PORT);
	}
	
	public void run() throws Exception{
		while(true) {
			/*Wait for a connection from a client*/
			Socket s=dServerSocket.accept();
			
			/*then process the clients's request*/
			processRequest(s);
		}
	}
	/* Reads the HTTP requests from the client and responds with the fields the user requested or a HTTP error code*/
	
	public void processRequest(Socket s) throws Exception{
		/*used to read data from the client */
	    
	        /*Developers should ensure strict length checks before data is parsed by the server*/    
	        br= new BufferedReader(new InputStreamReader(s.getInputStream()));
	        
		/*used to write data to the client*/
		OutputStreamWriter osw=new OutputStreamWriter(s.getOutputStream());
		
		/*read the HTTP request from the client*/
		/*Before assigning string data to buffers, the developer should make sure that the data is not larger than the buffer's capacity*/
		String request=br.readLine();
		
		String command=null;
		String pathname=null;
		
		/*parse HTTP request*/
		StringTokenizer st=new StringTokenizer(request,"");
		command=st.nextToken();
		pathname=st.nextToken();
		
		// Along with checking for "GET", 
		if(command.equals("GET")) {
		/*if request is NOT a GET return an error saying this server does not implement the requested command*/
			osw.write("HTTP/1.0 501 NOT implemented");
		}
		/* close the connection to the client*/
		osw.close();
	}
	
	public void serveFile(OutputStreamWriter osw, String pathname)throws Exception{
		FileReader fr=null;
		int c=-1;
		StringBuffer sb=new StringBuffer();
		
		/*remove the initial slash at the beginning of the pathname in the request*/
		if(pathname.charAt(0)=='/')
			 pathname=pathname.substring(1);
		/*if there was no filename specified by the client, serve the "index.html" file*/
		if(pathname.equals(""))
			pathname="index.html";
		/* try to open file specified by pathname */
		try {
			fr = new FileReader (pathname);                 
	 	    c = fr.read(); 
		}catch(Exception e) {
			/* if the file is not found,return the
	 	       appropriate HTTP response code  */
	 	    osw.write ("HTTP/1.0 404 Not Found\n\n");         
	 	    return;
		}
		/* if the requested file can be successfully opened
	 	   and read, then return an OK response code and
	 	   send the contents of the file */
		osw.write ("HTTP/1.0 200 OK\n\n"); 
		
		/*Bounds checking can be done here. Insufficient bounds checks can cause memory leaks associated with heap that can 
		 * take up memory resources, which stops the heap from accomodating the OS’s needs, resulting in a system crash*/
		while (c != -1) {       // What if value of c is -5?, the better way would be to make while condition 'c>0'    
		    sb.append((char)c);                            
	 	    c = fr.read();                                  
	 	}                                                   
	 	osw.write (sb.toString());  
	}
	  /* This method is called when the program is run from the command line. */
  public static void main (String argv[]) throws Exception { 

	/* Create a SimpleWebServer object, and run it */
	SimpleWebServer sws = new SimpleWebServer();           
	sws.run();    // call the run method of call SimpleWebServer2                                         
 	}      
	
}
