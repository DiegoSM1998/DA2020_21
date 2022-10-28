public class Edge {
	double length; 
	Node source;
	Node target;
	long id;

	public Edge(double l, Node s, Node t, long Id) {
		length = l;
		source = s;
		target = t;
		id = Id;
	}
   
	public Node getSource() {
		return source;
	}

	public Node getTarget() {
		return target;
	}

	public double getLength() {
		return length;
	}
    
	public long getId() {
		return id;
	}
	
	public void setSource(Node source) {
		this.source = source;
	}
	
	public void setTarget(Node target) {
		this.target = target;
	}
    
	public void setLength(double length) {
		this.length = length;
	}
    
	public void setId(long id) {
		this.id = id;
	}
    
	public String toString() { 
		return "ID: " + id + ", longitud: " + length + ", desde: " + source + ", hasta: " + target+"\n"; 
	}

} 
