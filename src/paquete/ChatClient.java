package paquete;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {// Se declara una clase ChatCliente 
//Se crean sus atributos que serviran para el ingreso, salida y despliegue de los datos
	BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    
    public ChatClient() {

        // ventana para despliegue de los mensajes
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        textField.addActionListener(new ActionListener() {
            //envia lo que se encuentra en el campo de texto al servidor y espera por el sguiente mensaje
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());//Obtiene el texto del cuadro de texto
                textField.setText("");//Limpia el cuadro de texto una vez que se envio el mensaje
            }
        });
    }
    
   //Metodo que pide la IP para hacer la conexion
    private String getServerAddress() {
    	
    	//Ventana que pide el ingreso de una IP
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the Chatter",
            JOptionPane.QUESTION_MESSAGE);
    }

  //Ventana para pedir el nombre del Cliente
    private String getName() {
    	//Devuelve el nombre que el cliente ingreso 
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    private void run() throws IOException {

        String serverAddress = getServerAddress();//obtiene la IP del servidor usando el metodo getServerAddress()
        Socket socket = new Socket(serverAddress, 9001);//crea un socket con la IP del servidor y el puerto para establecer la conexion
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));// Se declara un objeto in para poder leer los datos
        out = new PrintWriter(socket.getOutputStream(), true);//Se declara un objeto out para enviar datos

        /**En esta parte se realiza el broadcasting de los datos al servidor
         * */
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());//Si la cadena empieza con SUBMITNAME,obtiene un dato nombre
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);//si la cadena empieza con NAMEACCEPTED establece un campo detexto
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");//Si la cadena empieza con MESSAGE, se espera la entrada de un mensaje
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();//Se declara un objeto del tipo Cliente
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Se inicializa la ventana del chat
        client.frame.setVisible(true);//la hace visible
        client.run();//llama al metodo run
    }
    
}