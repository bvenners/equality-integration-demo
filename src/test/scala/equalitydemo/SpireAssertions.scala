package equalitydemo

import org.scalatest._

trait SpireAssertions extends Assertions with SpireEquality
object SpireAssertions extends SpireAssertions

