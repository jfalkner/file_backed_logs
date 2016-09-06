package jfalkner.logs

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.time.Instant

import jfalkner.cc2csv.Csv.{marshall, unmarshall}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag


// Logic related to marshall/unmarshall-ing log entries
trait Logs {

  val logsPath: Path
  lazy val ignoredDirs: Set[String] = Set()

  // what prefix the combined log file uses
  val aggregatePostfix = "all"

  def make[P <: Product: ClassTag](postfix: String): Logger[P] = new Logger[P](postfix)

  def filter(f: File, isdir: Boolean): Boolean = f.isDirectory == isdir && !ignoredDirs.contains(f.getName)

  def ls(path: Path, isdir: Boolean = false): Set[Path] =
    Files.newDirectoryStream(path).asScala.toList.filter(x => filter(x.toFile, isdir)).toSet

  def resolve(postfix: String, ts: Instant = Instant.now()) : Path = {
    val path = logsPath.resolve(s"$ts$postfix")
    if (path.toFile.exists()) resolve(postfix, ts.plusMillis(1)) else path
  }


  // save multiple
  def logAll(postfix: String, ts: Instant = Instant.now())(values: Seq[Product]) : Option[Path] =
    if (!values.isEmpty) Some(Files.write(resolve(postfix, ts), values.map(marshall).mkString("\n").getBytes)) else None

  // save single
  def log(postfix: String, ts: Instant = Instant.now())(value: Product) : Path =
    Files.write(resolve(postfix, ts), marshall(value).getBytes)

  // loads existing
  def load[T <: Product: ClassTag](postfix: String): Set[T] =
    ls(logsPath).filter(_.toString.endsWith(postfix)).flatMap(f => Files.readAllLines(f).asScala.map(unmarshall[T]))

  // condense a bunch down to done log -- write all then delete
  def squash[T <: Product: ClassTag](postfix: String): Path = {
    val all = Files.write(resolve(aggregatePostfix), load[T](postfix).map(marshall).mkString("\n").getBytes)
    ls(logsPath).filter(_.toString.endsWith(postfix)).filter(!_.startsWith("all")).foreach(Files.deleteIfExists)
    all
  }

  // postfix-specific logger
  class Logger[P <: Product: ClassTag](postfix: String) {
    def log(value: P): Path = Logs.this.log(postfix)(value)
    def logAll(values: Seq[P]): Option[Path] = Logs.this.logAll(postfix)(values)
    def load(): Set[P] = Logs.this.load[P](postfix)
    def squash(): Path = Logs.this.squash[P](postfix)
  }

}