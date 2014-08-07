package equalitydemo

import scalaz._
import Scalaz.{ToEqualOps => _, _}

import org.scalactic._
import org.scalatest._
import scala.language.implicitConversions

class ScalazEqualizer[L](val leftSide: L) {
  def ===[R](rightSide: R)(implicit constraint: Constraint[L, R]): Boolean = constraint.areEqual(leftSide, rightSide)
  def !==[R](rightSide: R)(implicit constraint: Constraint[L, R]): Boolean = !constraint.areEqual(leftSide, rightSide)
}
final class AToBScalazConstraint[A, B](equivalenceOfB: Equal[B], cnv: A => B) extends Constraint[A, B] {
  override def areEqual(a: A, b: B): Boolean = equivalenceOfB.equal(cnv(a), b)
}
final class BToAScalazConstraint[A, B](equivalenceOfA: Equal[A], cnv: B => A) extends Constraint[A, B] {
  override def areEqual(a: A, b: B): Boolean = equivalenceOfA.equal(a, cnv(b))
}

trait LowPriorityScalazConstraints extends TripleEquals {
  implicit def lowPriorityScalazConstraint[A, B](implicit equivalenceOfB: Equal[B], ev: A => B): Constraint[A, B] = new AToBScalazConstraint[A, B](equivalenceOfB, ev)
}

trait ScalazEquality extends LowPriorityScalazConstraints {
  override def convertToEqualizer[T](left: T): Equalizer[T] = super.convertToEqualizer[T](left)
  override def unconstrainedEquality[A, B](implicit equalityOfA: Equality[A]): Constraint[A, B] = super.unconstrainedEquality[A, B]
  implicit def convertToScalazEqualizer[T](left: T): ScalazEqualizer[T] = new ScalazEqualizer(left)
  implicit def spireConstraint[A, B](implicit equivalenceOfA: Equal[A], ev: B => A): Constraint[A, B] = new BToAScalazConstraint[A, B](equivalenceOfA, ev)
}

object ScalazEquality extends ScalazEquality

