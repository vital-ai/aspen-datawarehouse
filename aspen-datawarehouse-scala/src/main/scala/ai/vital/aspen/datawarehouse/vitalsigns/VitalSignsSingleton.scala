package ai.vital.aspen.datawarehouse.vitalsigns

import ai.vital.vitalsigns.VitalSigns

object VitalSignsSingleton {

  // convenience class wrapper around underlying VitalSigns implementation
  
  
    val vs = VitalSigns.get()

  
}