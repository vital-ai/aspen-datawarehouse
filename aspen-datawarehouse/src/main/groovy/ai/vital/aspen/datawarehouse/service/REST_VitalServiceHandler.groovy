package ai.vital.aspen.datawarehouse.service

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream
import java.util.HashSet;
import java.util.List
import java.util.Map
import java.util.Set

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.SerializationUtils;
import org.vertx.java.core.Handler
import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.http.HttpServerRequest
import org.vertx.java.core.http.HttpServerResponse

import ai.vital.aspen.datawarehouse.service.AbstractHandler
import ai.vital.endpoint.EndpointType
import ai.vital.vitalservice.VitalStatus
import ai.vital.vitalservice.exception.VitalServiceException
import ai.vital.vitalservice.exception.VitalServiceUnimplementedException
import ai.vital.vitalservice.model.App
import ai.vital.vitalservice.model.Customer
import ai.vital.vitalservice.query.VitalGraphQuery
import ai.vital.vitalservice.query.VitalSelectQuery
import ai.vital.vitalservice.segment.VitalSegment
import ai.vital.vitalsigns.datatype.VitalURI
import ai.vital.vitalsigns.model.GraphObject


class REST_VitalServiceHandler extends AbstractHandler implements Handler<HttpServerRequest>{

	// REST handler, similar to vital prime implementation
	
	// client implementation in vitalservice-source
	
	// calls SparkVitalServiceImpl in scala codebase
	
	private static Set<String> commandsWithoutCustomer = new HashSet<String>();
	
	static {
		commandsWithoutCustomer.add("ping");
		commandsWithoutCustomer.add("shutdown");
		commandsWithoutCustomer.add("addCustomer");
		commandsWithoutCustomer.add("removeCustomer");
		commandsWithoutCustomer.add("listCustomers");
	}
	
	/*
	 * @Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		return this.post(entity);
	}
	 */
	
	/*
	protected Representation post(Representation entity)
	throws ResourceException {



if(!MediaType.APPLICATION_JAVA_OBJECT.equals(entity.getMediaType())) {
	throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, "Expected content type: " + MediaType.APPLICATION_JAVA_OBJECT.toString());
}

ObjectInputStream inputRequest = null;

Map<String, Object> input = null;

try {
	inputRequest = new ObjectInputStream(entity.getStream());

	input = (Map<String, Object>) inputRequest.readObject();
	
} catch (Exception e) {
	throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, e.getLocalizedMessage());
} finally {
	IOUtils.closeQuietly(inputRequest);
}


Object toSerialize = null;

try {
	Object output = handle_vs(input);
	toSerialize = output;
} catch (VitalServiceException e) {
	toSerialize = new VitalServiceException(e.getLocalizedMessage());
} catch (VitalServiceUnimplementedException e) {
	toSerialize = new VitalServiceException(e.getLocalizedMessage());
} catch(ResourceException e) {
	throw e;
} catch(Exception e) {
	throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
}

final Object o = toSerialize;

OutputRepresentation os = new OutputRepresentation(MediaType.APPLICATION_JAVA_OBJECT) {
	
	
	public void write(OutputStream arg0) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(arg0);
		oos.writeObject(o);
	}
};

return os;

}
	
	*/
	
	@Override
	public void handle(HttpServerRequest req) {

		String contentType = req.headers().get("Content-Type");
		
		final HttpServerResponse resp = req.response()
		
		try {
			
			if(contentType != "application/x-java-serialized-object") {
				resp.setStatusCode(415).end("Supports application/x-java-serialized-object only")
				return
			}
			
			req.bodyHandler(new Handler<Buffer>(){
				
				void handle( Buffer buffer){
			
					ByteArrayInputStream bis = new ByteArrayInputStream(buffer.getBytes())
					
					
					ObjectInputStream inputRequest = null;
					
					Map<String, Object> input = null;
					
					try {
						inputRequest = new ObjectInputStream(bis);
			
						input = (Map<String, Object>) inputRequest.readObject();
						
					} catch (Exception e) {
						resp.setStatusCode(422).end(e.getLocalizedMessage());
					} finally {
						IOUtils.closeQuietly(inputRequest);
					}
					
					
					Object toSerialize = null;
					
					try {
						Object output = handle_vs(input);
						toSerialize = output;
					} catch (VitalServiceException e) {
						toSerialize = new VitalServiceException(e.getLocalizedMessage());
					} catch (VitalServiceUnimplementedException e) {
						toSerialize = new VitalServiceException(e.getLocalizedMessage());
					} catch(Exception e) {
						throw resp.setStatusCode(500).end(e.localizedMessage);
					}
			
					
					resp.end(new Buffer(SerializationUtils.serialize(toSerialize)))
					
					
					/*
					final Object o = toSerialize;
					
					OutputRepresentation os = new OutputRepresentation(MediaType.APPLICATION_JAVA_OBJECT) {
						
						@Override
						public void write(OutputStream arg0) throws IOException {
							ObjectOutputStream oos = new ObjectOutputStream(arg0);
							oos.writeObject(o);
						}
					};
			
					return os;
					*/
							
				}
				
			});
		
			/*
			req.exceptionHandler(new Handler<Throwable>(){
				
				void handler(Throwable thr) {
					re
				}
				
			});
			*/
		} catch(Exception e) {
			resp.setStatusCode(500).end(e.localizedMessage)
		}
		
	}

	
	private Object handle_vs(Map<String, Object> input) throws VitalServiceException, VitalServiceUnimplementedException {
		
		
				String method = (String) input.get("method");
				String customerID = (String) input.get("customer");
				String appID = (String) input.get("app");
				Map<String, Object> params = (Map<String, Object>) input.get("params");
				
				Customer c = null;
				
				
				if(customerID != null) {
					
					c = new Customer();
					
					c.setID(customerID);
				} else {
					
					if(! ( commandsWithoutCustomer.contains(method) ) ) {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No 'customer' parameter");
					}
					
					
				}
				
				if( customerID != null && ! service.getCustomer().getID().equals(customerID) ) {
		
					throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "Invalid 'customer' parameter - " + customerID + " - this prime instance supports " + service.getCustomer().getID() + " only.");
					
				}
		
				App a = null;
				
				if(appID != null) {
					
					a = new App();
					
					a.setID(appID);
					
					a.setCustomerID(customerID);
					
				}
				
				
				if("shutdown".equals(method)) {
					
				
				}
				
				if("endpoints".equals(method)) {
					
					
					
				}
				
				if("listDatascripts".equals(method)) {
					
					
					
					
				}
				
				if("addDatascript".equals(method)) {
					
					
					
					
				}
				
				if("removeDatascript".equals(method)) {
					
					
				}
				
				if("callFunction".equals(method)) {
		
					Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
					
					String function = (String) params.get("function");
					
					//return service.callFunction(c, a, function, arguments);
					
				}
		
				if("get".equals(method)) {
				
					//return service.get(c, a, (VitalURI) params.get("uri"));
					
				}
		
				if("save".equals(method)) {
					
					String segmentID = (String) params.get("targetSegment");
					
					GraphObject graphObject = (GraphObject) params.get("graphObject");
					
					List<GraphObject> graphObjectList = (List<GraphObject>) params.get("graphObjectsList");
					
					/*
					if(graphObject != null) {
						return service.save(c, a, VitalSegment.withId(segmentID), graphObject);
					} else if(graphObjectList != null) {
						return service.save(c, a, VitalSegment.withId(segmentID), graphObjectList);
					} else {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No graph object or list");
					}
					*/
					
				}
				
				if("delete".equals(method)) {
					
					VitalURI uri = (VitalURI) params.get("uri");
					
					List<VitalURI> uris = (List<VitalURI>) params.get("uris");
					/*
					if(uri != null) {
						return service.delete(c, a, uri);
					} else if(uris != null) {
						return service.delete(c, a, uris);
					} else {
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "No uri or uris list");
					}
					*/
					
				}
				
				if("deleteObjects".equals(method)) {
					
					List<GraphObject> objects = (List<GraphObject>) params.get("objects");
					
					//return service.deleteObjects(c, a, objects);
					
				}
		
				if("selectQuery".equals(method)) {
					
					VitalSelectQuery query = (VitalSelectQuery) params.get("query");
					
					//return service.selectQuery(c, a, query);
					
				}
		
				
				if("graphQuery".equals(method)) {
					
					VitalGraphQuery query = (VitalGraphQuery) params.get("query");
					
					//return service.graphQuery(c, a, query);
					
				}
		
				
				if("listSegments".equals(method)) {
					
					//return service.listSegments(c, a);
					
				}
		
				
				if("ping".equals(method)) {
					
					//return service.ping();
					return VitalStatus.withOKMessage("Aspen REST service is up")
					
				}
				
				
				if("addSegment".equals(method)) {
					
					VitalSegment segment = (VitalSegment) params.get("segment");
					
					//if(! (segment instanceof PrimeVitalSegment)) {
					//	throw new RuntimeException("Expected instanceof " + PrimeVitalSegment.class.getCanonicalName());
					//}
					
					boolean createIfNotExists = (boolean) params.get("createIfNotExists");
					
					//return service.addSegment(c, a, segment, createIfNotExists);
					
				}
		
				
				if("removeSegment".equals(method)) {
					
					String segmentID = (String) params.get("segment");
					
					boolean deleteData = (boolean) params.get("deleteData");
					
					//return service.removeSegment(c, a, VitalSegment.withId(segmentID), deleteData);
					
				}
		
				if("listApps".equals(method)) {
					
					//return service.listApps(c);
					
				}
				
		
				if("addApp".equals(method)) {
					
					App app = (App) params.get("app");
					
					//return service.addApp(c, app);
					
				}
				
				if("removeApp".equals(method)) {
					
					App app = (App) params.get("app");
					
					//return service.removeApp(c, app);
					
				}
		
				
				if("addCustomer".equals(method)) {
					
					Customer customer = (Customer) params.get("customer");
					
					//return service.addCustomer(customer);
					
				}
				
				if("removeCustomer".equals(method)) {
					
					Customer customer = (Customer) params.get("customer");
		
					//return service.removeCustomer(customer);
					
				}
		
				
				if("listCustomers".equals(method)) {
					
					//return service.listCustomers();
					
				}
				
				
				if("uploadFile".equals(method)) {
					throw new VitalServiceException("Use another endpoint for file upload!");
				}
				
				if("downloadFile".equals(method)) {
					throw new VitalServiceException("Use another endpoint for file upload!");
				}
				
				if("fileExists".equals(method)) {
					VitalURI uri = (VitalURI) params.get("uri");
					String fileName = (String) params.get("fileName");
					//try {
					//	return VitalPrime.get().filesComponent.fileExists(c, a, uri.URI, fileName) ? VitalStatus.OK : VitalStatus.withError("file does not exist");
					//} catch (IOException e) {
					//	throw new VitalServiceException(e.getLocalizedMessage());
					//}
				}
		
				
				if("deleteFile".equals(method)) {
					VitalURI uri = (VitalURI) params.get("uri");
					String fileName = (String) params.get("fileName");
					//try {
					//	return VitalPrime.get().filesComponent.deleteFile(c, a, uri.URI, fileName) ? VitalStatus.OK : VitalStatus.withError("file does not exist");
					//} catch(Exception e) {
					//	throw new VitalServiceException(e.getLocalizedMessage());
					//}
				}
				
				//throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Unknown method: " + method);
			}
	
}
