package app.ptd.server;

import app.ptd.server.scheduler.QuartzInit;
import java.time.Duration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

/**
 *
 * @author mvolejnik
 */
public class App {
  
  private static final String JOB_INTERVAL = "interval";
  private static final String JOB_INTERVAL_DELAY = "intervaldelay";
  private static final String JOB_INTERVAL_RANDOM = "intervalrandom";
  private static final String OPERATORS = "operators";
  private static final String REGISTRY_MULTICAST_IP = "multicastip";
  private static final String REGISTRY_MULGTICAST_PORT = "multicastport";
  private static final String SERVICE_STATUS_UPDATE = "serviceStatusUpdate";  
  private static final String OPERATORS_DEFAULT = """
                                                  cz.prg.dpp=https://www.dpp.cz/rss/cz/mimoradne-udalosti.xml;
                                                  cz.prg.pid=https://pid.cz/feed/rss-mimoradnosti/;""";
  
  private static Options options(){
        var options = new Options();
        options.addOption("i", JOB_INTERVAL, true, "scheduler job interval");
        options.addOption("d", JOB_INTERVAL_DELAY, true, "scheduler job interval delay");
        options.addOption("r", JOB_INTERVAL_RANDOM, true, "scheduler job interval random");
        options.addOption("ma", REGISTRY_MULTICAST_IP, true, "registry service ip address");
        options.addOption("mp", REGISTRY_MULGTICAST_PORT, true, "registry serivce multicast port");
        options.addOption("s", SERVICE_STATUS_UPDATE, true, "status update service URN");
        options.addOption("o", OPERATORS, true, "operators map as operatorid=url");
        return options;
    }
  
  public static void main(String[] args) throws Exception
    {
        CommandLine line = new DefaultParser().parse( options(), args );
        try (QuartzInit scheduler = new QuartzInit(
            Duration.parse(line.getOptionValue(JOB_INTERVAL, "PT2S")),
            Duration.parse(line.getOptionValue(JOB_INTERVAL_DELAY, "PT1M")),
            Duration.parse(line.getOptionValue(JOB_INTERVAL_RANDOM, "PT1M")),
            line.getOptionValue(OPERATORS, OPERATORS_DEFAULT))) {
          while (true){
            Thread.sleep(100);
          }
        }
    }
  
}
