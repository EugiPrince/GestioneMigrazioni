package it.polito.tdp.borders.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.borders.db.BordersDAO;

public class Model {
	
	private Graph<Country, DefaultEdge> graph ;
	private Map<Integer,Country> countriesMap ;
	private Simulatore sim;
	
	public Model() {
		this.countriesMap = new HashMap<>() ;
		this.sim = new Simulatore();
	}
	
	public void creaGrafo(int anno) {
		
		this.graph = new SimpleGraph<>(DefaultEdge.class) ;

		BordersDAO dao = new BordersDAO() ;
		
		//vertici
		dao.getCountriesFromYear(anno,this.countriesMap) ;
		Graphs.addAllVertices(graph, this.countriesMap.values()) ;
		
		// archi
		List<Adiacenza> archi = dao.getCoppieAdiacenti(anno) ;
		for( Adiacenza c: archi) {
			graph.addEdge(this.countriesMap.get(c.getState1no()), 
					this.countriesMap.get(c.getState2no())) ;
			
		}
	}
	
	public List<Country> getCountries() {
		List<Country> res = new ArrayList<>();
		res.addAll(this.graph.vertexSet());
		Collections.sort(res);
		return res;
	}
	
	public List<CountryAndNumber> getCountryAndNumber() {
		List<CountryAndNumber> list = new ArrayList<>() ;
		
		for(Country c: graph.vertexSet()) {
			list.add(new CountryAndNumber(c, graph.degreeOf(c))) ;
		}
		Collections.sort(list);
		return list ;
	}
	
	public void simula(Country partenza) {
		if(this.graph != null) {
			this.sim.init(partenza, this.graph);
			this.sim.run();
		}
	}

	public Integer getT() {
		return this.sim.getT();
	}
	
	public List<CountryAndNumber> getStanzialiOrdinata() {
		Map<Country, Integer> stanziali = sim.getStanziali();
		List<CountryAndNumber> res = new ArrayList<>();
		
		for(Country c : stanziali.keySet())
			res.add(new CountryAndNumber(c, stanziali.get(c)));
		
		Collections.sort(res);
		return res;
	}
}
