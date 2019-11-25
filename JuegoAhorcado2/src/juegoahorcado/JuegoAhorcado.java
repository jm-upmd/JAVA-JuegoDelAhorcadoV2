
package juegoahorcado;

import java.util.Scanner;
import java.util.TreeSet;

public class JuegoAhorcado {
	
	// Array de strings. Cada string representa un estado de la horca pintada con caracteres.
	// Cada string de cada dibujo de la horca se ha escrito  en varivas líneas concatenadas para que 
	// visualmente sea más legible el patrón de dibujo que representan.
	// Por ejemplo, la primera horca podría representarse con el siguiente string en una sola línea:
	//   "  +---+  \n  |   |  \n      |  \n      |  \n      |  \n      |  \n=========\n"
		
	private static final String[] IMAGENES_AHORCADO = {
		//primera horca	
		"  +---+  \n" +
		"  |   |  \n" +
		"      |  \n" +
		"      |  \n" +
		"      |  \n" +
		"      |  \n" +
		"=========\n" ,
		//segunda horca
		"         \n" +
		"  +---+  \n" +
		"  |   |  \n" +
		"  O   |  \n" +
		"      |  \n" +
		"      |  \n" +
		"      |  \n" +
		"=========\n" ,
		// tercera horca
		"         \n" +
		"  +---+  \n" +
		"  |   |  \n" +
		"  O   |  \n" +
		"  |   |  \n" +
		"      |  \n" +
		"      |  \n" +
		"=========\n" ,
		
		"         \n" +
		"  +---+  \n" +
		"  |   |  \n" +
		"  O   |  \n" +
		" /|   |  \n" +
		"      |  \n" +
		"      |  \n" +
		"=========\n" ,
		
		"         \n" +
		"  +---+  \n" +
		"  |   |  \n" +
		"  O   |  \n" +
		" /|\\  |  \n" +  // caracter de barra invertida \ hay que duplicarlo para que lo interprete como caracter literal y no de escape.
		"      |  \n" +
		"      |  \n" +
		"=========\n" ,
		
		"         \n" +
		"  +---+  \n" +
		"  |   |  \n" +
		"  O   |  \n" +
		" /|\\  |  \n" +
		" /    |  \n" +
		"      |  \n" +
		"=========\n" ,
		
		"         \n" +
		"  +---+  \n" +
		"  |   |  \n" +
		"  O   |  \n" +
		" /|\\  |  \n" +
		" / \\  |  \n" +
		"      |  \n" +
		"=========\n" };
	
	static Scanner sc =  new Scanner(System.in);
	
	static final int MAX_FALLOS = IMAGENES_AHORCADO.length -1;
	
	static final String[] PALABRAS = {"ORDENADOR", "CONFEDERACION", "ALTAVOZ","TIBURON","CONSTELACION","MOTOCICLETA",
			"ESTRATIFICACION","REPRESENTANTE","FUNCIONALIDAD","POLIMORFISMO","EXPERIMENTACION","ESQUELETO","FUNICULAR"};
		
	static String palabraADescubrir;  // Palabra a descubrir.
		
	// Palabra en construcción. Array donde se irá construyendo la palabra a medida que el usuario da letras.
	static char[] palapraEnConstruccion; 
	
	// Colección para almacenar las letras ya usadas y mostrarlas en la partida.
	// Usamos una TreeSet porque no almacena duplicados y además ordena los elementos.
	// En el caso de guardar Characters los ordena alfabéticamente por defectro. 
	// Ojo, por defecto no ordena bien la 'ñ' . Habría que usar un objeto Comparator para 
	// establecer el orden correcto (eso lo veremos más adelante)
	
	static TreeSet<Character> letrasUtilizadas;
	
	static int contFallos = 0;  // Contador de fallos. Se incrementa por cada letra suministrada que no existenta en la palabra a descubrir.
	static int contTiradas = 0; // Contador del número de tiradas (letras suministradas) en la partida.
	static int aciertos; 	    // Número de ocurrencias de la letra suministrada por el usuario en  la palabra a descubrir.
	
	static char letra;  // Letra a comprobar si existe en palabraADescrubrir .	
	
	 
	public static void main(String[] args) {
		
		palabraADescubrir = damePalabra();  // Palabra aleatoria de entre las existentes en PALABRAS.
		
		palapraEnConstruccion = new char[palabraADescubrir.length()]; // Se irán añadiendo letras coincidentes suministradas por usuario.
		
		letrasUtilizadas = new TreeSet<>();  // Crea colección para guardar letras utilizadas.
		
		letra = '_'; // Caracter a guardar en palabraEnConstruccion[i] cuando la letra de la posición i aun no ha sido suministrada.				
		
		nuevaPartida(); // Crea una nueva partida.(Pinta horca inicial y patrón (huecos) de la palabra a descubrir ...)
		
		for(;;) { 			// Bucle sin condición de parada ni contadores. También se puede poner while (true){
			
			letra = pideLetra("Escribe la letra: ");  	// Pide letra por consola.
			
			borraConsola();
			
			// Comprueba ocurencias de la letra en la palabra a descubrir, las añade a la palabra en construcción, 
			// pinta la palabra en construcción actualizada y devuelve número de aciertos (ocurrencias de la letra).
			aciertos = hazJugada();  	
			
			// Si letra no está en la palabra a descubrir incrementa contador de fallos.
			contFallos = aciertos == 0 ? contFallos + 1 : contFallos;  	
			
			pintaHorca(contFallos); // Repinta horca según número de fallos.
			
			pintaContadoresVidasYTiradas();
			
			pintaListaLetrasUsadas();
			
			// partidaTerminada() evalua si se ha llegado al total de fallos o se ha completado la palabra.
			// En ambos casos la partida se da por terminada y escribe el mensaje de victoria o derrota por consola,
			// y devuelve true indicando fin de la partida.
			// Si no se da ninguna de las condiciones anteriores devuelve false para  seguir pidiendo letras
			// en la partida en curso			
			
			if(partidaTerminada() ) { // Si la partida ha terminado preguntamos si quiere jugar otra
				// Pregunta por consola si jugar otra partida 
				if( ! nuevaPartida("\n¿Quieres jugar otra partida? [S/N]: ") ) 
					break;  	// Si no se quiere jugar otra partida entonces sale del bucle while
			}	
			
		} // fin for
		
		System.out.println("\n¡Hasta pronto!");

	}  // fin main()
	
	
	/**
	 * Pregunta si se quiere jugar otra partida y en caso afirmativo inicializa las 
	 * varibles palabra, palabraParcial, y pinta por consola la horca inicial y el
	 * patrón con guiones bajos de la palabra a descubrir
	 * @return 0 si hemos pulsado S para jugar otra partida; -1 en caso contrario 
	 */
	private static boolean nuevaPartida(String t) {
		if(pideLetra(t) == 'S') {
			nuevaPartida();
			return  true;
		} else return false;
		
	}
	
	/**
	 * Crea una nueva partida. Para elo  inicializa las  varibles palabra, palabraParcial, 
	 * y pinta por consola la horca inicial y el patrón con guiones bajos de la palabra a descubrir
	 */
	private static void nuevaPartida() {
		borraConsola();
		palabraADescubrir = damePalabra();
		palapraEnConstruccion= new char[palabraADescubrir.length()];
		letrasUtilizadas.clear();  // Limpia colección de letras utilizadas.
		letra = '_';
		contFallos = 0;
		hazJugada();
		pintaHorca(0);
		pintaContadoresVidasYTiradas();
	}
	
	/**
	 * Evalua la jugada y escribe por consola mensaje de derrota o victoria en el caso de haber 
	 * agotado el número de intentos o haber completado la palabra respectivamente.
	 
	 * @return true en caso de que la partida haya terminado, bien por derrota o victoria
	 *         false en caso contrario (la partida continua).
	 */
	private static boolean partidaTerminada() {
		if (aciertos == 0 && contFallos == MAX_FALLOS) {  
			System.out.println("Ooooh, fallaste");
			System.out.println("La palabra era: " + palabraADescubrir);
			return true;
		} else if (String.valueOf(palapraEnConstruccion).equals(palabraADescubrir)) {
			System.out.println("¡ENHORABUENA, has resuelto la palabra!");
			return true;
		} else return false;
		

	}
	
	/**
	 * Pide un caracter por consola.
	 * @param t Texto a escribir por consola para pedir una letra.
	 * @return Caracter escrito por consola convertido a mayúscula.
	 */
	private static char pideLetra(String t) {
		System.out.print(t);
		sc.reset();
		return  Character.toUpperCase(sc.next().charAt(0));
		
		
	}
	
	/**
	 * Añade la letra a palabra, siempre que esta esté presente en PALABRA. La letra
	 * es añadida en la misma posición que se encuentra en PALABRA. Escribre palabra
	 * por consola.
	 * 
	 * Si letra = '_' pinta por consola solo guienes de posición de letras.
	 * @return número de letras añadidas a palabra 
	 *         -1 si letra = '_'
	 *         -1 si la letra ya fue usada en alguna tirada anterior
	 */

	private static int hazJugada() {
		int contador = -1;
		// Pinta por consola solo los guiones de posición de letra.
		if (letra == '_') {
			for (int i = 0; i < palapraEnConstruccion.length; i++) 
				palapraEnConstruccion[i] = '_';			

		} else if (!letrasUtilizadas.contains(letra)){ // Si la letra aun no ha sido utilizada
					
			// Mete en array palabraEnConstruccion los caracteres coincidentes en
			// palabraADescubrir
			
			contador = 0;
			for (int i = 0; i < palabraADescubrir.length(); i++) {
				if (palabraADescubrir.charAt(i) == letra) {  // Si la letra está en palabraADescubrir
					palapraEnConstruccion[i] = letra; // Añade letra al array palabraEnConstruccion.
					contador++;
				}				
			}
			
			letrasUtilizadas.add(letra); // Añade letra utilizada a la colección.			
		} 
		
		pintaPalabraEnConstruccion();
		return contador; // Número de caracteres coincidentes añadidos a palabra.

	}
	
	private static void pintaContadoresVidasYTiradas() {
		System.out.printf("Tiradas: %d\tVidas: %d\n", letrasUtilizadas.size(),MAX_FALLOS-contFallos);
	}
	
	private static void  pintaPalabraEnConstruccion() {
		for (int i = 0; i < palapraEnConstruccion.length; i++) 
			System.out.printf("%s ", palapraEnConstruccion[i]);
		System.out.println("\n");

	}

	private static void pintaHorca(int i) {
		System.out.println(IMAGENES_AHORCADO[i]);
		
	}
	
	/**
	 * Escribe 200 líneas en blanco para simular un borrado de consola
	 */
	private static void borraConsola() {
		
		//Versión para borrar consola MS-DOS
		/*try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}*/
		
		// Para consola Eclipse. Simula borrado metiendo líneas en blanco.
		for(int i=0; i< 200; i++) {
			System.out.println();
		}
	}
	
	/**
	 * Devuelve una palabra del array PALABRAS. La palabra corresponede a una  
	 * posición aleatoria en el rango  0 - (n-1),  donde n es el número de palabras 
	 * almacenadas en el array PALABRAS
	 * @return La palabra seleccionada aleatoriamente.
	 */
	private static String damePalabra() {
		int n = PALABRAS.length;
		
		// Genero intero aleatorio entre 0 y n - 1;

		n = (int)(Math.random()*n);

		return PALABRAS[n];

	}
	
	/**
	 * Pinta por consola la lista de letras que ya han sido utilizadas
	 */
	private static void pintaListaLetrasUsadas() {
		System.out.print("Letras usadas: ");
		System.out.println(letrasUtilizadas.toString());
	}
	

}
