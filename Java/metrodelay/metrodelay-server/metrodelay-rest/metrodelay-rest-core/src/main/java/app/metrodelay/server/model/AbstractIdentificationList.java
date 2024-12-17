package app.metrodelay.server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jakarta.validation.constraints.NotNull;

public abstract class AbstractIdentificationList<E extends Identifiable> implements Iterable<E>, Identifiables<E> {

	private List<E> identifiables = new ArrayList<>();
	
	public AbstractIdentificationList() {
		super();
	}

	/* (non-Javadoc)
	 * @see app.metrodelay.server.model.Identifiables#add(E)
	 */
	@Override
	public Identifiables<E> add(@NotNull E identifiable) {
		identifiables.add(identifiable);
		return this;
	}

	/* (non-Javadoc)
	 * @see app.metrodelay.server.model.Identifiables#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		synchronized (this) {
			Collections.sort(identifiables);
			return identifiables.iterator();
		}
	}

}