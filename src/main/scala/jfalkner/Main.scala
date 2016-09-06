package jfalkner

import java.time.Instant

import reflect._
import scala.reflect.runtime.{ currentMirror => cm }
import scala.reflect.runtime.universe._

/**
  * Created by jfalkner on 9/1/16.
  */
object Main extends App {


//  def newCase[A](line: String)(implicit t: ClassTag[A]): A = {
//    val claas = cm classSymbol t.runtimeClass
//    val modul = claas.companion.asModule
//    val im = cm reflect (cm reflectModule modul).instance
//    defaut[A](im, "apply")
//  }

//  val line = s"Bar,123,${Instant.now()}"
//  println (unmarshall[Foo](line))
//  println(marshall(unmarshall[Foo](line)))
//
//  def unmarshall[A](line: String)(implicit t: ClassTag[A]): A = {
//    val claas = cm classSymbol t.runtimeClass
//    val modul = claas.companion.asModule
//    val im = cm reflect (cm reflectModule modul).instance
//
//    val args = line.split(",").zip(claas.primaryConstructor.asMethod.paramLists.head.map(_.typeSignature)).map{
//      case (v: String, t) if t =:= typeOf[String] => v
//      case (v: String, t) if t =:= typeOf[Int] => v.toInt
//      case (v: String, t) if t =:= typeOf[Instant] => Instant.parse(v)
//    }.asInstanceOf[Array[Object]]
//
//    val at = TermName("apply")
//    val ts = im.symbol.typeSignature
//    val method = (ts member at).asMethod
//    (im reflectMethod method)(args: _*).asInstanceOf[A]
//  }
//
//  def marshall[P <: Product](caseClass: P): String = {
//    caseClass.productIterator.map{
//      case v: Instant => v.toString
//      case v => v.toString
//    }.mkString(",")
//  }
}
