import java.util.ArrayList;

public class Node {
	long id; 
	double x; 
	double y; 
	boolean visitado = false;
	ArrayList<Edge> arcosAdyacentes = null;
	ArrayList<Node> nodosAdyacentes = null;
	int altura;
    
	public Node(long ID, double X, double Y) {
		id = ID;
		x = X;
		y = Y;
		arcosAdyacentes = new ArrayList<Edge>();
		nodosAdyacentes = new ArrayList<Node>();
		altura = 0;
	}
    
	public Node(long ID, double X, double Y, boolean v) {
		id = ID;
		x = X;
		y = Y;
		visitado = v;
		arcosAdyacentes = new ArrayList<Edge>();
		nodosAdyacentes = new ArrayList<Node>();
		altura = 0;
	}
    
	public Node() {
	}
    
	public long getId() {
		return id;
	}
    
	public double getX() {
		return x;
	}
    
	public double getY() {
		return y;
	}
    
	public boolean getVisitado() {
		return visitado;
	}
	
	public int getAltura() {
		return altura;
	}
    
	public ArrayList<Node> getNodosAdyacentes() {
		return nodosAdyacentes;
	}
    
	public ArrayList<Edge> getArcosAdyacentes() {
		return arcosAdyacentes;
	}
    
	public int getNumNodosAdyacentes() {
		return nodosAdyacentes.size();
	}
    
	public void setId(long ID) {
		id = ID;
	}
    
	public void setX(double X) {
		x = X;
	}
    
	public void setY(double Y) {
		y = Y;
	}
    
	public void setVisitado(boolean vis) {
		visitado = vis;
	}
    
	public void setAltura(int altura) {
		this.altura = altura;
	}
    
	public void addEdgeAdyacente(Edge e) {
		arcosAdyacentes.add(e);
	}
    
	public void addNodeAdyacente(Node n) {
		nodosAdyacentes.add(n);
	}
    
	public void setArcosAdyacentes(ArrayList<Edge> arcosAdyacentes) {
		this.arcosAdyacentes = arcosAdyacentes;
	}
    
	public int getNodosNivel(int Nivel, Node my) {

		ArrayList<Node> nDirectos = nodosAdyacentes;
		ArrayList<Node> nodosVisitados = new ArrayList<Node>();
		nodosVisitados.add(my);
		while (Nivel > 0) {
			ArrayList<Node> listaAux = new ArrayList<Node>();
			for (Node n : nDirectos) {
				if (!nodosVisitados.contains(n)) {
					listaAux.addAll(n.getNodosAdyacentes());
					listaAux.removeAll(nodosVisitados);
					nodosVisitados.add(n);
				} 
			}
			nDirectos = listaAux;
			Nivel--;
		}
		return nDirectos.size();
	}
    
	public void removeAllArcosAdyacentes() {
		arcosAdyacentes.removeAll(arcosAdyacentes);
	}
    
	public String toString() {
		return "ID: " + id + ", X: " + x + ", Y: " + y + ".";
	}

}
