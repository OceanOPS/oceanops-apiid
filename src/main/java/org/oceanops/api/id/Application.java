package org.oceanops.api.id;

import org.apache.cayenne.configuration.server.ServerRuntime;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application extends ResourceConfig  {
	Logger logger = LoggerFactory.getLogger(Application.class);
	public static ServerRuntime CAYENNE_RUNTIME;
	
	public Application() {		
		packages("org.oceanops.api.id");
	}

}
