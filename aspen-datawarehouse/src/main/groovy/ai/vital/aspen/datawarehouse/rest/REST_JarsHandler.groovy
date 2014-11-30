package ai.vital.aspen.datawarehouse.rest

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.VoidHandler
import org.vertx.java.core.http.HttpServerFileUpload;
import org.vertx.java.core.http.HttpServerRequest;

import ai.vital.aspen.datawarehouse.mock.MockDao;

class REST_JarsHandler extends AbstractHandler implements Handler<HttpServerRequest> {

	@Override
	public void handle(HttpServerRequest request) {

		String uri = request.uri();
		String method = request.method();
		
		if(method == 'POST') {
			
			request.expectMultiPart(true);

			File tempFile = File.createTempFile("aspen", ".jar")
			
			String filename = null			
			
			request.uploadHandler(new Handler<HttpServerFileUpload>(){
				
				public void handle(HttpServerFileUpload upload) {
					
					//just stream the upload to temp file
					filename = upload.filename()
					
					upload.streamToFileSystem(tempFile.getAbsolutePath())
					
				}
				
			});
		
			request.endHandler(new VoidHandler(){
				
				public void handle() {
				
					MultiMap form = request.formAttributes()
					
					String name = form.get("name");
					
					if(!name) {
						
						request.response().setStatusCode(400)
						request.response().end("No 'name' parameter")
						return
					}
					
					try {
						boolean added = MockDao.get().addJar(name, filename, tempFile)
						if(!added){
							request.response().setStatusCode(409).end("Jar ${name} already exists")
						} else {
							request.response().setStatusCode(200).end("OK")
						}						
					} catch(Exception e) {
						request.response().setStatusCode(500).end(e.localizedMessage)
					}
					

					
					
				}
			});
						//binary data stream ?
			
			
		} else if(method == 'GET') {
		
			try {
				
				MultiMap form = request.params()
				
				String name = form.get("name");
				
				if(!name) {
					
					request.response().setStatusCode(400)
					request.response().end("No 'name' parameter")
					return
				}
				
				File jarF = MockDao.get().getJar(name)
				if(jarF==null){
					request.response().setStatusCode(404).end("Jar ${name} not found")
				} else {
//					request.response().headers().add('Content-Length', '' + jarF.length())
//					request.response().headers().add('Content-Type', 'application/java-archive')
					request.response().headers().add('Content-Disposition', "attachment; filename=${jarF.name}")
					request.response().sendFile(jarF.absolutePath)
				}
			} catch(Exception e) {
				request.response().setStatusCode(500).end(e.localizedMessage)
			}
		
		}
		
		
	}

}
