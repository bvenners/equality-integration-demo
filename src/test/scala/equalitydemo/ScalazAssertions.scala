package equalitydemo

import org.scalatest._

trait ScalazAssertions extends Assertions with ScalazEquality
object ScalazAssertions extends ScalazAssertions

