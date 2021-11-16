package org.oceanops.api.id.exceptionmappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {
	private final Logger logger = LoggerFactory.getLogger(ErrorMapper.class);

	public Response toResponse(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity("{\"errorMessage\": \"Error while retrieving data\"}").type(MediaType.APPLICATION_JSON).build();
	}

}
