package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Servidor {

	public static void main(String[] args) {
		//se prepara para una instancia de un servidor socket
		ServerSocket servidor = null;
		Socket sc = null;
		//puesto por el que se comunicarán las maquinas
		final int PUERTO = 9000;
		//como se hará uso de recursos externos se valida con excepciones
		try {
			//se instancia el servidor socket
			servidor = new ServerSocket(PUERTO);
			System.out.println("Soy el servidor y he iniciado");
			//se preparan canales de entrada y salida
			DataInputStream in;
			DataOutputStream out;
			//se realiza una conexión con la base de datos
			Connection conexion = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/cuentas", "root", "kamailio");
			//se mantendrá un ciclo mientras mientras que la maquina este energizada o alguien termine el proceso
			while (true) {
				//se deja a la escucha de laconexión de los clientes
				sc = servidor.accept();
				//instancia del canal de entrada
				in = new DataInputStream(sc.getInputStream());
				String mensajeRecibido = in.readUTF();
				//se dicide el mensaje recibido para procesarlo
				String[] mensajeRecibidoDividido = mensajeRecibido.split(";");
				//se prepara el mensaje a enviar al cliente
				String mensajeEnviar = null;
				//canal de salida
				out = new DataOutputStream(sc.getOutputStream());
				//se verifica lo que el usuario solicita
				switch (mensajeRecibidoDividido[0]) {
				//en caso de que lo que el usuario solicite se insertar una cuenta
				case "insertar":
					//sentencia SQL para insertar una cuenta
					String insercionCuenta = "INSERT INTO valores (cuenta, valor) VALUES (" +
							mensajeRecibidoDividido[1] + ", " +
							mensajeRecibidoDividido[2] + ")";
					//se prepara la sentencia para ejecución
					PreparedStatement pstmInsercion = conexion.prepareStatement(insercionCuenta);
					//se ejecuta la sentencia
					pstmInsercion.execute();
					//se devuelve al cliente el mensaje
					mensajeEnviar = "Registro grabado OK";
					out.writeUTF(mensajeEnviar);
					break;
			
				//en caso de que el cliente desee consultar la cuenta
				case "consultar":
					//sentencia para consultar una cuenta
					String consultaCuenta = "SELECT id, cuenta, valor FROM valores WHERE cuenta = '" + 
							mensajeRecibidoDividido[1] + "'";
					//se prepara la sentencia para ejecución
					PreparedStatement pstmConsulta = conexion.prepareStatement(consultaCuenta);
					//se ejecuta la consulta y se almacena su resultado para verificación
					ResultSet resultadoConsultaCuenta = pstmConsulta.executeQuery();
					//se verifica que el resultado de la consulta tenga al menos un registro
					if (resultadoConsultaCuenta.next()) {
						//en caso de que haya al menos un registro se indica al cliente
						mensajeEnviar = "1;";
						//se preparan los datos al cliente de la cuenta
						String[] cuenta = {
								resultadoConsultaCuenta.getString(1),
								resultadoConsultaCuenta.getString(2),
								resultadoConsultaCuenta.getString(3)
						};
						//se deja lista la cadena de texto a enviar al cliente
						mensajeEnviar += cuenta[0] + ";" +
								cuenta[1] + ";" + 
								cuenta[2];
					} else {
						//se indica al cliente que no se encontró una cuenta
						mensajeEnviar = "0";
					}
					//se envia el mensaje construido
					out.writeUTF(mensajeEnviar);
					break;
					
				default:
					break;
				}
			}
		} catch (Exception e) {
			//se presenta al usuario programador el inconveniente presentado con los recursos externos para corrección
			System.out.println(e.toString());
		}
	}
}
