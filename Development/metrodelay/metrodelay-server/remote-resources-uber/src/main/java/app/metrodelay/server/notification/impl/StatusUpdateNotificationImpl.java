/*
 */
 package app.metrodelay.server.notification.impl;
 import app.metrodelay.server.notification.StatusUpdateNotification;
 import java.io.InputStream;
 
 /**
 *
 * @author mvolejnik
 */
public class StatusUpdateNotificationImpl implements StatusUpdateNotification{
    
    private InputStream content;

    public StatusUpdateNotificationImpl(InputStream content) {
        this.content = content;
    }
    
    
     @Override
    public InputStream content() {
        return content;
    }
 }