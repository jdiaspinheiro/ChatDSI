import java.net.*;
import java.io.*;

public class Servidor {
	static int DEFAULT_PORT=8081;
	
	public static void main(String[] args) {
		int port=DEFAULT_PORT;
		Presencas presences = new Presencas();
		ServerSocket servidor = null; 
	
		try	{ 
			servidor = new ServerSocket(port);
		} catch (Exception e) { 
			System.err.println("Erro ao criar o socket do servidor...");
			e.printStackTrace();
			System.exit(-1);
		}
			
		System.out.println("Servidor a espera de ligacoes na porta " + port);
		
		while(true) {
			try {
				Socket ligacao = servidor.accept();
								
				ServidorHandler server = new ServidorHandler(ligacao, presences);
				server.start();
				
			} catch (IOException e) {
				System.out.println("Erro na execucao do servidor: "+e);
				System.exit(1);
			}
		}
	}
}
