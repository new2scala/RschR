package org.ditw.graphProb.belUpdating

import org.ditw.graphProb.belUpdating.Potentials.Potential

/**
  * Created by dev on 2017-12-15.
  */
object ProbModels {
  case class ProbModel(potentials:List[Potential], desc:String = "")


}
