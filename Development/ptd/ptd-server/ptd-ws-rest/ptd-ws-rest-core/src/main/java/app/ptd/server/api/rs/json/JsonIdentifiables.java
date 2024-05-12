package app.ptd.server.api.rs.json;

import java.io.OutputStream;

import jakarta.json.Json;
import jakarta.json.stream.JsonGenerator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import app.ptd.server.model.AbstractIdentification;
import app.ptd.server.model.Cities;
import app.ptd.server.model.Identifiable;
import app.ptd.server.model.Identifiables;

public class JsonIdentifiables {

	private static final Logger l = LogManager.getLogger(JsonIdentifiables.class);
	
	private static final String CITY_CODE = "code" ;
	private static final String CITY_NAME = "name" ;
	private static final String CITY_LOCAL_NAME = "localName" ;
	
	public void identifiables(OutputStream citiesOs, Identifiables<? extends Identifiable> objects){
		l.debug("identifiables::");
		JsonGenerator gen = Json.createGenerator(citiesOs);	
		gen.writeStartObject(); // {
		gen.writeStartArray(objects.getName()); // {
		for (Identifiable i : objects){
			l.trace("identifiables:: Writing %s [%s] to JSON stream.", objects.getName(), i);
			gen.writeStartObject();
			gen.write(CITY_CODE, i.getCode());
			gen.write(CITY_NAME, i.getName());
			if (i.getLocalName() != null){//TODO stringtuils
			  gen.write(CITY_LOCAL_NAME, i.getLocalName());
			}
			gen.writeEnd();
		}
		gen.writeEnd();
		gen.writeEnd();
		gen.close();
	}

}
