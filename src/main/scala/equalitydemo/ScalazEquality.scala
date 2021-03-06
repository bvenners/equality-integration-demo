package equalitydemo

import scalaz._
import Scalaz.{ToEqualOps => _, _}

import org.scalactic._
import org.scalatest._
import TripleEqualsSupport.AToBEquivalenceConstraint
import TripleEqualsSupport.BToAEquivalenceConstraint
import scala.language.implicitConversions

final class ScalazEquivalence[T](equal: Equal[T]) extends Equivalence[T] {
  def areEquivalent(a: T, b: T): Boolean = equal.equal(a, b)
}

trait LowPriorityScalazConstraints extends TripleEquals {
  implicit def lowPriorityScalazConstraint[A, B](implicit equalOfB: Equal[B], ev: A => B): Constraint[A, B] =
    new AToBEquivalenceConstraint[A, B](new ScalazEquivalence(equalOfB), ev)
}

trait ScalazEquality extends LowPriorityScalazConstraints {
  override def convertToEqualizer[T](left: T): Equalizer[T] = super.convertToEqualizer[T](left)
  implicit override def convertToCheckingEqualizer[T](left: T): CheckingEqualizer[T] = new CheckingEqualizer(left)
  override def unconstrainedEquality[A, B](implicit equalityOfA: Equality[A]): Constraint[A, B] = super.unconstrainedEquality[A, B]
  implicit def spireConstraint[A, B](implicit equalOfA: Equal[A], ev: B => A): Constraint[A, B] =
    new BToAEquivalenceConstraint[A, B](new ScalazEquivalence(equalOfA), ev)
}

object ScalazEquality extends ScalazEquality

