package superheroes;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static void main (String[] args) {
		
		Scanner sc = new Scanner(System.in);

		SuperheroesDatabase superheroes = new SuperheroesDatabase();
		
		char menuOption = 'a';

		// Main menu loop
		do {
			System.out.println("Escoja una opción: ");
			System.out.println("  1) Crear las tablas \"escena\" y \"rival\".");
			System.out.println("  2) Cargar datos  de las tablas \"escena\", \"protagoniza\" y \"rival\".");
			System.out.println("  3) Mostrar el catálogo.");
			System.out.println("  4) Obtener la duración de una película.");
			System.out.println("  5) Obtener las escelas de las películas en las que aparece un villano.");
			System.out.println("  6) Desenmascarar a un superheroe.");
			System.out.println("  0) Salir de la aplicación.");

			// Read user's option and check that it is a valid option
			menuOption = 'a';
			do {
				String line = sc.nextLine();
				if (line.length()==1) {
					menuOption = line.charAt(0);
				}
				if (menuOption<'0' || menuOption>'6') {
					System.out.println("Opción incorrecta.");
				}
			} while (menuOption<'0' || menuOption>'6');

			// Call a specific method depending on the option
			switch (menuOption) {
			case '1':
				System.out.println("Creando tabla \"escena\"...");
				superheroes.createTableEscena();
				System.out.println("Creando tabla \"rival\"...");
				superheroes.createTableRival();
				break;
			case '2':
				System.out.println("Cargando datos de la tabla \"escena\"...");
				int n = superheroes.loadEscenas("escenas.csv");
				System.out.println("Se han cargado " + n + " entradas.");
				System.out.println("Cargando datos de la tabla \"protagoniza\" y \"rival\"...");
				n = superheroes.loadProtagoniza("protagonistas.csv");
				System.out.println("Se han cargado " + n + " entradas.");
				break;
			case '3':
				System.out.println("Catálogo disponible:");
				System.out.println(superheroes.catalogo());
				break;
			case '4':
				System.out.println("¿De qué película quieres saber la duración?");
				String nombre_pelicula = sc.nextLine();
				System.out.println("Duración total de " + nombre_pelicula + ":");
				System.out.println(superheroes.duracionPelicula(nombre_pelicula) + " minutos.");
				break;
			case '5':
				System.out.println("¿De qué villano quieres consultar las escenas de sus películas?");
				String villano = sc.nextLine();
				System.out.println("Escenas de las películas en las que participa " + villano + ":");
				System.out.println(superheroes.getEscenas(villano));
				break;
			case '6':
				System.out.println("¿Cuál es el nombre del superhéroe al que quieres desenmascarar?");
				String nombre = sc.nextLine();
				System.out.println("¿Y su apellido?");
				String apellido = sc.nextLine();
				if (superheroes.desenmascara(nombre, apellido, "avatar_desenmascarado.jpg"))
					System.out.println("¡" + nombre + " " + apellido + " ha sido desenmascarado!");
				else
					System.out.println("La identidad secreta de " + nombre + " " + apellido + " sigue siendo todo un misterio.");
				break;
			}

			if (menuOption!='0')
				System.out.println("¿Qué más desea hacer?");
			else
				System.out.println("¡Hasta pronto!");
		} while (menuOption!='0');

		sc.close();
		
		superheroes.closeConnection();
		
	}

}
