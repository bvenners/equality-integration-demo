package equalitydemo

import org.scalatest._

trait SpireMatchers extends MustMatchers with SpireEquality
object SpireMatchers extends SpireMatchers

