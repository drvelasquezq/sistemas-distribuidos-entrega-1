package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

	public static void main(String[] args) {
		//host que el socket utilizara como dirección para conectarse
		final String HOST = "127.0.0.1";
		//puerto de la maquina a utilizar para conectarse
		final int PUERTO = 9000;
		//sirve para utilizar en las cadenas que se saben serán divididas y tratadas como arreglos
		final String separador = ";";
		//es para hacer más legible la lectura de las salidas del programa
		String delineado = "----------------------------";
		//es la clase que permitira obtener los datos por ls terminal para procesar
		Scanner scanner;
		//como se hará uso de recursos externos se valida con excepciones
		try {
			//se instancia la clase de las entradas de datos por terminal
			scanner = new Scanner(System.in);
			//se mantendrá un ciclo mientras que el usuario desee salir
			while (true) {
				//es la declaración de las opciones presentadas al usuario por terminal
				String mensajeMenu = "Ingrese la opción a ejecutar: " + "\n" +
						"1. Insertar un registro cuenta" + "\n" +
						"2. Consultar un registro cuenta" + "\n" +
						"3. Salir";
				//impresión por pantalla de las opciones
				System.out.println(mensajeMenu);
				//se recoge por terminal el dato de la opción que se elige
				String opcionElegida = scanner.nextLine();
				//un arreglo de opciones para determinar lo que el usuario eligio
				String[] opciones = {
						"1", //Insertar
						"2", //Consultar
						"3"  //Salir
				};
				//en caso de que el usuario haya elegido Insertar
				if (opcionElegida.equals(opciones[0])) {
					System.out.print("Ingrese el número de cuenta a registrar: ");
					//se recoge el dato que es el número de la cuenta
					String numeroCuenta = scanner.nextLine();
					System.out.print("Valor: ");
					//se recoge el dato que es el valor que se almacenará a la cuenta
					String valorCuenta = scanner.nextLine();
					//es la cadena con las partes separadas que se enviará al servidor para procesar
					String mensajeEnvioInsercionCuenta = "insertar" + separador + numeroCuenta + separador + valorCuenta;
					//instancia de la clase socket que permitira crear el enlace entre los programas
					Socket socketInsercionCuenta = new Socket(HOST, PUERTO);
					//canal de salida del socket
					DataOutputStream salidaInsercionCuenta = new DataOutputStream(socketInsercionCuenta.getOutputStream());
					salidaInsercionCuenta.writeUTF(mensajeEnvioInsercionCuenta);
					//canal de entrada del socket para leer lo que el servidor responde
					DataInputStream entradaInsercionCuenta = new DataInputStream(socketInsercionCuenta.getInputStream());
					String mensajeRecibidoInsercionCuenta = entradaInsercionCuenta.readUTF();
					//cierre del socket
					socketInsercionCuenta.close();
					//impresión por pantalla del mensaje recibido del servidor
					System.out.println(delineado + "\n" + 
							mensajeRecibidoInsercionCuenta + "\n" +
							delineado);
				}
				//en caso de que lo que el usuario elija sea consultar una cuenta
				if (opcionElegida.equals(opciones[1])) {
					System.out.print("Ingrese el número de cuenta a consultar: ");
					//se recoge el dato que es el número de la cuenta
					String numeroCuenta = scanner.nextLine();
					//es la cadena con las partes separadas que se enviará al servidor para procesar
					String mensajeEnvioConsultaCuenta = "consultar" + separador + numeroCuenta;
					//instancia de la clase socket que permitira crear el enlace entre los programas
					Socket socketConsultaCuenta = new Socket(HOST, PUERTO);
					//canal de salida del socket
					DataOutputStream salidaConsultaCuenta = new DataOutputStream(socketConsultaCuenta.getOutputStream());
					salidaConsultaCuenta.writeUTF(mensajeEnvioConsultaCuenta);
					//canal de entrada del socket para leer lo que el servidor responde
					DataInputStream entradaConsultaCuenta = new DataInputStream(socketConsultaCuenta.getInputStream());
					String mensajeRecibidoConsultaCuenta = entradaConsultaCuenta.readUTF();
					//cierre del socket
					socketConsultaCuenta.close();
					//división de la respuesta del servidor
					String[] mensajeRecibidoConsultaCuentaDividido = mensajeRecibidoConsultaCuenta.split(";");
					//se valida que la cuenta exista con un indicador
					if (mensajeRecibidoConsultaCuentaDividido[0].equals("1")) {
						String mensajeSalidaCuenta = "ID: " + 
								mensajeRecibidoConsultaCuentaDividido[1] + ", " +
					            "Número de cuenta: " + 
								mensajeRecibidoConsultaCuentaDividido[2] + ", " +
								"Valor: " + mensajeRecibidoConsultaCuentaDividido[3];
						//se presenta al usuario la información de la cuenta
						System.out.println(delineado + "\n" + 
								mensajeSalidaCuenta + "\n" +
								delineado);
					} else {
						//se presenta al usuario que no se encontró la cuenta
						System.out.println(delineado + "\n" + 
								"El número de cuenta consultado no existe" + "\n" +
								delineado);
					}
				}
				//en caso de que el usuario elija terminar el programa
				if (opcionElegida.equals(opciones[2])) {
					//se finaliza el ciclo con está condición
					break;
				}
			}
		} catch (Exception e) {
			//se presenta al usuario programador el inconveniente presentado con los recursos externos para corrección
			System.out.println(e.toString());
		}
	}

}
