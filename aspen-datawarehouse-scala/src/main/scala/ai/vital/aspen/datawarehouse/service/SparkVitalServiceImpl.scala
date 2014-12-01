package ai.vital.aspen.datawarehouse.service


import org.apache.spark._

import ai.vital.aspen.datawarehouse.vitalsigns._


object SparkVitalServiceImpl {

  val appName = "SparkVitalService"
  
  val master = "local[2]"
  
  // use this config for initial testing
    
  val conf = new SparkConf().setAppName(appName).setMaster(master)
  
  // use this context for initial testing 
  
  val context = new SparkContext(conf)
    
  // force init of vitalsigns
  val vs = VitalSignsSingleton.vs
  
  // init()
  
  def init() {
     
  }
  
  
  // Manages N Segments
  
  // Manages N Data Collections
  
  // Manages N Models
  
  // Manages N Named RDDs
  
  // Manages N Contexts
  
  // Manages N Jobs (Job Templates available to run)
  
  // Manages N Jobs (Running Jobs, Job History)
  
  // Manages N Jars
  
  
  
  // these functions called via callFunction wrapper
  
  // add jar, list jars, remove jar
  
  // add data collection, list data collections, remove data collection
  
  // add context, list contexts, remove contexts
  
  // add mode, list models, remove model
  
  // add named rdd, list named rdds, remove named rdd
  
  // list jobs, add job, remove (kill) job
  
  // list job templates
  
  // list job history
  
  
  
  
  
  
  // vital service implementation functions
  
  
  // callFunction
  
  // registerOntology
  
  // getGraphObject
  
  // getGraphObjectExpanded
  
  // saveGraphObject
  
  // saveGraph
  
  // deleteGraphObject
  
  // deleteGraphObjectExpanded
  
  // selectQuery
  
  
  // graphQuery
  
  
  // generateURI
  
  // addSegment
  
  // removeSegment
  
  // list segments
  
  
  
  
  // sendFlumeEvent (sendEvent --> Spark Streaming)
  
  // sendFlumeEvents (Spark Streaming)
  
  // uploadFile (hdfs)
  
  // downloadFile (hdfs)
  
  // fileExists (hdfs)
  
  // deleteFile (hdfs)
  
  // ping
  
  
  def ping() {
    
    println ("Ping!")
    
  }
  
  
  
}