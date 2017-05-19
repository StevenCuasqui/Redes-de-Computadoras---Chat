package paquete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

//se crea una clase Servidor
public class ChatServer {


    private static final int PORT = 9001;// El puerto que el servidor usara sera el 9001

    private static HashSet<String> names = new HashSet<String>();//se genera un arreglo con los nombres de los clientes
    
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();//se genera un arreglo con los mensajes que seran enviados por los clientes
    
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);//Se crea un socket Servidor en el puerto especificado
        try {
            while (true) {
                new Handler(listener.accept()).start();//Se genera el Thread para empezar la comunicacion
            }
        } finally {
            listener.close();//Se cierra la comunicacion para ese "hilo"
        }
    }
    
   
    private static class Handler extends Thread { // se construye una clase Handler que se encargara de manejar y transmitir sus mensajes
        private String name;// Se crean sus atributos, que serviran para el ingreso y salida de datos
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {// Constructor de la clase Handler, que recibe un socket
            this.socket = socket;
        }
        
        public void run() {
            try {

                
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream())); // Se declara un objeto in para poder leer los mensajes
                out = new PrintWriter(socket.getOutputStream(), true);//Se declara un objeto out para enviar datos
                
                while (true) {
                    out.println("SUBMITNAME");// manda un mensaje al Cliente para que envie un nombre
                    name = in.readLine();// lee el nombre enviado por el cliente
                    if (name == null) {// Se encarga de comprobar que intruduzca una cadena no vacia
                        return;
                    }
                    synchronized (names) { //comprueba que no exista esa cadena dentro del arreglo de nombres
                        if (!names.contains(name)) {
                            names.add(name);//de esta manera, si no se encuentra este nombre en el arreglo, lo añade
                            break;
                        }
                    }
                }

                out.println("NAMEACCEPTED");
                writers.add(out);//Envia un mensaje al cliente indicado que su nombre fue satisfactoriamente enviado

                /** Esta es la parte en que se realiza el Broadcasting, porque el servidor espera una entrada de datos
                 * para despues enviarla al arreglo de los mensajes e imprimirlo */
                while (true) {
                    String input = in.readLine();//Lee los datos que el cliente mande 
                    if (input == null) {//sale del bucle si el cliente envia una cadena vacia
                        return;
                    }
                    for (PrintWriter writer : writers) { //obtiene un mensaje del arreglo de mensajes
                        writer.println("MESSAGE " + name + ": " + input);//Envia el mensaje enviado con el nombre del Cliente
                    }
                }
            } catch (IOException e) {
                System.out.println(e);//controla errores del programa
            } finally {
                
                if (name != null) {//si se recibe un nombre distinto a nulo
                    names.remove(name);//remueve este nombre del arreglo de nombres
                }
                if (out != null) {//si el servidar envia un dato que no sea nulo
                    writers.remove(out);//remueve este mensaje del arreglo de mensajes
                }
                try {
                    socket.close();//cierra el socket con el cliente
                } catch (IOException e) {
                }
            }
        }
    }
}
