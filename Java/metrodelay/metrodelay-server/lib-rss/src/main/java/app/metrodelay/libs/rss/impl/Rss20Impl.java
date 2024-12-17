package app.metrodelay.libs.rss.impl;

import java.io.InputStream;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import app.metrodelay.libs.rss.RssException;
import app.metrodelay.libs.rss.jaxb.rss20.Rss;

public class Rss20Impl implements app.metrodelay.libs.rss.Rss{
	
	private Rss rss;

	public Rss20Impl(InputStream rssInputStream) throws RssException{
		try {
	        JAXBContext ctx =  JAXBContext.newInstance(Rss.class);
	        Unmarshaller mrs = ctx.createUnmarshaller();
	        rss = (Rss) mrs.unmarshal(rssInputStream);
	        
        } catch (JAXBException e) {
	        throw new RssException(e);
        }
	}

	@Override
	public Rss getRss() {
		return rss;
	}
	
}
