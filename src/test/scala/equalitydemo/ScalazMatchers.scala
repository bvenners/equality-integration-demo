package equalitydemo

import org.scalatest._

trait ScalazMatchers extends MustMatchers with ScalazEquality
object ScalazMatchers extends ScalazMatchers

