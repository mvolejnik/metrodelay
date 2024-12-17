package app.metrodelay.server.model;

import java.util.Iterator;

import jakarta.validation.constraints.NotNull;

public interface Identifiables<E extends Identifiable> extends Iterable<E> {

	public Identifiables<E> add(E identifiable);

	public Iterator<E> iterator();
	
	public String getName();
	
}