package ai.vital.aspen.datawarehouse.mock

import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.typesafe.config.Config

class MockDao {

	private static MockDao singleton
	
	File jarsDir = null
	
	private final static Logger log = LoggerFactory.getLogger(MockDao.class)
	
	List jars = [
//		[name: 'jar1', 'filename': 'app1.jar', 'uploadTime': new Date(currentDate -6000000).toString(), 'size': '123 KB'],
//		[name: 'jar2', 'filename': 'app2.jar', 'uploadTime': new Date(currentDate -250000).toString(), 'size': '1.12 MB']
	]
	
	public static MockDao get() {
		if(singleton == null) throw new RuntimeException("Mock dao not set up")
		return singleton
	}
	private MockDao(Config mockConfig) {
		
		String jarsDirPath = mockConfig.getString("jarsDir")
		
		jarsDir = new File(jarsDirPath)
		
		log.info("Mock jars directory: ${jarsDir.absolutePath}")
		
		if( !jarsDir.isDirectory() ) throw new RuntimeException("Mock jars does not exist or is not a directory: ${jarsDir.absolutePath}")
		
		//mock files are kept in <encoded_app>/<file_name> structure
		if(jarsDir.listFiles().length == 0) {
			
			//init with two random files
			
			for(int i = 0 ; i < 2; i++) {
				
				File d = new File(jarsDir, "app${i+1}")
				d.mkdirs()
				File j = new File(d, "app${i+1}.jar")
				
				JarOutputStream jos = new JarOutputStream(new FileOutputStream(j))
				ZipEntry entry1 = new ZipEntry("app${i+1}.txt")
				jos.putNextEntry(entry1)
				jos.write("This is sample app${i+1} jar".getBytes("UTF-8"))
				ZipEntry entry2 = new ZipEntry("data${i+1}.bin")
				jos.putNextEntry(entry2)
				jos.write(RandomStringUtils.randomAlphanumeric(100000 + new Random().nextInt(900000)).getBytes('UTF-8'))
				jos.close()
				
				jars.add([name: (String)"app${i+1}", filename: j.name, uploadTime: new Date().toString(), size: humanReadableByteCount(j.length(),true)])
				
			}
			
		} else {
			
		
			for(File d : jarsDir.listFiles()) {
				
				if(!d.isDirectory()) continue
					
				File[] js = d.listFiles()
				
				if(js.length < 1) continue
				
				File j = js[0]
				
				jars.add([name: dec(d.name), filename: j.name, uploadTime: new Date(j.lastModified()).toString(), size: humanReadableByteCount(j.length(),true)])
				
			}
		
		}
		
	}
	
	static String enc(String s) {
		return URLEncoder.encode(s, 'UTF-8')
	}
	
	static String dec(String e) {
		return URLDecoder.decode(e, 'UTF-8')
	}
	
	public static MockDao init(Config mockConfig) {
		
		
		if(singleton != null) throw new RuntimeException("MockDao already initialized!")
		
		singleton = new MockDao(mockConfig)
		
		return singleton
		
	}
	
	public synchronized boolean deleteJar(String name) {
		
		Map record = null
		
		for(Map e : jars) {
			
			if(e.name == name) {
				record = e
				break
			}
			
		}
		
		if(record ==null) return false
		
		File thisD = null
		
		for( File d : jarsDir.listFiles() ) {
			
			if(dec(d.name) == name) {
				thisD = d
				break
			}
			
		}
		
		FileUtils.deleteQuietly(thisD)
		
		jars.remove(record)
		
		return true
		
	}

	//http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = "" + (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public synchronized List listJars() {
		return jars;
	}
	
	public synchronized boolean addJar(String name, String filename, File tempFile) {
		
		for(Map e : jars) {
			
			if(e.name == name) {
				//already exists
				return false
			}
			
		}
		
		File d = new File(jarsDir, enc(name))
		d.mkdirs()
		
		File j = new File(d, filename)
		
		FileUtils.moveFile(tempFile, j)
		
		jars.add([name: dec(d.name), filename: j.name, uploadTime: new Date(j.lastModified()).toString(), size: humanReadableByteCount(j.length(),true)])
		
		return true
		
	}
	
	public File getJar(String name) {
		
		Map record = null
		
		for(Map e : jars) {
			
			if(e.name == name) {
				//already exists
				record = e
				break
			}
			
		}
		
		if(record == null) return null
		
		File d = new File(jarsDir, enc(name))
		
		return new File(d, record.filename)
		
		
	}
}
