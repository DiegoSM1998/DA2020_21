import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Principal {

	static Scanner reader = new Scanner(System.in);
	static long idEdge = 0;

	public static void main(String[] args) {
		menu();
	}

	
	
	private static void menu() { 
		int opcion; 
		boolean control = true; 
		do { 
			do { 
				System.out.println("\n***     MENU     ***\n"); 
				System.out.println("1.- Imprimir la información de todos los pueblos (Tarea 1)."); 
				System.out.println("2.- Imprimir información sobre los grafos."); 
				System.out.println("3.- Dibujar los grafos (Tarea 2)."); 
				System.out.println("4.- Dibujar los árboles de expansión mínima con PRIM (Tarea 3)."); 
				System.out.println("5.- Dibujar los árboles de expansión mínima con KRUSKAL (Tarea 3).");
				System.out.println("6.- Dibujar el árbole de expansión mínima con PRIM del grafo "
						+ "generado por la unión de todos los nodo raíz (Tarea4)"); 
				System.out.println("7.- Dibujar el árbole de expansión mínima con KRUSKAL del grafo"
						+ "generado por la unión de todos los nodo raíz (Tarea4)"); 
				System.out.println("8.- Salir."); 
				System.out.println(); 
				opcion = controlarNumero("\nSeleccione entre 1-8:"); 
			} while (opcion < 1 && opcion > 8);

			long inicio; 
			long fin; 
			double tiempo; 
			final File carpeta = new File("./xml"); 
			
			switch (opcion) { 
			case 1: 

				inicio = System.currentTimeMillis(); 

				listarFicherosPorCarpeta(carpeta); 

				fin = System.currentTimeMillis(); 

				tiempo = (double) ((fin - inicio)); 
				
				System.out.println("El método ha tardado " + tiempo / 1000 + " segundos"); 
			
				break; 
			case 2: 

				inicio = System.currentTimeMillis(); 

				imprimirGrafo(carpeta); 

				fin = System.currentTimeMillis(); 

				tiempo = (double) ((fin - inicio)); 

				System.out.println("El método ha tardado " + tiempo / 1000 + " segundos"); 

				break; 
			case 3: 

				inicio = System.currentTimeMillis(); 

				dibujarGrafo(carpeta); 

				fin = System.currentTimeMillis(); 

				tiempo = (double) ((fin - inicio)); 

				System.out.println("El método ha tardado " + tiempo / 1000 + " segundos"); 

				break; 
			case 4: 
				inicio = System.currentTimeMillis(); 

				mstPRIM(carpeta); 

				fin = System.currentTimeMillis(); 

				tiempo = (double) ((fin - inicio)); 

				System.out.println("El método ha tardado " + tiempo / 1000 + " segundos"); 
				
				break; 
			case 5: 
				inicio = System.currentTimeMillis(); 

				dibujarGrafoKruskal(carpeta); 

				fin = System.currentTimeMillis(); 

				tiempo = (double) ((fin - inicio)); 

				System.out.println("El método ha tardado " + tiempo / 1000 + " segundos"); 
				
				break; 
			case 6: 
				inicio = System.currentTimeMillis(); 

				arbolRaices(carpeta, 'p'); 

				fin = System.currentTimeMillis(); 

				tiempo = (double) ((fin - inicio)); 

				System.out.println("El método ha tardado " + tiempo / 1000 + " segundos"); 
				
				break; 
			case 7: 
				inicio = System.currentTimeMillis(); 

				arbolRaices(carpeta, 'k'); 

				fin = System.currentTimeMillis(); 

				tiempo = (double) ((fin - inicio)); 

				System.out.println("El método ha tardado " + tiempo / 1000 + " segundos"); 
				
				break; 
			case 8: 
				System.err.println("Programa finalizado"); 
				control = false; 

			}

		} while (control);

		reader.close(); 
	}
	
	

	private static void arbolRaices(File carpeta, char tipo) { 
		ArrayList<Node> raices = new ArrayList<Node>(); 
		raices = obtenerRaices(carpeta, tipo); 

		Grafo grafoRaices = new Grafo(); 
		grafoRaices = enlazarNodosRaiz(raices, tipo); 

		Grafo arbolRaices = new Grafo(); 

		if (tipo == 'p') 
			arbolRaices = grafoRaices.Prim(); 
		else 
			arbolRaices = grafoRaices.Kruskal(); 
		
		arbolRaices = obtenerRaiz(arbolRaices); 
		System.out.println(arbolRaices.getRaiz());
		arbolRaices.dibujarArbol("arbolesRaices"); 
	}
	
	

	private static Grafo enlazarNodosRaiz(ArrayList<Node> raices, char tipo) { 
		Grafo grafoRaices = new Grafo(); 
		int id = 0; 

		if (tipo == 'p') 
			grafoRaices.setNombre("Grafo raíces PRIM"); 
		else 
			grafoRaices.setNombre("Grafo raíces KRUSKAL"); 
		while (!raices.isEmpty()) { 
			Node r1 = raices.remove(0); 
			grafoRaices.addNodo(r1); 
			for (Node r2 : raices) { 
				if (r1.getId() != r2.getId()) { 
					Edge eAux = new Edge(calcularDistancia(r1, r2), r1, r2, id++); 
					grafoRaices.addEdge(eAux); 
				}
			}
		}
		return grafoRaices; 
	}
	
	

	private static double calcularDistancia(Node r1, Node r2) { 
		return (Math.abs(r1.getX() - r2.getX()) + Math.abs(r1.getY() - r2.getY())); 
	}
	
	

	private static ArrayList<Node> obtenerRaices(File carpeta, char tipo) { 
		
		ArrayList<Node> raices = new ArrayList<Node>(); 
		for (final File ficheroEntrada : carpeta.listFiles()) { 
			if (ficheroEntrada.isDirectory()) { 
			} else { 
				if (!ficheroEntrada.getName().startsWith(".")) { 
					Grafo grafo = new Grafo(ficheroEntrada.getAbsoluteFile()); 
					Grafo arbolK = new Grafo(); 
					if (tipo == 'p') 
						arbolK = grafo.Prim(); 
					else 
						arbolK = grafo.Kruskal(); 
					arbolK = obtenerRaiz(arbolK); 
					System.out.println(arbolK.getNombre() + ", ID Raíz: " 
							+ arbolK.getRaiz().getId() + ", Media: "
							+ arbolK.getMedia() + ", Varianza: " 
							+ arbolK.getVarianza() + "."); 
					raices.add(arbolK.getRaiz()); 
				}
			}
		}
		return raices; 
	}
	
	

	private static Grafo obtenerRaiz(Grafo arbol) { 
		double min = 99999999; 
		Node raiz = new Node(); 
		for (int i = 0; i < arbol.nodes.size(); i++) { 
			arbol.setRaiz(arbol.getNodo(i)); 
			arbol.recorrerPreorden(arbol); 
			double var = arbol.getVarianza(); 
			if (var <= min) { 
				min = var; 
				raiz = arbol.getNodo(i); 
			}
		}
		arbol.setRaiz(raiz); 
		arbol.recorrerPreorden(arbol); 
		return arbol; 
	}
	
	
	private static void dibujarGrafoKruskal(File carpeta) { 
		for (final File ficheroEntrada : carpeta.listFiles()) { 
			if (ficheroEntrada.isDirectory()) { 
			} else { 
				if (!ficheroEntrada.getName().startsWith(".")) { 
					Grafo grafo = new Grafo(ficheroEntrada.getAbsoluteFile()); 
					Grafo arbolK = grafo.Kruskal(); 
					arbolK.dibujarGrafo("kruskal"); 
				}
			}
		}
	}

	
	private static void mstPRIM(File carpeta) { 
		for (final File ficheroEntrada : carpeta.listFiles()) { 
			if (ficheroEntrada.isDirectory()) { 
			} else { 
				if (!ficheroEntrada.getName().startsWith(".")) { 
					Grafo grafo = new Grafo(ficheroEntrada.getAbsoluteFile()); 
					Grafo mstPRIM = grafo.Prim(); 
					mstPRIM.dibujarGrafo("prim"); 
				}
			}

		}
	}

	
	
	private static void imprimirGrafo(File carpeta) { 
		for (final File ficheroEntrada : carpeta.listFiles()) { 
			if (ficheroEntrada.isDirectory()) { 
			} else { 
				if (!ficheroEntrada.getName().startsWith(".")) { 
					Grafo grafo = new Grafo(ficheroEntrada.getAbsoluteFile()); 
					System.out.println(grafo); 
				}
			}

		}

	}

	
	
	private static void dibujarGrafo(File carpeta) { 
		for (final File ficheroEntrada : carpeta.listFiles()) { 
			if (ficheroEntrada.isDirectory()) { 
			} else { 
				if (!ficheroEntrada.getName().startsWith(".")) { 
					Grafo grafo = new Grafo(ficheroEntrada.getAbsoluteFile()); 
					grafo.dibujarGrafo("grafos"); 
				}
			}

		}
	}
	
	
	public static int controlarNumero(String texto) { 
		int num = 0; 
		boolean control = true; 

		do { 
			try { 
				System.out.println(texto); 
				num = reader.nextInt(); 
				control = false; 
			} catch (Exception e) { 
				System.err.println("\nSolo se permiten números"); 
				reader.next(); 
			}
		} while (control);

		return num; 
	}

	
	
	private static void listarFicherosPorCarpeta(File carpeta) { 
		
		System.out.println(
				"<Población>,<Cantidad de nodos>,<Cantidad de arcos>,"
				+ "<Longitud total de los arcos>,<cantidad de coord X"
				+ " pares>,<cantidad de coord Y impares>"); 
		SAXParserFactory factory = SAXParserFactory.newInstance(); 
		try { 
			SAXParser saxParser = factory.newSAXParser(); 
			DefaultHandler handle = new DefaultHandler() { 
				boolean data = false, pedirPoblacion = false, pedirY = false, pedirX = false, pedirLongitud; 
				int nNodes = 0, nEdges = 0, nxpar = 0, nyimpar = 0; 
				double nLongiTotal = 0; 
				String dLength, dX, dY, poblacion = ""; 

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException { 
					if (qName.equals("data")) { 
						data = true; 
						String nkey = attributes.getValue("key"); 
						if (nkey.equals("d0")) { 
							pedirPoblacion = true; 
						}
						if (nkey.equals(dY)) { 
							pedirY = true; 
						}
						if (nkey.equals(dX)) { 
							nNodes++; 
							pedirX = true; 
						}
						if (nkey.equals(dLength)) { 
							nEdges++; 
							pedirLongitud = true; 
						}
					}

					if (qName.equals("key")) { 
						switch (attributes.getValue("attr.name")) { 
						case "length":
							dLength = attributes.getValue("id"); 
							break; 
						case "x": 
							dX = attributes.getValue("id"); 
							break; 
						case "y": 
							dY = attributes.getValue("id"); 
							break; 
						}
					}
				}

				public void endDocument() throws SAXException { 
					System.out.println(poblacion + ", " + nNodes + ", " + nEdges + ", "
							+ new DecimalFormat("#.00").format(nLongiTotal) + ", " + 
							nxpar + ", " + nyimpar + "."); 
					nNodes = 0; 
					nEdges = 0; 
					nLongiTotal = 0; 
					nxpar = 0; 
					nyimpar = 0; 
				}

				public void characters(char[] ch, int start, int length) throws SAXException { 
					if (data) { 
						if (pedirX) { 
							double cordenadax = Double.parseDouble(new String(ch, start, length)); 
							if (((int) cordenadax % 2) == 0) { 
								nxpar++; 
							}
							pedirX = false; 
						}
						if (pedirY) { 
							double cordenaday = Double.parseDouble(new String(ch, start, length)); 
							if (((int) (cordenaday) % 2) != 0) { 
								nyimpar++; 
							}
							pedirY = false; 
						}
						if (pedirLongitud) { 
							nLongiTotal += Double.parseDouble(new String(ch, start, length)); 
							pedirLongitud = false; 
						}
						data = false; 
					}

					if (pedirPoblacion) { 
						poblacion = new String(ch, start, length); 
						pedirPoblacion = false; 
					}

				}

			};

			for (final File ficheroEntrada : carpeta.listFiles()) { 
				if (ficheroEntrada.isDirectory()) { 
				} else { 
					if (!ficheroEntrada.getName().startsWith(".")) { 
						saxParser.parse(ficheroEntrada.getAbsoluteFile(), handle); 
					}
				}
			}

		} catch (ParserConfigurationException e) { 
			e.printStackTrace(); 
		} catch (SAXException e) { 
			e.printStackTrace(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}

	}

}