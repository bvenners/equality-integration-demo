package equalitydemo

import spire.algebra._
import spire.math._
import spire.implicits.{eqOps => _, _}

import org.scalactic._
import org.scalatest._
import TripleEqualsSupport.AToBEquivalenceConstraint
import TripleEqualsSupport.BToAEquivalenceConstraint
import scala.language.implicitConversions

final class SpireEquivalence[T](eq: Eq[T]) extends Equivalence[T] {
  def areEquivalent(a: T, b: T): Boolean = eq.eqv(a, b)
}
final class BToASpireConstraint[A, B](equivalenceOfA: Eq[A], cnv: B => A) extends Constraint[A, B] {
  override def areEqual(a: A, b: B): Boolean = equivalenceOfA.eqv(a, cnv(b))
}

trait LowPrioritySpireConstraints extends TripleEquals {
  implicit def lowPrioritySpireConstraint[A, B](implicit eqOfB: Eq[B], ev: A => B): Constraint[A, B] =
    new AToBEquivalenceConstraint[A, B](new SpireEquivalence(eqOfB), ev)
}

trait SpireEquality extends LowPrioritySpireConstraints {
  override def convertToEqualizer[T](left: T): Equalizer[T] = super.convertToEqualizer[T](left)
  implicit override def convertToCheckingEqualizer[T](left: T): CheckingEqualizer[T] = new CheckingEqualizer(left)
  override def unconstrainedEquality[A, B](implicit equalityOfA: Equality[A]): Constraint[A, B] = super.unconstrainedEquality[A, B]
  implicit def spireConstraint[A, B](implicit eqOfA: Eq[A], ev: B => A): Constraint[A, B] =
    new BToAEquivalenceConstraint[A, B](new SpireEquivalence(eqOfA), ev)
}

object SpireEquality extends SpireEquality

