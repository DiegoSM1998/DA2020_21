import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Grafo {
	String nombre;
	ArrayList<Node> nodes = new ArrayList<Node>();
	ArrayList<Edge> edges = new ArrayList<Edge>();
	Node raiz;
	double media;
	double varianza;
	
	public Grafo(File archivo) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handle = new DefaultHandler() {
				boolean data = false, pedirPoblacion = false, pedirY = false, pedirX = false, pedirLongitud,
						edg = false, nde = false;
				long idNode = 0, idSource = 0, idTarget = 0;
				double x = 0, y = 0, longitud = 0;
				String dLength, dX, dY;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					if (qName.equals("node")) {
						idNode = Long.parseLong(attributes.getValue("id"));
						nde = true;
					}
					if (qName.equals("edge")) {
						idSource = Long.parseLong(attributes.getValue("source"));
						idTarget = Long.parseLong(attributes.getValue("target"));
						edg = true;
					}
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
							pedirX = true;
						}
						if (nkey.equals(dLength)) {
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

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (nde && idNode != 0 && x != 0 && y != 0) {
						nodes.add(new Node(idNode, x, y));
						idNode = 0;
						x = 0;
						y = 0;
						nde = false;
					}
					if (edg && longitud != 0 && idSource != 0 && idTarget != 0) {
						edges.add(new Edge(longitud, buscarNodo(idSource), 
								buscarNodo(idTarget), Principal.idEdge++));
						edg = false;
						longitud = 0;
						idSource = 0;
						idTarget = 0;
					}

					if (data) {
						if (pedirX) {
							x = Double.parseDouble(new String(ch, start, length));
							pedirX = false;
						}
						if (pedirY) {
							y = Double.parseDouble(new String(ch, start, length));
							pedirY = false;
						}
						if (pedirLongitud) {
							longitud = Double.parseDouble(new String(ch, start, length));
							pedirLongitud = false;
						}
						if (pedirPoblacion) {
							nombre = new String(ch, start, length);
							pedirPoblacion = false;
						}
						data = false;
					}
				}

			};
			media = 0;
			saxParser.parse(archivo.getAbsoluteFile(), handle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Grafo() {
	}
	
	public void dibujarArbol(String nombreCarpeta) {
													
		int xDimension = 0, yDimension = 0; 
		int[] cotaArbol = acotarImagenArbol();
		xDimension = cotaArbol[1] * 200;
		yDimension = cotaArbol[0] * 200;

		BufferedImage bufferedImage = new BufferedImage(xDimension, yDimension, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bufferedImage.createGraphics();

		g2.setColor(Color.black); 
		g2.fillRect(0, 0, xDimension, yDimension); 
		g2.setColor(Color.red);

		dibujarNodosArbol(g2, xDimension, yDimension, cotaArbol);
		RenderedImage rendImage = bufferedImage;

		crearCarpeta(nombreCarpeta);
		File file = new File("./" + nombreCarpeta + "/" + nombre + ".png");
		try {
			ImageIO.write(rendImage, "png", file);
			System.out.println("Arbol de " + nombre + " dibujado");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int[] acotarImagenArbol() {
									  

		int[] dimensiones = { 0, 0 };
		for (Node n : nodes) {
			if (n.getAltura() > dimensiones[0]) {
				dimensiones[0] = n.getAltura();
			}
		}
		dimensiones[0]++;
		dimensiones[1] = mayorNumNodosNivel(calcularNumNodosNivel(dimensiones[0]));
		return dimensiones;
	}

	
	private int mayorNumNodosNivel(int[] numNodosAltura) {

		int anchura = 0;
		for (int i : numNodosAltura) {
			if (i > anchura) {
				anchura = i;
			}
		}
		return anchura;
	}

	
	private void dibujarNodosArbol(Graphics2D g2, int xDim, int yDim, int[] dimensiones) {
																						  

		int[] numNodosNivel = calcularNumNodosNivel(dimensiones[0]);
		int x = 0, y = 0, r = 20;
		int anchura = 0, altura = 0;
		int[][][] aristas = calcularAristas(dimensiones[1], 
											numNodosNivel.length);
		int[] anchuras = new int[numNodosNivel.length];
		altura = yDim / (dimensiones[0] + 1);
		g2.setStroke(new BasicStroke(3));

		for (int i = 0; i < dimensiones[0]; i++) {
			anchura = xDim / (numNodosNivel[i] + 1);
			anchuras[i] = anchura;
			y += altura;
			x = 0;
			for (int j = 0; j < numNodosNivel[i]; j++) {
				g2.setColor(Color.red);
				x += anchura;
				g2.fillOval(x, y, r, r);
				g2.setColor(Color.white);
				aristas[i][j][0] = x;
				aristas[i][j][1] = y;
				if (i > 0) {
					int k;
					boolean encontrar = false;
					for (k = 0; k < numNodosNivel[i - 1]; k++) {
						if (aristas[i - 1][k][2] > 0) {
							encontrar = true;
							break;
						}
					}
					if (encontrar) {
						g2.drawLine(x + r / 2, 
									y + r / 2, 
									aristas[i - 1][k][0] + r / 2, 
									aristas[i - 1][k][1] + r / 2);
						aristas[i - 1][k][2]--;
					}
				}
			}
		}
		mostrarMatriz(aristas);
	}

	
	private void mostrarMatriz(int[][][] m) { 
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[0].length; j++) {
				System.out.print(m[i][j][0] + ", " 
							   + m[i][j][1] + ", " 
							   + m[i][j][2] + "\t"); 
			}
			System.out.println();
		}
		System.out.println();
	}

	
	private int[] calcularNumNodosNivel(int dimensionesY) {

		int altura = 0;
		int[] numNodosNivel = new int[dimensionesY];

		while (altura != dimensionesY) {
			for (Node n : nodes) {
				if (n.getAltura() == altura) {
					numNodosNivel[altura]++;
				}
			}
			altura++;
		}
		return numNodosNivel;
	}

	
	private int[][][] calcularAristas(int mayorNumNodosNivel, int altura) {

		int[][][] aristas = new int[altura][mayorNumNodosNivel][3];
		int cont;
		for (int i = 0; i < altura; i++) {
			cont = 0;
			for (Node n : nodes) {
				if (n.getAltura() == i) {
					aristas[i][cont][2] = n.getArcosAdyacentes().size();
					cont++;
				}
			}
		}
		return aristas;
	}
	
	
	protected Node buscarNodo(long id) {
		for (Node node : nodes) {
			if (node.getId() == id) {
				return node;
			}
		}
		return null;
	}
	
	public void dibujarGrafo(String nombreCarpeta) {
		int xResta = 0, yResta = 0;
		double xMinMax[] = { 100000.00, -100000.00 };
		double yMinMax[] = { 100000.00, -100000.00 };

		calcularMinMax('x', xMinMax);
		calcularMinMax('y', yMinMax);

		xResta = (int) (Math.abs((xMinMax[1] - xMinMax[0])) * 40000 + 1000);
		yResta = (int) (Math.abs((yMinMax[1] - yMinMax[0])) * 40000 + 1000);

		BufferedImage bufferedImage = new BufferedImage(xResta, yResta, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bufferedImage.createGraphics();

		g2.setColor(Color.black); 
		g2.fillRect(0, 0, xResta + 1000, yResta + 1000); 
		g2.setColor(Color.red);

		dibujarNodos(g2, xMinMax, yMinMax, yResta);

		g2.setColor(Color.white); 
		g2.setStroke(new BasicStroke(5)); 

		dibujarEdges(g2, xMinMax, yMinMax, yResta);

		RenderedImage rendImage = bufferedImage;
		crearCarpeta(nombreCarpeta);
		File file = new File("./" + nombreCarpeta + "/" + nombre + ".png");
		try {
			ImageIO.write(rendImage, "png", file);
			System.out.println("Grafo de " + nombre + " dibujado");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
	public double[] buscarNodos(Edge e) {

		double nodos[] = new double[4];

		for (int i = 0; i < nodes.size(); i++) {
			if (e.getSource().getId() == nodes.get(i).getId()) {
				nodos[0] = nodes.get(i).getX();
				nodos[1] = nodes.get(i).getY();
			}
			if (e.getTarget().getId() == nodes.get(i).getId()) {
				nodos[2] = nodes.get(i).getX();
				nodos[3] = nodes.get(i).getY();
			}
		}

		return nodos;

	}

	
	public void calcularMinMax(char eje, double[] minMax) {

		if (eje == 'x') {
			for (int i = 0; i < this.nodes.size(); i++) {
				if (nodes.get(i).getX() > minMax[1]) {
					minMax[1] = nodes.get(i).getX();
				}
				if (nodes.get(i).getX() < minMax[0]) {
					minMax[0] = nodes.get(i).getX();
				}
			}
		} else if (eje == 'y') {
			for (int i = 0; i < this.nodes.size(); i++) {
				if (nodes.get(i).getY() > minMax[1]) {
					minMax[1] = nodes.get(i).getY();
				}
				if (nodes.get(i).getY() < minMax[0]) {
					minMax[0] = nodes.get(i).getY();
				}
			}
		}

	}

	
	public void dibujarNodos(Graphics2D g2, double[] xMinMax, double[] yMinMax, int yResta) {
		
		int x, y, r = 20;
		for (int i = 0; i < nodes.size(); i++) {
			x = (int) ((nodes.get(i).getX() - xMinMax[0]) * 30000 + 500);
			y = (int) ((nodes.get(i).getY() - yMinMax[0]) * 30000 + 500);

			y = yResta - y; 

			x = x - (r / 2);
			y = y - (r / 2);
			g2.fillOval(x, y, r, r);
		}
		g2.setColor(Color.blue);

		x = (int) ((raiz.getX() - xMinMax[0]) * 30000 + 500);
		y = (int) ((raiz.getY() - yMinMax[0]) * 30000 + 500);
		y = yResta - y;

		x = x - (r / 2);
		y = y - (r / 2);
		g2.fillOval(x, y, 30, 30);

	}

	
	public void dibujarEdges(Graphics2D g2, double[] xMinMax, double[] yMinMax, int yResta) {
		
		int iniEdgeX, finEdgeX, iniEdgeY, finEdgeY;
		for (int i = 0; i < edges.size(); i++) {

			double nodos[] = new double[4];
			nodos = buscarNodos(edges.get(i));

			iniEdgeX = (int) ((nodos[0] - xMinMax[0]) * 30000 + 500);
			iniEdgeY = (int) ((nodos[1] - yMinMax[0]) * 30000 + 500);
			finEdgeX = (int) ((nodos[2] - xMinMax[0]) * 30000 + 500);
			finEdgeY = (int) ((nodos[3] - yMinMax[0]) * 30000 + 500);

			iniEdgeY = yResta - iniEdgeY;
			finEdgeY = yResta - finEdgeY;

			g2.drawLine(iniEdgeX, iniEdgeY, finEdgeX, finEdgeY);
		}
	}
	
	public void crearCarpeta(String nombreCarpeta) {
		File directorio = new File("./" + nombreCarpeta);
		if (!directorio.exists()) {
			if (directorio.mkdirs()) {
				System.out.println("Directorio creado");
			} else {
				System.out.println("Error al crear directorio");
			}
		}
	}
	
	public void quicksort(int izq, int der) {

		Edge pivote = edges.get(izq);
		int i = izq;
		int j = der;
		Edge aux;

		while (i < j) { 
			while (edges.get(i).getLength() <= pivote.getLength() && i < j)
				i++; 
			while (edges.get(j).getLength() > pivote.getLength())
				j--; 
			if (i < j) { 
				aux = edges.get(i); 
				edges.set(i, edges.get(j));
				edges.set(j, aux);
			}
		}
		edges.set(izq, edges.get(j));
		edges.set(j, pivote);

		if (izq < j - 1)
			quicksort(izq, j - 1); 
		if (j + 1 < der)
			quicksort(j + 1, der);

	}
	
	public Grafo Kruskal() {
        Grafo arbolK = new Grafo();
        arbolK.setNombre(nombre);
        ArrayList<Node> nodos = nodes;
        quicksort(0, edges.size() - 1);

        ArrayList<Edge> arcos = edges;

        for (int j = 0; j < nodos.size(); j++) {
            arbolK.addNodo(nodos.get(j));
        }
        Edge arista = arcos.get(0);
        arbolK.addEdge(arista);
        arcos.remove(0);
        while (arcos.size() != 0) {
            arista = arcos.get(0);
            arbolK = añadirArista(arbolK, arista);
            arcos.remove(0);
        }
        for (int h = 0; h < nodes.size(); h++) {
            setNodosContiguos(nodes.get(h), arbolK);
        }
        return arbolK;
    }
	
	private boolean existeCiclo(Edge arista, Grafo arbolK) {
		ArrayList<Edge> aristas = arbolK.edges;

		if (aristas.size() == 0) {
			return false;
		} else {
			Node nodo0 = arista.getSource();
			Node nodo1 = arista.getTarget();
			arbolK.reset();
			ArrayList<Node> nodosFamilia0 = new ArrayList<Node>();
			nodosFamilia0.addAll(buscarContiguos(nodo0, arbolK));
			for (Node a : nodosFamilia0) {
				if (a.getId() == nodo1.getId()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<Node> buscarContiguos(Node node, Grafo arbolk) {

		ArrayList<Node> nodosContiguos = new ArrayList<Node>();
		ArrayList<Edge> arcos = arbolk.edges;
		for (Edge e : arcos) {
			if (e.getSource().getId() == node.getId() && !e.getTarget().getVisitado()) {
				e.getTarget().setVisitado(true);
				nodosContiguos.add(e.getTarget());
				nodosContiguos.addAll(buscarContiguos(e.getTarget(), arbolk));
			}
			if (e.getTarget().getId() == node.getId() && !e.getSource().getVisitado()) {
				e.getSource().setVisitado(true);
				nodosContiguos.add(e.getSource());
				nodosContiguos.addAll(buscarContiguos(e.getSource(), arbolk));
			}

		}
		return nodosContiguos;
	}
	
	private Grafo añadirArista(Grafo arbolK, Edge arista) {
		if (!existeCiclo(arista, arbolK)) {
			arbolK.addEdge(arista);
		}
		return arbolK;
	}
	
	public Grafo Prim() {
		reset();
		Grafo grafo = new Grafo();
		grafo.setNombre(nombre);
		edges.get(0).getSource().setVisitado(true);
		grafo.nodes.add(edges.get(0).getSource());

		while (noAcabado()) {
			double minimo = 999999999;
			Edge e = null;
			for (Edge edge : edges) {
				if (edge.getLength() < minimo && esValidoEdge(edge)) {
					minimo = edge.getLength();
					e = edge;
				}
			}
			if (!(e == null)) {
				if (e.getSource().getVisitado() == false) {
					e.getSource().setVisitado(true);
					grafo.edges.add(e);
					grafo.nodes.add(e.getSource());
				}
				if (e.getTarget().getVisitado() == false) {
					e.getTarget().setVisitado(true);
					grafo.edges.add(e);
					grafo.nodes.add(e.getTarget());
				}
			}
		}
		return grafo;
	}
	
	private void reset() {
		for (Node n : nodes) {
			n.setVisitado(false);
		}
	}
	
	private boolean noAcabado() {
		for (Node n : nodes) {
			if (n.getVisitado() == false) {
				return true;
			}
		}
		return false;
	}
	
	private boolean esValidoEdge(Edge edge) {
		if (edge.getSource() == edge.getTarget())
			return false;
		if (edge.getSource().getVisitado() == false &&
		    edge.getTarget().getVisitado() == false) {
			return false;
		} else if (edge.getSource().getVisitado() && 
				   edge.getTarget().getVisitado()) {
			return false;
		}
		return true;
	}
	
	public void setNodosContiguos(Node node, Grafo arbolk) {
		ArrayList<Edge> arcos = arbolk.edges;
		for (Edge e : arcos) {
			if (e.getSource().getId() == node.getId()) {
				e.getSource().nodosAdyacentes.add(e.getTarget());
			}
			if (e.getTarget().getId() == node.getId()) {
				e.getTarget().nodosAdyacentes.add(e.getSource());
			}
		}
	}
	
	public double calcularMedia(ArrayList<Node> nodosHoja) {
		double sum = 0;
		for (Node n : nodosHoja) {
			sum += n.getAltura();
		}
		double media = sum / nodosHoja.size();
		return media;
	}
	
	public double calcularVar(double media, ArrayList<Node> nodos) {
		double var = 0;
		double sum = 0;
		for (Node n : nodos) {
			sum += Math.pow(media - n.getAltura(), 2);
		}
		var = sum / nodos.size();
		return var;
	}
	
	public ArrayList<Node> conseguirNodosHoja(Grafo arbol) {
		ArrayList<Node> nodosHoja = new ArrayList<Node>();
		for (Node n : arbol.nodes) {
			if (n.getArcosAdyacentes().size() == 0) {
				nodosHoja.add(n);
			}
		}
		return nodosHoja;
	}
	
	public boolean recorrerPreorden(Grafo arbol) {
		reiniciar(arbol);
		ArrayList<Edge> arcos = new ArrayList<Edge>();
		arcos.addAll(arbol.getEdges());
		Stack<Node> nodosFrontera = new Stack<Node>();
		raiz.setAltura(0);
		nodosFrontera.add(raiz);
		while (!arcos.isEmpty()) {
			Node cabeza = nodosFrontera.pop();
			for (int i = 0; i < arcos.size(); i++) {
				boolean borrar = false;
				if (arcos.get(i).getSource().getId() == cabeza.getId()) {
					arcos.get(i).getTarget().setAltura(1 + cabeza.getAltura());
					media += arcos.get(i).getTarget().getAltura();
					nodosFrontera.add(arcos.get(i).getTarget());
					arcos.get(i).getSource().addEdgeAdyacente(arcos.get(i));
					borrar = true;
				}
				if (arcos.get(i).getTarget().getId() == cabeza.getId()) {
					arcos.get(i).getSource().setAltura(1 + cabeza.getAltura());
					media += arcos.get(i).getSource().getAltura();
					nodosFrontera.add(arcos.get(i).getSource());
					arcos.get(i).getTarget().addEdgeAdyacente(arcos.get(i));
					borrar = true;
				}
				if (borrar) {
					arcos.remove(i);
					i--;
				}
			}
		}
		if (arcos.isEmpty()) {
			media = media / nodes.size();
			varianza = calcularVar(media, nodes);
			return true;
		} else
			return false;
	}
	
	public Node encontrarNodoRaizDiego(Grafo arbolK) {
													  
		int[][] matriz = new int[arbolK.nodes.size()][arbolK.nodes.size()];
		ArrayList<Integer> numeros = new ArrayList<Integer>();
		int aux = 0;
		for (int i = 0; i < arbolK.nodes.size(); i++) {
			for (int j = 0; j < arbolK.nodes.size(); j++) {
				if (arbolK.nodes.get(j).getNumNodosAdyacentes() == 1) {
					matriz[i][j] = 1;
				} else {
					matriz[i][j] = arbolK.getNodo(j).getNodosNivel(i, arbolK.getNodo(j));
					if (matriz[i][j] == 0) {
						numeros.add(j);
						aux = j;
						return arbolK.nodes.get(j);
					}
				}
			}
			if (!numeros.isEmpty()) {
				if (numeros.size() == 1) {
					return arbolK.getNodo(numeros.remove(0));
				}
				return arbolK.getNodo(sacarMenor(matriz, numeros, --aux));
			}
		}
		return null;

	}
	
	private int sacarMenor(int[][] matriz, ArrayList<Integer> numeros, int j) {
		int min = 9999999;
		int iAux = 0;
		for (int i : numeros) {
			if (matriz[j][i] < min) {
				min = matriz[0][i];
				iAux = i;
			}
		}
		return iAux; 
	}
	
	public void reiniciar(Grafo arbolK) {
		for (Node n : arbolK.nodes) {
			n.removeAllArcosAdyacentes();
		}
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public Node getNodo(int i) {
		return nodes.get(i);
	}
	
	public Edge getEdge(int i) {
		return edges.get(i);
	}
	
	public Node getRaiz() {
		return raiz;
	}
	
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	
	public double getMedia() {
		return media;
	}
	
	public double getVarianza() {
		return varianza;
	}
	
	public void setNombre(String n) {
		nombre = n;
	}
	
	public void addNodo(Node n) {
		nodes.add(n);
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
	}
	
	public void setRaiz(Node raiz) {
		this.raiz = raiz;
	}
	
	public String toString() { 
		String cadena = "";
		for (int i = 0; i < nodes.size(); i++)
			cadena += nodes.get(i).toString() + "\n";
		for (int i = 0; i < edges.size(); i++)
			cadena += edges.get(i).toString() + "\n";
		return cadena;
	}
}