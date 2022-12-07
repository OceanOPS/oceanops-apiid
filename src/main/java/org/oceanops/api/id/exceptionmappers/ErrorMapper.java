package org.oceanops.api.id.exceptionmappers;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

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
