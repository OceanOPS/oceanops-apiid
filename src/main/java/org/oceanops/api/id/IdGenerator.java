package org.oceanops.api.id;

import java.sql.SQLException;
import java.time.LocalDateTime;

import javax.naming.NamingException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response.Status;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SQLSelect;
import org.apache.cayenne.query.SelectById;
import org.oceanops.api.Authorization;
import org.oceanops.api.exceptions.MissingMetadataException;
import org.oceanops.orm.Agency;
import org.oceanops.orm.NetworkPtf;
import org.oceanops.orm.Program;
import org.oceanops.orm.ProgramAgency;
import org.oceanops.orm.Ptf;
import org.oceanops.orm.PtfBatchStatus;
import org.oceanops.orm.PtfDeployment;
import org.oceanops.orm.PtfHardware;
import org.oceanops.orm.PtfIdentifiers;
import org.oceanops.orm.PtfModel;
import org.oceanops.orm.PtfStatus;
import org.oceanops.orm.Wmo;

public class IdGenerator {
	private ObjectContext context;
	
	private IdInput input;
	private PtfModel model;
	private Program program;
	private PtfBatchStatus batchStatus;
	private String requestedType;
	private String wigosRef;
	private String ref;
	private String gtsId;
	private StringBuilder returnMessage;
	private String batchRefDate;
	private String batchRef;
	
	/**
	 * Calls the database function generating a random SOT ID, locking it for further submission
	 * @return The generated ID
	 * @throws SQLException
	 */
	private String getShipBasedID() throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.GET_ship_based_wmo_id() from dual", String.class).selectOne(context);
		
		return result;
	}

	/**
	 * Calls the database function generating a random Argo ID, locking it for further submission
	 * @return The generated ID
	 * @throws SQLException
	 */
	private String getProfilingFloatID() throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.GET_profiling_float_wmo_id() from dual", String.class).selectOne(context);
		
		return result;
	}

	/**
	 * Calls the database function generating a random fixed system ID, locking it for further submission
	 * @return The generated ID
	 * @throws SQLException
	 */
	private String getFixedSystemID(Double lon, Double lat) throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.get_fixed_system_wmo_id(#bind($lon), #bind($lat)) from dual", String.class)
		.param("lon", lon)
		.param("lat", lat).selectOne(context);
		
		return result;
	}

	/**
	 * Calls the database function generating a random surface drifter ID, locking it for further submission
	 * @return The generated ID
	 * @throws SQLException
	 */
	private String getSurfaceDrifterID() throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.get_surface_drifter_wmo_id() from dual", String.class).selectOne(context);
		
		return result;
	}

	/**
	 * Calls the database function generating a random sub surface auto system ID, locking it for further submission
	 * @return The generated ID
	 * @throws SQLException
	 */
	private String getSubSurfaceAutoID() throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.GET_sub_surface_auto_wmo_id() from dual", String.class).selectOne(context);
		
		return result;
	}

	/**
	 * Calls the database function generating a random marine animal system ID, locking it for further submission
	 * @return The generated ID
	 * @throws SQLException
	 */
	private String getMarineAnimalID() throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.GET_marine_animal_wmo_id() from dual", String.class).selectOne(context);
		
		return result;
	}
	/**
	 * Calls the database function generating a random marine animal system ID, locking it for further submission
	 * @return The generated ID
	 * @throws SQLException
	 */
	private String getSurfaceAutoOtherID() throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.GET_surface_auto_other_wmo_id() from dual", String.class).selectOne(context);
		
		return result;
	}
	

	/**
	 * Based on the reference, computes a WIGOS identifier using the same increment as the reference
	 * @param ref The provided OceanOPS reference
	 * @return The computed WIGOS identifier
	 */
	private String getWIGOSRef(String ref) throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.get_wigosid_from_ref(#bind($ref)) from dual", String.class)
			.param("ref", ref)
			.selectOne(context);
		
		return result;
	}
	/**
	 * Based on the WMO ID, computes a reference
	 * @param wmoId The provided WMO ID
	 * @return The computed reference
	 */
	private String getRefFromWmoId(String wmoId) throws SQLException {
		String result = SQLSelect.scalarQuery("SELECT id_mgmt.get_ref_from_wmo(#bind($wmoId)) from dual", String.class)
			.param("wmoId", wmoId)
			.selectOne(context);
		
		return result;
	}


	/**
	 * Accessor to the name of the platform model
	 * @return the platform model name
	 */
	public String getModelName(){
		return (this.model == null) ? null : this.model.getName(); 
	}
	
	
	/**
	 * Acccessor
	 * @return
	 */
	public String getWigosRef() {
		return wigosRef;
	}

	/**
	 * Acccessor
	 * @return
	 */
	public String getBatchRef() {
		return batchRef;
	}

	/**
	 * Acccessor
	 * @return
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * Acccessor
	 * @return
	 */
	public String getGtsId() {
		return gtsId;
	}

	public String getMessage(){
		return this.returnMessage.toString();
	}

	/**
	 * Main constructor, parses, checks the inputs, and generates the identifers.
	 * @param input The input parameters, stored in a {@link IdInput} object.
	 * @param batchRefDate A string representing the date associated to this request. 
	 * @throws SQLException
	 * @throws NamingException
	 * @throws BadRequestException
	 */
	public IdGenerator(IdInput input, String batchRefDate) throws SQLException, NamingException, ClientErrorException, MissingMetadataException {	
		this.context = Utils.getCayenneContext();
		this.input = input;
		this.gtsId = null;
		this.wigosRef = null;
		this.ref = null;
		this.requestedType = null;
		this.batchRefDate = batchRefDate;
		this.returnMessage = new StringBuilder();
		
		// Parsing input
		this.parseInput();

		// Checking program provided
		if(this.program == null){
			throw new ClientErrorException("Invalid or missing program", Status.BAD_REQUEST);
		}
		else{
			// Checking authorization
			// Context needed for stability
			if(!Authorization.hasEditRightForProgram((Integer)this.program.getObjectId().getIdSnapshot().get("ID"), this.context))
				throw new ClientErrorException("Not authorized for the provided program", Status.UNAUTHORIZED);
		}

		// Checking start date
		if(this.input.getStartDate() == null){
			throw new ClientErrorException("Invalid or missing start/deployment date", Status.BAD_REQUEST);
		}
		
		// Checking WMO duplicate if provided
		if(this.input.getGtsId() != null){
			if(this.isGTSIDADuplicate()){
				throw new ClientErrorException("Provided GTS identifier is already allocated on the date range specified", Status.BAD_REQUEST);
			}
		}

		// Getting WMO ID type
		if(this.model == null){
			this.model = this.program.getDefaultPtfModel();
			if(returnMessage.length() > 0 )
				returnMessage.append(";");
			returnMessage.append("Model not provided or invalid, taking default program model.");
		}
		
		if(model == null)
			throw new MissingMetadataException("No platform model found: model = " + input.getModel() + " ; program = " + input.getProgram());
		else{
			if(model.getApiWmoIdType() == null)
				throw new MissingMetadataException("No WMO ID type for model = " + input.getModel());
			else{
				this.requestedType = model.getApiWmoIdType().getCode();
			}
		}

		// Generates the identifiers
		this.generate();
		
		// Creating platform records
		this.createPtfRecord();
	}

	/**
	 * Based on provided parameters to the constructor, generate all the requested identifiers
	 * @throws Exception
	 */
	private void generate() throws ClientErrorException, SQLException{
		switch (this.requestedType) {
		case "profiling_float":
			if(this.input.getGtsId() != null){
				if(this.input.getGtsId().length() == 7 && Utils.isInteger(this.input.getGtsId())){
					this.gtsId = this.input.getGtsId();
					this.ref = this.getRefFromWmoId(this.gtsId);
				}
				else
					throw new ClientErrorException("Invalid GTS ID (WMO): must be a 7-digit numerical string", Status.BAD_REQUEST);
			}
			else{
				String id = this.getProfilingFloatID();
				if(id.equals("-1") || id.startsWith("TMP"))
					throw new InternalServerErrorException("Impossible to get a unique identifier");
				else {
					this.gtsId = id;
					this.ref = this.gtsId;
				}
			}

			break;
		case "ship_based":
			if(this.input.getGtsId() != null)
				if(this.input.getGtsId().length() == 7){
					this.gtsId = this.input.getGtsId();					
					this.ref = this.getRefFromWmoId(this.gtsId);
				}
				else
					throw new ClientErrorException("Invalid GTS ID (WMO): must be a 7-character/digit string", Status.BAD_REQUEST);
			else{
				String id = this.getShipBasedID();
				if(id.equals("-1"))
					throw new InternalServerErrorException("Impossible to get a unique identifier");
				else {
					this.gtsId = id;
					this.ref = this.gtsId;
				}
			}
			break;
		case "fixed_system":
			if(this.input.getGtsId() == null){
				// Fixed systems require coordinates to generate an ID
				if(this.input.getLongitude() == null || this.input.getLatitude() == null)
					throw new ClientErrorException("Coordinates are mandatory for fixed observing systems", Status.BAD_REQUEST);
				else{
					String id = this.getFixedSystemID(this.input.getLongitude(), this.input.getLatitude());
					if(id.equals("-1"))
						throw new InternalServerErrorException("Impossible to get a unique identifier");
					else {
						this.gtsId = id;
						this.ref = this.gtsId;
					}
				}
			}
			else {
				if(this.input.getGtsId().length() == 7 && Utils.isInteger(this.input.getGtsId())){
					this.gtsId = this.input.getGtsId();					
					this.ref = this.getRefFromWmoId(this.gtsId);
				}
				else
					throw new ClientErrorException("Invalid GTS ID (WMO): must be a 7-digit numerical string", Status.BAD_REQUEST);
			}
			break;
		case "surface_drifter":
			if(this.input.getGtsId() == null){
				String id = this.getSurfaceDrifterID();
				if(id.equals("-1"))
					throw new InternalServerErrorException("Impossible to get a unique identifier");
				else {
					this.gtsId = id;
					this.ref = this.gtsId;
				}
			}
			else {
				if(this.input.getGtsId().length() == 7 && Utils.isInteger(this.input.getGtsId())){
					this.gtsId = this.input.getGtsId();					
					this.ref = this.getRefFromWmoId(this.gtsId);
				}
				else
					throw new ClientErrorException("Invalid GTS ID (WMO): must be a 7-digit numerical string", Status.BAD_REQUEST);
			}
			break;
		case "sub_surface_auto":
			if(this.input.getGtsId() == null){
				String id = this.getSubSurfaceAutoID();
				if(id.equals("-1"))
					throw new InternalServerErrorException("Impossible to get a unique identifier");
				else {
					this.gtsId = id;
					this.ref = this.gtsId;
				}
			}
			else {
				if(this.input.getGtsId().length() == 7 && Utils.isInteger(this.input.getGtsId())){
					this.gtsId = this.input.getGtsId();
					this.ref = this.getRefFromWmoId(this.gtsId);
				}
				else
					throw new ClientErrorException("Invalid GTS ID (WMO): must be a 7-digit numerical string", Status.BAD_REQUEST);
			}
			break;
		case "marine_animal":
			if(this.input.getGtsId() == null){
				String id = this.getMarineAnimalID();
				if(id.equals("-1"))
					throw new InternalServerErrorException("Impossible to get a unique identifier");
				else {
					this.gtsId = id;
					this.ref = this.gtsId;
				}
			}
			else {
				if(this.input.getGtsId().length() == 7 && Utils.isInteger(this.input.getGtsId())){
					this.gtsId = this.input.getGtsId();
					this.ref = this.getRefFromWmoId(this.gtsId);
				}
				else
					throw new ClientErrorException("Invalid GTS ID (WMO): must be a 7-digit numerical string", Status.BAD_REQUEST);
			}
			break;
		case "surface_auto_other":
			if(this.input.getGtsId() == null){
				String id = this.getSurfaceAutoOtherID();
				if(id.equals("-1"))
					throw new InternalServerErrorException("Impossible to get a unique identifier");
				else {
					this.gtsId = id;
					this.ref = this.gtsId;
				}
			}
			else {
				if(this.input.getGtsId().length() == 7 && Utils.isInteger(this.input.getGtsId())){
					this.gtsId = this.input.getGtsId();
					this.ref = this.getRefFromWmoId(this.gtsId);
				}
				else
					throw new ClientErrorException("Invalid GTS ID (WMO): must be a 7-digit numerical string", Status.BAD_REQUEST);
			}
			break;
		default:
			throw new ClientErrorException("WMO ID type not supported: " + this.requestedType, Status.BAD_REQUEST);
		}
		if(this.ref != null){
			// Allocating a WIGOS REF only for automated GTS ID
			if(this.input.getGtsId() == null && !this.ref.startsWith("TMP"))
				this.wigosRef = this.getWIGOSRef(this.ref);
		}
	}

	/**
	 * Parses the inputs and fetch the respecxtive entities.
	 */
	private void parseInput(){
		if(this.input.getProgram() != null)
			this.program = ObjectSelect.query(Program.class).where(Program.NAME.eq(this.input.getProgram())).selectOne(this.context);
		
		if(this.input.getModel() != null)
			this.model = ObjectSelect.query(PtfModel.class).where(PtfModel.NAME.eq(this.input.getModel())).selectOne(this.context);
		
		if(this.input.getBatchStatus() != null)
			this.batchStatus = ObjectSelect.query(PtfBatchStatus.class).where(PtfBatchStatus.NAME.eq(this.input.getBatchStatus())).selectOne(this.context);
	}

	/**
	 * Creates entities in the database
	 */
	private void createPtfRecord(){
		// Resetting context for stability of insert
		ObjectContext dataContext = BaseContext.getThreadObjectContext();
		try{
			Program localProgram = dataContext.localObject(this.program);
			localProgram.setObjectContext(dataContext);
			PtfModel localModel = dataContext.localObject(this.model);
			localModel.setObjectContext(dataContext);
			PtfBatchStatus localBatchStatus = null;
			if(this.batchStatus != null){
				localBatchStatus = dataContext.localObject(this.batchStatus);
				localBatchStatus.setObjectContext(dataContext);
			}
			Integer[] monitoringCriteria = this.determineCriteriaByNetwork(localProgram, localModel);

			Double lat = this.input.getLatitude();
			Double lon = this.input.getLongitude();
			LocalDateTime deplDate = this.input.getStartDate();
			if(lat == null || lon == null){
				// Taking default agency location
				Agency leadAgency = null;
				for(ProgramAgency pa : this.program.getProgramAgencies()){
					if(pa.getLead().intValue() == 1)
						leadAgency = pa.getAgency();
				}
				if(leadAgency != null){
					lat = leadAgency.getLat();
					lon = leadAgency.getLon();
				}
				else{
					lat = 46.2044;
					lon = 6.1432;
				}
			}

			this.batchRef = this.batchRefDate + "-" + this.model.getName();
			
			PtfDeployment depl = dataContext.newObject(PtfDeployment.class);
			depl.setLat(lat);
			depl.setLon(lon);
			depl.setDeplDate(deplDate);

			PtfIdentifiers pid = dataContext.newObject(PtfIdentifiers.class);
			pid.setWigosRef(this.wigosRef);
			pid.setInternalRef(this.input.getInternalId());

			Ptf ptf = dataContext.newObject(Ptf.class);
			ptf.setRef(this.ref);
			ptf.setBatchRequestRef(this.batchRef);
			ptf.setProgram(localProgram);
			ptf.setPtfModel(localModel);
			ptf.setPtfBatchStatus(localBatchStatus);
			ptf.setPtfIdentifiers(pid);
			ptf.setPtfDepl(depl);
			ptf.setDescription("CREATED BY API ID");
			ptf.setActivityCriterion(monitoringCriteria[0]);
			ptf.setClosureCriterion(monitoringCriteria[1]);

			if(this.input.getSerialNo() != null){
				PtfHardware ph = dataContext.newObject(PtfHardware.class);
				ph.setSerialRef(this.input.getSerialNo());
				ptf.setPtfHardware(ph);
			}

			PtfStatus ps = SelectById.query(PtfStatus.class, 0).selectOne(dataContext);
			ptf.setPtfStatus(ps);

			NetworkPtf np = dataContext.newObject(NetworkPtf.class);
			np.setPtf(ptf);
			np.setNetwork(ptf.getProgram().getNetwork());

			Wmo wmo = dataContext.newObject(Wmo.class);
			wmo.setPtf(ptf);
			wmo.setWmo(this.gtsId);
			wmo.setStartDate(deplDate);

			dataContext.commitChanges();
		}
		catch(Exception e){
			dataContext.rollbackChanges();
			throw e;
		}
	}

	/**
	 * Checks if the provided GTS identifier is a duplicate by checking the time range associated with the request
	 * @return true if a duplicate is found, false otherwise
	 */
	private boolean isGTSIDADuplicate() {
		boolean wmoIsValid = false;
        if(this.input.getGtsId() != null){
            if(this.program.getNetwork().getId().intValue() == 1000622){
                wmoIsValid = true;
            }
            else {		
                String sql = null;
                sql = "select 1 from wmo where wmo = #bind($wmo) AND util.dates_overlap(start_date, end_date, #bind($startDate), #bind($endDate)) = 1 and rownum = 1";
				Integer result = SQLSelect.scalarQuery(sql, Integer.class)
					.param("wmo", this.input.getGtsId())
					.param("startDate", this.input.getStartDate())
					.param("endDate", this.input.getEndDate()).selectOne(context);

				if(result != null){
					wmoIsValid = false;
				}
				else{
					wmoIsValid = true;
				}
            }
        }
        else 
            wmoIsValid = true;
		return !wmoIsValid;
	}

	/**
	 * Determines, based on a program and model, the monitoring criteria to save foe the newly created platform.
	 * @param program the program to which this platform belongs to.
	 * @param model the model specified for this platform.
	 * @return an array of two Integer, the firstone being the atcivity criterion, the second one the closure criterion
	 */
	private Integer[] determineCriteriaByNetwork(Program program, PtfModel model) {
    	Integer activityCriterion = null, closureCriterion = null;
		String actConstantName, closConstantName;	
		String networkName = program.getNetwork().getNameShort().toUpperCase();	
		if(networkName.equals("GO-SHIP") || networkName.equals("GOSHIP")){
			actConstantName = "COMMONS.ACTIVITY_CRITERION_GOSHIP";
			closConstantName = "COMMONS.CLOSURE_CRITERION_GOSHIP";
		}
		else if(networkName.equals("OCEANGLIDERS") || networkName.equals("OCEANGLIDER") || networkName.equals("GLIDERS")){
			actConstantName = "COMMONS.ACTIVITY_CRITERION_GLIDERS";
			closConstantName = "COMMONS.CLOSURE_CRITERION_GLIDERS";
		}
		else if(networkName.equals("ARGO")){
			actConstantName = "COMMONS.ACTIVITY_CRITERION_ARGO";
			closConstantName = "COMMONS.CLOSURE_CRITERION_ARGO";
		}
		else if(networkName.equals("DBCP")){
			if(model.getPtfType().getPtfFamily().getId() == 3 || model.getPtfType().getPtfFamily().getId() == 3){
				actConstantName = "COMMONS.ACTIVITY_CRITERION_DBCP_MB";
				closConstantName = "COMMONS.CLOSURE_CRITERION_DBCP_MB";
			}
			else{
				actConstantName = "COMMONS.ACTIVITY_CRITERION_DBCP";
				closConstantName = "COMMONS.CLOSURE_CRITERION_DBCP";
			}
		}
		else if(networkName.equals("SOT")){
			actConstantName = "COMMONS.ACTIVITY_CRITERION_SOT";
			closConstantName = "COMMONS.CLOSURE_CRITERION_SOT";
		}
		else if(networkName.equals("OCEANSITES")){
			actConstantName = "COMMONS.ACTIVITY_CRITERION_OCEANSITES";
			closConstantName = "COMMONS.CLOSURE_CRITERION_OCEANSITES";
		}
		else{
			actConstantName = "COMMONS.ACTIVITY_CRITERION_JCOMMOPS";
			closConstantName = "COMMONS.CLOSURE_CRITERION_JCOMMOPS";
		}

		// Fetching criteria from the DB
		Object[] result = SQLSelect.columnQuery("SELECT util.get_constant_value(#bind($actConst)), util.get_constant_value(#bind($cloConst)) from dual", Integer.class, Integer.class)
			.param("actConst", actConstantName)
			.param("cloConst", closConstantName).selectOne(context);
		activityCriterion = (Integer)result[0];
		closureCriterion = (Integer)result[1];
		
		Integer[] res = {activityCriterion, closureCriterion};
		return res;
    }
}
