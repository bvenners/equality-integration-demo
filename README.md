equality-integration-demo
=========================

```scala
> console

import scalaz._
import Scalaz._
1L === 1
1 === 1L
1 === ()
() === 1

case class Box[T](o: T)

Box(1) === Box(1)

implicit def boxEqual[T] =
  new Equal[Box[T]] {
    def equal(a: Box[T], b: Box[T]): Boolean = a == b
  }

Box(1) === Box(1)

Box(1) === 1
1 === Box(1)

import scala.language.implicitConversions
implicit def widenIntToBox(i: Int): Box[Int] = Box(i)

Box(1) === 1
1 === Box(1)

--------------------------------------------
> console

import spire.algebra._
import spire.math._
import spire.implicits._
1L === 1
1 === 1L
1 === ()
() === 1

case class Box[T](o: T)

Box(1) === Box(1)

implicit def boxEq[T] =
  new Eq[Box[T]] {
    def eqv(a: Box[T], b: Box[T]): Boolean = a == b
  }

Box(1) === Box(1)

Box(1) === 1
1 === Box(1)

import scala.language.implicitConversions
implicit def widenIntToBox(i: Int): Box[Int] = Box(i)

Box(1) === 1
1 === Box(1)

val half = r"1/2"

half === 0.5
0.5 === half

--------------------------------------------
> console

import org.scalactic._
import ConversionCheckedTripleEquals._
1L === 1
1 === 1L
1 === ()
() === 1

case class Box[T](o: T)

Box(1) === Box(1)

Box(1) === 1
1 === Box(1)

import scala.language.implicitConversions
implicit def widenIntToBox(i: Int): Box[Int] = Box(i)

Box(1) === 1
1 === Box(1)

--------------------------------------------
> console
import spire.algebra._
import spire.math._
import spire.implicits.{eqOps => _, _}
import equalitydemo.SpireEquality._
1L === 1
1 === 1L
1 === ()
() === 1

case class Box[T](o: T)

Box(1) === Box(1)

implicit def boxEq[T] =
  new Eq[Box[T]] {
    def eqv(a: Box[T], b: Box[T]): Boolean = a == b
  }

Box(1) === Box(1)

Box(1) === 1
1 === Box(1)

import scala.language.implicitConversions
implicit def widenIntToBox(i: Int): Box[Int] = Box(i)

Box(1) === 1
1 === Box(1)

val half = r"1/2"

half === 0.5
0.5 === half

--------------------------------------------
console>

import scalaz._
import Scalaz.{ToEqualOps => _, _}
import equalitydemo.ScalazEquality._
1L === 1
1 === 1L
1 === ()
() === 1

case class Box[T](o: T)

Box(1) === Box(1)

implicit def boxEqual[T] =
  new Equal[Box[T]] {
    def equal(a: Box[T], b: Box[T]): Boolean = a == b
  }

Box(1) === Box(1)

Box(1) === 1
1 === Box(1)

import scala.language.implicitConversions
implicit def widenIntToBox(i: Int): Box[Int] = Box(i)

Box(1) === 1
1 === Box(1)

--------------------------------------------
> test:console

import scalaz._
import Scalaz.{ToEqualOps => _, _}
import equalitydemo.ScalazAssertions._
assert(1L === 1)
assert(1 === 1L)
assert(1 === ())
assert(() === 1)

case class Box[T](o: T)

assert(Box(1) === Box(1))

implicit def boxEqual[T] =
  new Equal[Box[T]] {
    def equal(a: Box[T], b: Box[T]): Boolean = a == b
  }

assert(Box(1) === Box(1))

assert(Box(1) === 1)
assert(1 === Box(1))

import scala.language.implicitConversions
implicit def widenIntToBox(i: Int): Box[Int] = Box(i)

assert(Box(1) === 1)
assert(1 === Box(1))
assert(2 === Box(1))

--------------------------------------------
> test:console

import spire.algebra._
import spire.math._
import spire.implicits.{eqOps => _, _}
import equalitydemo.SpireMatchers._
1L must === (1)
1 must === (1L)

case class Box[T](o: T)

Box(1) must === (Box(1))

implicit def boxEq[T] =
  new Eq[Box[T]] {
    def eqv(a: Box[T], b: Box[T]): Boolean = a == b
  }

Box(1) must === (Box(1))

Box(1) must === (1)
1 must === (Box(1))

import scala.language.implicitConversions
implicit def widenIntToBox(i: Int): Box[Int] = Box(i)

Box(1) must === (1)
1 must === (Box(1))

val half = r"1/2"

half must === (0.5)
0.5 must === (half)
```
