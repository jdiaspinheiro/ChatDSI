import java.net.*;
import java.io.*;
import java.util.*;

public class ServidorHandler extends Thread {
	Socket ligacao;
	BufferedReader in;
	PrintWriter out;
	Presencas presences;
	String usersTable;
	String website;
	String messageTable;

	public void run() {
		try {

			String msg;
			int len, length;
			int contentLength = -1;
			String metodo = "";

			// lê a primeira linha: request-line
			msg = in.readLine();
			len = msg == null ? 0 : msg.trim().length();

			// Trata a request line
			if (len != 0) {
				System.out.println(msg);

				StringTokenizer tokens = new StringTokenizer(msg);
				String token = tokens.nextToken();
				String tokenB = tokens.nextToken();

				System.out.println(token);
				System.out.println(tokenB);

				if (token.equals("GET"))
					metodo = "GET";
				if (token.equals("GET") && tokenB.equals("/RotaTabelas"))
					metodo = "GETLista";
				if (token.equals("POST") && tokenB.equals("/CriarConta"))
					metodo = "POSTConta";
				if (token.equals("POST") && tokenB.equals("/CriarMensagem"))
					metodo = "POSTMensagem";
			}

			// lê todas as linhas (terminadas por new line) até ler uma linha em branco;
			// corresponde a ler todo o cabeçalho
			while (len != 0) {

				// lê a linha seguinte
				msg = in.readLine();
				len = msg == null ? 0 : msg.trim().length();

				System.out.println(msg);

				if (msg.startsWith("Content-Length:")) {
					String cl = msg.substring("Content-Length:".length()).trim();
					contentLength = Integer.parseInt(cl);
				}

			}

			if (metodo == "GET") {
				System.out.println("A ligacao que atendeu o GET foi: " + ligacao.getInetAddress() + " do porto "
						+ ligacao.getPort());

				StringBuffer sb = new StringBuffer(500000);
				sb.append(gerarWebsite());
				System.out.println("GET");
				System.out.println("HTTP/1.1 200 OK");
				out.println("HTTP/1.1 200 OK");
				length = sb.length();
				out.println("Content-Length: " + length);
				out.write("\r\n");
				out.println(sb);
				out.flush();

				// Criar Conta
			} else if (metodo == "POSTConta") {

				// Validar valor contentLength
				char[] buf = new char[contentLength];
				in.read(buf);

				System.out.println(buf);
				String t = String.valueOf(buf);
				String[] resposta = t.split("=");
				String username = resposta[1];
				System.out.println(username);
				System.out.println(presences.getListaEspectadores());

				if (presences.VerificarC(username) == true) {
					StringBuffer sb = new StringBuffer(500000);
					sb.append(gerarWebsiteAlertAlreadyExists());
					out.println("HTTP/1.1 200 OK");
					length = sb.length();
					out.println("Content-Length: " + length);
					out.write("\r\n");
					out.println(sb);
					out.flush();

				} else {
					presences.AdicionarConta(username);
					StringBuffer sb = new StringBuffer(500000);
					sb.append(gerarWebsite());
					System.out.println("Sucesso, conta criada");
					out.println("HTTP/1.1 200 OK");
					length = sb.length();
					out.println("Content-Length: " + length);
					out.write("\r\n");
					out.println(sb);
					out.flush();

				}
			}

			// Get Lista Pessoas
			else if (metodo == "GETLista") {
				ArrayList<String> presentes = presences.getListaEspectadores();
				System.out.println(presentes);
				System.out.println("A ligacao que atendeu o GET foi: " + ligacao.getInetAddress() + " do porto "
						+ ligacao.getPort());
				System.out.print("Chegou aqui");
				StringBuffer sb = new StringBuffer(500000);

				sb.append(gerarWebsite());

				System.out.println("GET aqui");
				System.out.println("HTTP/1.1 200 OK");
				out.println("HTTP/1.1 200 OK");
				length = sb.length();
				out.println("Content-Length: " + length);
				out.write("\r\n");
				out.println(sb);
				out.flush();

			// Criar Mensagem
			} else if (metodo == "POSTMensagem") {
				char[] buf = new char[contentLength];
				in.read(buf);

				System.out.println(buf);
				String t = String.valueOf(buf);
				String[] resposta = t.split("&");
				String username = resposta[0];
				String[] id = username.split("=");
				String username1 = id[1];
				String mensagem = resposta[1];
				String[] conteudo = mensagem.split("=");
				String conteudo1 = conteudo[1];

				conteudo1 = conteudo1.replaceAll("\\+", " ");

				System.out.println(username);
				System.out.println(username1);
				System.out.println(mensagem);
				System.out.println(conteudo1);

				if (presences.VerificarC(username1) == true) {

					presences.AdicionarMensagens(username1, conteudo1);
					StringBuffer sb = new StringBuffer(500000);
					sb.append(gerarWebsite());
					System.out.println("Sucesso");
					System.out.println(presences.getMensagens());
					out.println("HTTP/1.1 200 OK");
					length = sb.length();
					out.println("Content-Length: " + length);
					out.write("\r\n");
					out.println(sb);
					out.flush();

				} else {
					System.out.println("USERNAME NAO EXISTENTE");
					StringBuffer sb = new StringBuffer(500000);
					sb.append(gerarWebsiteAlertNoUser());
					out.println("HTTP/1.1 200 OK");
					length = sb.length();
					out.println("Content-Length: " + length);
					out.write("\r\n");
					out.println(sb);
					out.flush();
				}
			}
			out.close();
			ligacao.close();
		}

		catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);

		}

	}

	// Metodo que do Handler de servidor
	public ServidorHandler(Socket ligacao, Presencas presences) {
		this.ligacao = ligacao;
		this.presences = presences;

		try {
			this.in = new BufferedReader(new InputStreamReader(ligacao.getInputStream()));

			this.out = new PrintWriter(ligacao.getOutputStream());
		} catch (IOException e) {
			System.out.println("Erro na execucao do servidor: " + e);
			System.exit(1);
		}
	}

	// Metodo que gera a tabela de users para ser usada ao gerar o website
	public String preencherUserTable() {
		StringBuilder stringB = new StringBuilder();
		ArrayList<String> presentes = presences.getListaEspectadores();
		for (String s : presentes) {
			stringB.append(" <tr> <td> " + s + "</td> </tr> ");
		}
		return usersTable = stringB.toString();
	}

	// Metodo que gera a tabela de mensagens
	public String preencherMessageTable() {
		StringBuilder stringB = new StringBuilder();
		ArrayList<String> mensagens = presences.getMensagens();
		for (int i = 0; i < mensagens.size(); i += 3) {
			stringB.append("<tr><td> " + mensagens.get(i) + "</td>");
			stringB.append(" <td>" + mensagens.get(i + 1) + "</td> ");
			stringB.append(" <td>" + mensagens.get(i + 2) + "</td> </tr>");
		}
		return messageTable = stringB.toString();
	}

	// Metodo que gera o website
	public String gerarWebsite() {
		preencherUserTable();
		preencherMessageTable();
		return website = new String(
				websiteFinalBlock1 + messageTable + websiteFinalBlock2 + usersTable + websiteFinalBlock3);
	}

	// Metodo que gera o website com pop up de erro de username ja existente
	public String gerarWebsiteAlertAlreadyExists() {
		preencherUserTable();
		preencherMessageTable();
		return website = new String(
				websiteFinalBlock1 + messageTable + websiteFinalBlock2_WithAlert + usersTable + websiteFinalBlock3);
	}

	// Metodo que gera o website com pop up de erro de username nao existente
	public String gerarWebsiteAlertNoUser() {
		preencherUserTable();
		preencherMessageTable();
		return website = new String(
				websiteFinalBlock1_WithAlert + messageTable + websiteFinalBlock2 + usersTable + websiteFinalBlock3);
	}

	// Strings geradoras do website
	String websiteBlock1 = new String(
			"<!DOCTYPE html><html lang='en'><head><meta http-equiv='refresh' content='30'/><meta charset='UTF-8' /><meta name='viewport' content='width=device-width, initial-scale=1.0' /><meta http-equiv='X-UA-Compatible' content='ie=edge' /><title>Sistemas Distribuídos - Chat</title><link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css' integrity='sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T' crossorigin='anonymous'><style>.card-body{overflow-y:auto;overflow-x:auto;scrollTo(0, xH)}.bg{background:url(https://visme.co/blog/wp-content/uploads/2017/07/50-Beautiful-and-Minimalist-Presentation-Backgrounds-028.jpg) no-repeat center center fixed;-webkit-background-size:cover;-moz-background-size:cover;-o-background-size:cover;background-size:cover}</style></head><body class='bg'><div class='container-fluid px-md-5' style='margin-top:30px'><div class='row justify-content-between'><div class='col-md-2 px-md-5'> <img src='http://protimber.lnec.pt/logos/UM.png' class='float-left' alt='UM-EE'></div><div class='col-md-10 text-white'><h2>Universidade do Minho - Engenharia e Gestão de Sistemas de Informação</h2><h5>Sistemas Distribuídos - Projeto 1</h5><p>Sistema de Resposta de Audiências</p></div></div></div><div class='d-flex flex-row p-3 align-items-center' style='margin-top: 20px'><div class='container col-md-8'>");
	String websiteBlock2 = new String(
			"<div class='card text-center' style='height:700px'><div class='card-header font-weight-bold'> Chatroom</div><div class='card-body'><table class='table table-striped'><thead><tr><th class='w-10'>Time</th><th class='w-20'>Username</th><th class='w-70'>Mensagem</th></tr></thead><tbody> ");
	String websiteBlock3 = new String(
			" </tbody></table></div><div class='card-footer'><form class='form-inline' action='/CriarMensagem' method='POST'> <input type='text' id='name' name='name' class='form-control col-sm-2' placeholder='Nome/ID' /><input type='text' id='mensagem' name='mensagem' class='form-control col-sm-8' placeholder='Mensagem' /> <input type='submit' value='Enviar Mensagem' class='btn btn-primary col-sm-2' /></form></div></div></div><div class='container col-md-3'><div class='container p-3'><form class='text-center font-weight-bold text-white form-inline' action='/CriarConta' method='POST'> <label for='name'> Crie um novo utilizador para utilizar o chat!</label> <input type='text' id='name' name='name' class='form-control w-75' placeholder='Nome/ID' /> <input type='submit' value='Registar' class='btn btn-primary w-25' /></form></div>");
	String websiteBlock4 = new String(
			"<div class='card text-center text-black bg-light' style='height:600px'><div class='card-header font-weight-bold'> User List</div><div class='card-body'><table class='table table-striped'><tbody> ");
	String websiteBlock5 = new String(
			" </tbody></table></div><div class='card-footer'><form method='get' action='/RotaTabelas'> <button type='submit' value='RotaTabelas' class='btn btn-primary'>Atualizar Servidor</button></form></div></div></div></div></div> <script src='https://code.jquery.com/jquery-3.3.1.slim.min.js' integrity='sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo' crossorigin='anonymous'></script> <script src='https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js' integrity='sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1' crossorigin='anonymous'></script> <script src='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js' integrity='sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM' crossorigin='anonymous'></script> </body></html>");
	String websiteAlertNoUser = new String(
			"<div class='alert alert-danger alert-dismissible fade show' > <button type='button' class='close' data-dismiss='alert'>&times;</button> <strong>Erro!</strong> Esse username não existe, por isso não pode enviar mensagem. Tente outra vez. </div>");
	String websiteAlertAlreadyExists = new String(
			"<div class='alert alert-danger alert-dismissible fade show' > <button type='button' class='close' data-dismiss='alert'>&times;</button> <strong>Erro!</strong> Esse username já existe. Tente outra vez.</div>");
	String websiteFinalBlock1 = new StringBuilder().append(websiteBlock1).append(websiteBlock2).toString();
	String websiteFinalBlock1_WithAlert = new StringBuilder().append(websiteBlock1).append(websiteAlertNoUser)
			.append(websiteBlock2).toString();
	String websiteFinalBlock2 = new StringBuilder().append(websiteBlock3).append(websiteBlock4).toString();
	String websiteFinalBlock2_WithAlert = new StringBuilder().append(websiteBlock3).append(websiteAlertAlreadyExists)
			.append(websiteBlock4).toString();
	String websiteFinalBlock3 = new StringBuilder().append(websiteBlock5).toString();
}