equality-integration-demo
=========================

This project shows how Scalactic's Equality can be integrated with other equality mechanisms
such as the Eq typeclass from Spire or the Equal typeclass from Scalaz, and how that can then
be used in ScalaTest when testing projects that use those libraries. It was motivated by this
issue in Spire: 

First, the Haskell-inspired typeclass approach to equality in Spire and Scalaz has some problems
with how Scala applies implicit conversions. Here's an example in Scalaz:

```scala
-------------

> console
[info] Starting scala interpreter...
[info] 
Welcome to Scala version 2.10.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_65).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import scalaz._
import scalaz._

scala> import Scalaz._
import Scalaz._

scala> 1L === 1 // An implicit Long => Int conversion is applied
res0: Boolean = true

scala> 1 === 1L // Asymmetrically, this doesn't compile
<console>:14: error: could not find implicit value for parameter F0: scalaz.Equal[Any]
              1 === 1L
              ^

scala> 1 === () // This fails to compile, as you'd wish
<console>:14: error: not enough arguments for method ===: (other: Int)Boolean.
Unspecified value parameter other.
              1 === ()
                ^

scala> () === 1 // Asymmetrically, this compiles, and moreover yields true!
<console>:14: warning: a pure expression does nothing in statement position; you may be omitting necessary parentheses
              () === 1
                     ^
res3: Boolean = true

scala> 

scala> case class Box[T](o: T)
defined class Box

scala> 

scala> Box(1) === Box(1)  // This fails to compile as planned, because no Equal[Box[Int]] has yet been defined
<console>:16: error: could not find implicit value for parameter F0: scalaz.Equal[Box[Int]]
              Box(1) === Box(1)
                 ^

scala> 

scala> implicit def boxEqual[T] =
     |   new Equal[Box[T]] {
     |     def equal(a: Box[T], b: Box[T]): Boolean = a == b
     |   }
boxEqual: [T]=> scalaz.Equal[Box[T]]

scala> 

scala> Box(1) === Box(1) // Now it works
res5: Boolean = true

scala> 

scala> Box(1) === 1 // This does not work, as the types don't match
<console>:17: error: could not find implicit value for parameter F0: scalaz.Equal[Any]
              Box(1) === 1
                 ^

scala> 1 === Box(1) // And the compile error is symmetric
<console>:17: error: could not find implicit value for parameter F0: scalaz.Equal[Object]
              1 === Box(1)
              ^

scala> 

scala> import scala.language.implicitConversions
import scala.language.implicitConversions

scala> implicit def widenIntToBox(i: Int): Box[Int] = Box(i)
widenIntToBox: (i: Int)Box[Int]

scala> 

scala> Box(1) === 1 // But define an Int => Box[Int] implicit widening conversion
res8: Boolean = true

scala> 1 === Box(1) // And you get that assymetry again
<console>:19: error: could not find implicit value for parameter F0: scalaz.Equal[Object]
              1 === Box(1)
              ^

scala> :q

[success] Total time: 19 s, completed Aug 6, 2014 8:35:57 PM
> console
[info] Starting scala interpreter...
[info] 
Welcome to Scala version 2.10.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_65).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import spire.algebra._
import spire.algebra._

scala> import spire.math._
import spire.math._

scala> import spire.implicits._
import spire.implicits._

scala> 1L === 1
res0: Boolean = true

scala> 1 === 1L // Spire's Eq has a similar problem to Scalaz's Equal
<console>:17: error: diverging implicit expansion for type spire.algebra.Eq[Any]
starting with method SeqOrder in trait SeqInstances2
              1 === 1L
              ^

scala> 1 === ()
<console>:17: error: macros application do not support named and/or default arguments
              1 === ()
                ^

scala> () === 1 // But at least both of these comparisons with Unit fail to compile in Spire
<console>:17: error: could not find implicit value for evidence parameter of type spire.algebra.Eq[AnyVal]
              () === 1
              ^

scala> 

scala> case class Box[T](o: T)
defined class Box

scala> 

scala> Box(1) === Box(1) // As before, this is by design as no Eq[Box[Int]] as yet exists
<console>:19: error: could not find implicit value for evidence parameter of type spire.algebra.Eq[Box[Int]]
              Box(1) === Box(1)
                 ^

scala> 

scala> implicit def boxEq[T] =
     |   new Eq[Box[T]] {
     |     def eqv(a: Box[T], b: Box[T]): Boolean = a == b
     |   }
boxEq: [T]=> spire.algebra.Eq[Box[T]]

scala> 

scala> Box(1) === Box(1) // Now this works
res5: Boolean = true

scala> 

scala> Box(1) === 1 // A slightly scary compiler error message, but...
<console>:20: error: diverging implicit expansion for type spire.algebra.Eq[Any]
starting with method SeqOrder in trait SeqInstances2
              Box(1) === 1
                 ^

scala> 1 === Box(1) // The good thing is both of these fail to compile symmetrically
<console>:20: error: could not find implicit value for evidence parameter of type spire.algebra.Eq[Object]
              1 === Box(1)
              ^

scala> 

scala> import scala.language.implicitConversions
import scala.language.implicitConversions

scala> implicit def widenIntToBox(i: Int): Box[Int] = Box(i)
widenIntToBox: (i: Int)Box[Int]

scala> 

scala> Box(1) === 1 // But define the widening conversion, and the assymetry shows up again
res8: Boolean = true

scala> 1 === Box(1)
<console>:22: error: could not find implicit value for evidence parameter of type spire.algebra.Eq[Object]
              1 === Box(1)
              ^

scala> 

scala> val half = r"1/2"
half: spire.math.Rational = 1/2

scala> 

scala> half === 0.5 // Note that it happens with Spire's own implicit widening conversions
res10: Boolean = true

scala> 0.5 === half // Such as Double => Rational.
<console>:23: error: could not find implicit value for evidence parameter of type spire.algebra.Eq[Object]
              0.5 === half
              ^

scala> :q

[success] Total time: 22 s, completed Aug 6, 2014 8:36:20 PM
> console
[info] Starting scala interpreter...
[info] 
Welcome to Scala version 2.10.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_65).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import org.scalactic._
import org.scalactic._

scala> import ConversionCheckedTripleEquals._
import ConversionCheckedTripleEquals._

scala> 1L === 1
res0: Boolean = true

scala> 1 === 1L
res1: Boolean = true

scala> 1 === ()
<console>:14: error: types Int and Unit do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Unit]
              1 === ()
                ^

scala> () === 1
<console>:14: error: types Unit and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Unit,Int]
              () === 1
                 ^

scala> 

scala> case class Box[T](o: T)
defined class Box

scala> 

scala> Box(1) === Box(1)
res4: Boolean = true

scala> 

scala> Box(1) === 1
<console>:16: error: types Box[Int] and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Int]
              Box(1) === 1
                     ^

scala> 1 === Box(1)
<console>:16: error: types Int and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Box[Int]]
              1 === Box(1)
                ^

scala> 

scala> import scala.language.implicitConversions
import scala.language.implicitConversions

scala> implicit def widenIntToBox(i: Int): Box[Int] = Box(i)
widenIntToBox: (i: Int)Box[Int]

scala> 

scala> Box(1) === 1
res7: Boolean = true

scala> 1 === Box(1)
res8: Boolean = true

scala> :q

[success] Total time: 15 s, completed Aug 6, 2014 8:36:39 PM
> console
[info] Starting scala interpreter...
[info] 
Welcome to Scala version 2.10.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_65).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import spire.algebra._
import spire.algebra._

scala> import spire.math._
import spire.math._

scala> import spire.implicits.{eqOps => _, _}
import spire.implicits.{eqOps=>_, _}

scala> import equalitydemo.SpireEquality._
import equalitydemo.SpireEquality._

scala> 1L === 1
res0: Boolean = true

scala> 1 === 1L
res1: Boolean = true

scala> 1 === ()
<console>:20: error: types Int and Unit do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Unit]
              1 === ()
                ^

scala> () === 1
<console>:20: error: types Unit and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Unit,Int]
              () === 1
                 ^

scala> 

scala> case class Box[T](o: T)
defined class Box

scala> 

scala> Box(1) === Box(1)
<console>:22: error: types Box[Int] and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Box[Int]]
              Box(1) === Box(1)
                     ^

scala> 

scala> implicit def boxEq[T] =
     |   new Eq[Box[T]] {
     |     def eqv(a: Box[T], b: Box[T]): Boolean = a == b
     |   }
boxEq: [T]=> spire.algebra.Eq[Box[T]]

scala> 

scala> Box(1) === Box(1)
res5: Boolean = true

scala> 

scala> Box(1) === 1
<console>:23: error: types Box[Int] and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Int]
              Box(1) === 1
                     ^

scala> 1 === Box(1)
<console>:23: error: types Int and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Box[Int]]
              1 === Box(1)
                ^

scala> 

scala> import scala.language.implicitConversions
import scala.language.implicitConversions

scala> implicit def widenIntToBox(i: Int): Box[Int] = Box(i)
widenIntToBox: (i: Int)Box[Int]

scala> 

scala> Box(1) === 1
res8: Boolean = true

scala> 1 === Box(1)
res9: Boolean = true

scala> 

scala> val half = r"1/2"
half: spire.math.Rational = 1/2

scala> 

scala> half === 0.5
res10: Boolean = true

scala> 0.5 === half
res11: Boolean = true

scala> :q

[success] Total time: 18 s, completed Aug 6, 2014 8:37:00 PM
> console
[info] Starting scala interpreter...
[info] 
Welcome to Scala version 2.10.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_65).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import scalaz._
import scalaz._

scala> import Scalaz.{ToEqualOps => _, _}
import Scalaz.{ToEqualOps=>_, _}

scala> import equalitydemo.ScalazEquality._
import equalitydemo.ScalazEquality._

scala> 1L === 1
res0: Boolean = true

scala> 1 === 1L
res1: Boolean = true

scala> 1 === ()
<console>:17: error: types Int and Unit do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Unit]
              1 === ()
                ^

scala> () === 1
<console>:17: error: types Unit and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Unit,Int]
              () === 1
                 ^

scala> 

scala> case class Box[T](o: T)
defined class Box

scala> 

scala> Box(1) === Box(1)
<console>:19: error: types Box[Int] and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Box[Int]]
              Box(1) === Box(1)
                     ^

scala> 

scala> implicit def boxEqual[T] =
     |   new Equal[Box[T]] {
     |     def equal(a: Box[T], b: Box[T]): Boolean = a == b
     |   }
boxEqual: [T]=> scalaz.Equal[Box[T]]

scala> 

scala> Box(1) === Box(1)
res5: Boolean = true

scala> 

scala> Box(1) === 1
<console>:20: error: types Box[Int] and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Int]
              Box(1) === 1
                     ^

scala> 1 === Box(1)
<console>:20: error: types Int and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Box[Int]]
              1 === Box(1)
                ^

scala> 

scala> import scala.language.implicitConversions
import scala.language.implicitConversions

scala> implicit def widenIntToBox(i: Int): Box[Int] = Box(i)
widenIntToBox: (i: Int)Box[Int]

scala> 

scala> Box(1) === 1
res8: Boolean = true

scala> 1 === Box(1)
res9: Boolean = true

scala> :q

[success] Total time: 19 s, completed Aug 6, 2014 8:37:29 PM
> test:console
[info] Starting scala interpreter...
[info] 
Welcome to Scala version 2.10.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_65).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import scalaz._
import scalaz._

scala> import Scalaz.{ToEqualOps => _, _}
import Scalaz.{ToEqualOps=>_, _}

scala> import equalitydemo.ScalazAssertions._
import equalitydemo.ScalazAssertions._

scala> assert(1L === 1)

scala> assert(1 === 1L)

scala> assert(1 === ())
<console>:17: error: types Int and Unit do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Unit]
              assert(1 === ())
                       ^

scala> assert(() === 1)
<console>:17: error: types Unit and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Unit,Int]
              assert(() === 1)
                        ^

scala> 

scala> case class Box[T](o: T)
defined class Box

scala> 

scala> assert(Box(1) === Box(1))
<console>:19: error: types Box[Int] and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Box[Int]]
              assert(Box(1) === Box(1))
                            ^

scala> 

scala> implicit def boxEqual[T] =
     |   new Equal[Box[T]] {
     |     def equal(a: Box[T], b: Box[T]): Boolean = a == b
     |   }
boxEqual: [T]=> scalaz.Equal[Box[T]]

scala> 

scala> assert(Box(1) === Box(1))

scala> 

scala> assert(Box(1) === 1)
<console>:20: error: types Box[Int] and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Int]
              assert(Box(1) === 1)
                            ^

scala> assert(1 === Box(1))
<console>:20: error: types Int and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Box[Int]]
              assert(1 === Box(1))
                       ^

scala> 

scala> import scala.language.implicitConversions
import scala.language.implicitConversions

scala> implicit def widenIntToBox(i: Int): Box[Int] = Box(i)
widenIntToBox: (i: Int)Box[Int]

scala> 

scala> assert(Box(1) === 1)

scala> assert(1 === Box(1))

scala> assert(2 === Box(1))
org.scalatest.exceptions.TestFailedException: 2 did not equal Box(1)
	at org.scalatest.Assertions$class.newAssertionFailedException(Assertions.scala:500)
	...

scala> :q

[success] Total time: 12 s, completed Aug 6, 2014 8:37:42 PM

> test:console
[info] Starting scala interpreter...
[info] 
Welcome to Scala version 2.10.4 (Java HotSpot(TM) 64-Bit Server VM, Java 1.6.0_65).
Type in expressions to have them evaluated.
Type :help for more information.

scala> import spire.algebra._
import spire.algebra._

scala> import spire.math._
import spire.math._

scala> import spire.implicits.{eqOps => _, _}
import spire.implicits.{eqOps=>_, _}

scala> import equalitydemo.SpireMatchers._
import equalitydemo.SpireMatchers._

scala> 1L must === (1)

scala> 1 must === (1L)

scala> 

scala> case class Box[T](o: T)
defined class Box

scala> 

scala> Box(1) must === (Box(1))
<console>:22: error: types Box[Int] and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Box[Int]]
              Box(1) must === (Box(1))
                     ^

scala> 

scala> implicit def boxEq[T] =
     |   new Eq[Box[T]] {
     |     def eqv(a: Box[T], b: Box[T]): Boolean = a == b
     |   }
boxEq: [T]=> spire.algebra.Eq[Box[T]]

scala> 

scala> Box(1) must === (Box(1))

scala> 

scala> Box(1) must === (1)
<console>:23: error: types Box[Int] and Int do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Box[Int],Int]
              Box(1) must === (1)
                     ^

scala> 1 must === (Box(1))
<console>:23: error: types Int and Box[Int] do not adhere to the type constraint selected for the === and !== operators; the missing implicit parameter is of type org.scalactic.Constraint[Int,Box[Int]]
              1 must === (Box(1))
                ^

scala> 

scala> import scala.language.implicitConversions
import scala.language.implicitConversions

scala> implicit def widenIntToBox(i: Int): Box[Int] = Box(i)
widenIntToBox: (i: Int)Box[Int]

scala> 

scala> Box(1) must === (1)

scala> 1 must === (Box(1))

scala> 

scala> val half = r"1/2"
half: spire.math.Rational = 1/2

scala> 

scala> half must === (0.5)

scala> 0.5 must === (half)

scala> 2 must === (Box(1))
org.scalatest.exceptions.TestFailedException: 2 did not equal Box(1)
	at org.scalatest.MatchersHelper$.newTestFailedException(MatchersHelper.scala:160)
	...

```

Below is what I copied and pasted into the REPL that produced the above output:

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
2 must === (half)
```
