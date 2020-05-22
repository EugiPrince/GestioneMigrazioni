package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {
	//Modello -> qual e' lo stato del mondo di cui dobbiamo tener traccia
	private Graph<Country, DefaultEdge> grafo;//Ci serve il grafo per recuperare i vicini, dove possono spostarsi i migranti
	
	//Tipi di evento, che andranno inseriti nella coda prioritaria
	private PriorityQueue<Evento> queue;
	
	//Parametri della simulazione
	private int nMigranti = 1000;
	private Country partenza; 
	
	//Output
	private int T = -1;
	private Map<Country, Integer> stanziali;
	
	//Sarebbero i vari set dei parametri
	public void init(Country partenza, Graph<Country, DefaultEdge> grafo) {
		this.partenza = partenza;
		this.grafo = grafo;
		
		//Impostazione dello stato iniziale
		this.T = 1;
		this.stanziali = new HashMap<>(); //La inizializziamo mettendo tutti gli stati e mettendo stanziali = 0
		
		for(Country c : this.grafo.vertexSet())
			this.stanziali.put(c, 0);
		
		this.queue = new PriorityQueue<>();
		
		//Inserisco il primo evento, perche' gia' lo conosco
		this.queue.add(new Evento(T, partenza, nMigranti));
	}
	
	public void run() {
		//Finche' c'e' un evento nella coda, lo estraggo e lo eseguo
		Evento e;
		while((e = this.queue.poll()) !=null) {
			this.T = e.getT(); //Cosi' T verra' sovrascritto e l'ultima volta con l'ultimo T che vediamo nella sim
			//Eseguo l'evento e
			//processEvent(e);
			int nPersone = e.getN();
			Country stato = e.getStato();
			
			//Cerco i vicini di stato
			List<Country> vicini = Graphs.neighborListOf(this.grafo, stato);
			
			int migranti = (nPersone / 2) / vicini.size();//Di questa meta' si spostano in parti uguali tra i vicini
			//E' chiaro che se i vicini dello stato sono piu' delle persone che dovrebbero spostarsi, allora
			//diventano tutti stanziali
			if(migranti > 0) {
				//Le persone devono muoversi, in parti uguali tra i vari vicini
				for(Country c : vicini)
					this.queue.add(new Evento(e.getT()+1, c, migranti));
				
			}
			int stanziali = nPersone - (migranti * vicini.size()); //Se migranti e 0, diventano tutte stanziali
			
			//Per caso i migranti possono tornare indietro (era stato esplicitamente specificato) Allora prendiamo
			//dalla mappa stanziali il valore che c'era prima, e lo modifichiamo
			this.stanziali.put(stato, this.stanziali.get(stato) + stanziali);
			
		}
	}
	
	public Map<Country, Integer> getStanziali() {
		return this.stanziali;
	}
	
	public Integer getT() {
		return this.T;
	}
}
