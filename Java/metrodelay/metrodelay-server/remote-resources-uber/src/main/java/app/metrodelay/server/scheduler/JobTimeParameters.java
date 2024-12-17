/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.metrodelay.server.scheduler;

import java.time.Duration;
import java.util.Objects;

/**
 *
 * @author mvolejnik
 */
public class JobTimeParameters {
    
    private final Duration startDelay;
    private final Duration interval;
    private final Duration maxOffset;

    public JobTimeParameters(Duration startDelay, Duration interval, Duration maxShift) {
        Objects.nonNull(startDelay);
        Objects.nonNull(interval);
        Objects.nonNull(maxShift);
        this.startDelay = startDelay;
        this.interval = interval;
        this.maxOffset = maxShift;
    }

    public Duration startDelay() {
        return startDelay;
    }

    public Duration interval() {
        return interval;
    }

    public Duration maxOffset() {
        return maxOffset;
    }
    
}
