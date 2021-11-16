package org.oceanops.api.id.exceptionmappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class NotAuthorizedMapper implements ExceptionMapper<javax.ws.rs.NotAuthorizedException> {
	private final Logger logger = LoggerFactory.getLogger(NotAuthorizedMapper.class);
    
	public Response toResponse(javax.ws.rs.NotAuthorizedException ex) {
		logger.trace(ex.getMessage());
		return Response.status(Status.UNAUTHORIZED).entity("{\"errorMessage\": \"" + ex.getMessage() + "\"}").type(MediaType.APPLICATION_JSON).build();
	}
}