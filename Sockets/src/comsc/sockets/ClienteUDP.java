package com.sc.sockets;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClienteUDP extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField campoIntroducir;
	private JTextArea areaPantalla;
	private DatagramSocket socket;

	public ClienteUDP() {
		super("Cliente");

		Container contenedor = getContentPane();

		campoIntroducir = new JTextField("Escriba aquí el mensaje");
		campoIntroducir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evento) {

				try {
					areaPantalla.append("\nEnviando paquete que contiene: "
							+ evento.getActionCommand() + "\n");

					String mensaje = evento.getActionCommand();
					byte datos[] = mensaje.getBytes();

					DatagramPacket enviarPaquete = new DatagramPacket(datos, datos.length, InetAddress.getLocalHost(), 1234);

					socket.send(enviarPaquete);
					areaPantalla.append("Paquete enviado\n");
					areaPantalla.setCaretPosition(areaPantalla.getText().length());
				}

				catch (IOException excepcionES) {
					mostrarMensaje(excepcionES.toString() + "\n");
					excepcionES.printStackTrace();
				}

			}

		}

		);

		contenedor.add(campoIntroducir, BorderLayout.NORTH);

		areaPantalla = new JTextArea();
		contenedor.add(new JScrollPane(areaPantalla), BorderLayout.CENTER);

		setSize(400, 300);
		setVisible(true);

		try {
			socket = new DatagramSocket();
		}

		catch (SocketException excepcionSocket) {
			excepcionSocket.printStackTrace();
			System.exit(1);
		}

	}

	private void esperarPaquetes() {
		while (true) {

			try {

				byte datos[] = new byte[100];
				DatagramPacket recibirPaquete = new DatagramPacket(datos,
						datos.length);

				socket.receive(recibirPaquete);

				mostrarMensaje("\nPaquete recibido:"
						+ "\nDel host: "
						+ recibirPaquete.getAddress()
						+ "\nPuerto del host: "
						+ recibirPaquete.getPort()
						+ "\nLongitud: "
						+ recibirPaquete.getLength()
						+ "\nContenido:\n\t"
						+ new String(recibirPaquete.getData(), 0,
								recibirPaquete.getLength()));
			} catch (IOException excepcion) {
				mostrarMensaje(excepcion.toString() + "\n");
				excepcion.printStackTrace();
			}

		}

	}

	private void mostrarMensaje(final String mensajeAMostrar) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				areaPantalla.append(mensajeAMostrar);
				areaPantalla.setCaretPosition(areaPantalla.getText().length());
			}

		}

		);
	}

	public static void main(String args[]) {
		ClienteUDP aplicacion = new ClienteUDP();
		aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		aplicacion.esperarPaquetes();
	}

}
