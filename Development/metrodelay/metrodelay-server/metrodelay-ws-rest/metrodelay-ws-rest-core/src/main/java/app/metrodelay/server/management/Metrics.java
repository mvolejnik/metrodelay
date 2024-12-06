package app.metrodelay.server.management;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

public class Metrics {

	public static final MetricRegistry REGISTRY = new MetricRegistry();
	
	private static final ConsoleReporter reporter = ConsoleReporter
			.forRegistry(Metrics.REGISTRY)
			.convertRatesTo(TimeUnit.SECONDS)
			.convertDurationsTo(TimeUnit.MILLISECONDS)
			.build();

	static {
		reporter.start(1, TimeUnit.MINUTES);
	}
	
	public static enum MetricsNames{
		METRICS_TRANSPORT_CITIES("cities"),
		METRICS_TRANSPORT_LINES("lines");
		
		private final String name;

		private MetricsNames(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}

}
