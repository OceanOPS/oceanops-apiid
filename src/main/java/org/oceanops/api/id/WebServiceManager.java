package org.oceanops.api.id;

import org.oceanops.api.Authentication;

import javax.ws.rs.POST;
import javax.ws.rs.NotAuthorizedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.oceanops.api.exceptions.MissingMetadataException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class WebServiceManager {
	private final Logger logger = LoggerFactory.getLogger(WebServiceManager.class);	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("getid")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<IdResponse> getID(final List<IdInput> inputs) throws Exception {
		ArrayList<IdResponse> response = null;

		if(Authentication.isAuthenticated() == true){
			response = new ArrayList<IdResponse>();
			String batchRequestRefDate =  org.oceanops.api.Utils.GetIsoDate(LocalDateTime.now());
			for (IdInput input : inputs) {
				IdResponse curResponse;
				try{
					IdGenerator gen = new IdGenerator(input, batchRequestRefDate);
					curResponse = new IdResponse(input, true, gen.getMessage(), gen.getWigosRef(), gen.getRef(), gen.getGtsId(), gen.getBatchRef(), gen.getModelName());
				}
				catch(ClientErrorException e){
					// Exception following wrong input
					curResponse = new IdResponse(input, false, e.getMessage(), null, null, null, null, null);
					logger.info(e.getMessage());
				}
				catch(MissingMetadataException e){
					// Exception following inconsistent/missing metadata
					curResponse = new IdResponse(input, false, "Impossible to allocate an identifier, please contact OceanOPS", null, null, null, null, null);
					logger.error(e.getMessage(), e);
				}
				catch(Exception e){
					// Other exception, like database related ones
					curResponse = new IdResponse(input, false, "Unexpected error while allocating an identifier, please contact OceanOPS", null, null, null, null, null);
					logger.error(e.getMessage(), e);
				}
				
				response.add(curResponse);
			}
		}
		else{
			throw new NotAuthorizedException("Authentication required", Response.status(Status.UNAUTHORIZED));
		}
		
		return response;
	}
}
