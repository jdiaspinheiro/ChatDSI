import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Presencas {

    private String username;
    private boolean registoC;
    private boolean verificacaoC;
    private ArrayList<String> ListaEspectadores = new ArrayList<String>();
    private ArrayList<String> ListaMensagens = new ArrayList<String>();
   
public  ArrayList<String> getMensagens() {
        return ListaMensagens;
    }

    public void AdicionarMensagens(String username, String mensagem) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        ListaMensagens.add(java.time.LocalTime.now().format(formatter));
        ListaMensagens.add(username);
        ListaMensagens.add(mensagem);
    }

    public void setMensagens(ArrayList<String> ListaMensagens) {
        this.ListaMensagens = ListaMensagens;
    }

    public String getUsername() {
        return username;
    }


    public ArrayList<String> getListaEspectadores() {
        return ListaEspectadores;
    }

    public void setListaEspectadores(ArrayList<String> ListaEspectadores) {
        this.ListaEspectadores = ListaEspectadores;
    }

    public void AdicionarConta(String user) {
        ListaEspectadores.add(user);
    }

    public boolean RegistarC(String username) {
        for (String userid : ListaEspectadores) {
            if (userid.equals(username)) {
                registoC = false;
            } else {
                ListaEspectadores.add(username);
                registoC = true;
            }
        }
        return registoC;
    }

    public boolean VerificarC(String username) {
        for (String s : ListaEspectadores) {
            verificacaoC = s.equals(username);
            if (verificacaoC == true) {
                break;
            };
        }
        return verificacaoC;
    }
}