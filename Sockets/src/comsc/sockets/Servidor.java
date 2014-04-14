package comsc.sockets;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Servidor extends JFrame {
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
private JTextField campoIntroducir;
   private JTextArea areaPantalla;
   private ObjectOutputStream salida;
   private ObjectInputStream entrada;
   private ServerSocket servidor;
   private Socket conexion;
   private int contador = 1;

   public Servidor()
   {
      super( "Servidor" );

      Container contenedor = getContentPane();

      campoIntroducir = new JTextField();
      campoIntroducir.setEditable( false );
      campoIntroducir.addActionListener(
         new ActionListener() {


            public void actionPerformed( ActionEvent evento )
            {
               enviarDatos( evento.getActionCommand() );
               campoIntroducir.setText( "" );
            }
         }
      );

      contenedor.add( campoIntroducir, BorderLayout.NORTH );

      areaPantalla = new JTextArea();
      contenedor.add( new JScrollPane( areaPantalla ),
         BorderLayout.CENTER );

      setSize( 300, 150 );
      setVisible( true );

   }
   public void ejecutarServidor()
   {

      try {

         // crear un objeto ServerSocket.
         servidor = new ServerSocket( 1234, 100 );

         while ( true ) {

            try {
               esperarConexion();
               obtenerFlujos();
               procesarConexion();
            }

            catch ( EOFException excepcionEOF ) {
               System.err.println( "El servidor terminó la conexión" );
            }

            finally {
               cerrarConexion();
               ++contador;
            }

         }

      }


      catch ( IOException excepcionES ) {
         excepcionES.printStackTrace();
      }

   }
   private void esperarConexion() throws IOException
   {
      mostrarMensaje( "Esperando una conexión\n" );
      mostrarMensaje( "Conexión " + contador + " recibida de: " +
         conexion.getInetAddress().getHostName() );
   }


   private void obtenerFlujos() throws IOException
   {

      salida = new ObjectOutputStream( conexion.getOutputStream() );
      salida.flush();


      entrada = new ObjectInputStream( conexion.getInputStream() );

      mostrarMensaje( "\nSe recibieron los flujos de E/S\n" );
   }

   private void procesarConexion() throws IOException
   {

      String mensaje = "Conexión exitosa";
      enviarDatos( mensaje );


      establecerCampoTextoEditable( true );

      do {


         try {
            mensaje = ( String ) entrada.readObject();
            mostrarMensaje( "\n" + mensaje );
         }


         catch ( ClassNotFoundException excepcionClaseNoEncontrada ) {
            mostrarMensaje( "\nSe recibió un tipo de objeto desconocido" );
         }

      } while ( !mensaje.equals( "CLIENTE>>> TERMINAR" ) );

   }

   private void cerrarConexion()
   {
      mostrarMensaje( "\nFinalizando la conexión\n" );
      establecerCampoTextoEditable( false );

      try {
         salida.close();
         entrada.close();
         conexion.close();
      }
      catch( IOException excepcionES ) {
         excepcionES.printStackTrace();
      }
   }


   private void enviarDatos( String mensaje )
   {

      try {
         salida.writeObject( "SERVIDOR>>> " + mensaje );
         salida.flush();
         mostrarMensaje( "\nSERVIDOR>>> " + mensaje );
      }

      catch ( IOException excepcionES ) {
         areaPantalla.append( "\nError al escribir objeto" );
      }
   }


   private void mostrarMensaje( final String mensajeAMostrar )
   {

      SwingUtilities.invokeLater(
         new Runnable() {
            public void run()
            {
               areaPantalla.append( mensajeAMostrar );
               areaPantalla.setCaretPosition(
                  areaPantalla.getText().length() );
            }

         }

      );
   }


   private void establecerCampoTextoEditable( final boolean editable )
   {

      SwingUtilities.invokeLater(
         new Runnable() {

            public void run()
            {
               campoIntroducir.setEditable( editable );
            }

         }

      );
   }

   public static void main( String args[] )
   {
      Servidor aplicacion = new Servidor();
      aplicacion.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      aplicacion.ejecutarServidor();
   }

}