package com.bpbonline.serverfiles;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures JAX-RS for the application.
 * @author Juneau
 */
@ApplicationPath("resources")
public class JAXRSConfiguration extends Application {
      @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(com.bpbonline.serverfiles.resources.JavaEE8Resource.class);
        resources.add(com.bpbonline.serverfiles.resources.FileService.class);
        resources.add(com.bpbonline.serverfiles.resources.ZipService.class);
       
        
       
        return resources;
    }
}
