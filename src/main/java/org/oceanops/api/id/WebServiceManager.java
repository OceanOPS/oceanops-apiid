package org.oceanops.api.id;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.oceanops.api.Authentication;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.NotAuthorizedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.oceanops.api.exceptions.MissingMetadataException;
import org.oceanops.orm.NcLevel;
import org.oceanops.orm.NcNotification;
import org.oceanops.orm.NcTopic;
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
		
		// Creating notification
		if(response.size() > 0){
			try{
				ObjectContext dataContext = Utils.getCayenneContext();
				NcNotification notif = dataContext.newObject(NcNotification.class);
				NcTopic ncTopic = SelectById.query(NcTopic.class, 701).selectOne(dataContext);
				notif.setNcTopic(ncTopic);
				notif.setContact(Authentication.getContact());
				NcLevel ncLevel = SelectById.query(NcLevel.class, 20).selectOne(dataContext);
				notif.setNcLevel(ncLevel);
				notif.setIsPrivate(1);
				notif.setNotificationDate(LocalDateTime.now());
				notif.setName("[INFO] New ID allocation");
				String description = "User ";
				if(Authentication.getContact() != null)
					description += Authentication.getContact().getLogin();
				else{
					// SHOULD NOT HAPPEN
					description += "!!! UNKNOWN !!!";
					logger.warn("Request made by unknown user");
				}
				
				String listIds = "";
				int nbIds = 0;
				for (IdResponse idResponse : response) {
					if(idResponse.getRef() != null){
						if(listIds.length() > 0){
							listIds += ", ";
						}
						nbIds++;
						listIds += idResponse.getRef();
					}
				}
				description += " has requested " + nbIds + " IDs: ";
				description += listIds;
				notif.setDescription(description);
				if(nbIds > 0)
					dataContext.commitChanges();
				else
					dataContext.rollbackChanges();
			}
			catch(Exception e){
				logger.error("Impossible to create notification: " + e.getMessage());
			}
		}
		return response;
	}
}
