package jfalkner.logs

import java.nio.file.Files
import java.time.Instant

import org.apache.commons.io.FileUtils
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification


class LogsSpec extends Specification {

  val (a, b, c) = (Foo("A"), Foo("B"), Foo("C"))
  val suffix = ".example.csv"

  "Logs" should {
    "log and read single value" in {
      withCleanup{ logs =>
        def l = logs.make[Foo](suffix)
        l.log(a)
        Set(a) mustEqual l.load
      }
    }
    "log and read multiple values" in {
      withCleanup{ logs =>
        def l = logs.make[Foo](suffix)
        l.log(a)
        l.log(b)
        l.log(c)
        Set(a, b, c) mustEqual l.load
      }
    }
    "avoid overwriting existing log files" in {
      withCleanup{ logs =>
        val ts = Instant.now()
        logs.log(suffix, ts)(a)
        logs.log(suffix, ts)(b)
        logs.log(suffix, ts)(c)
        Set(a, b, c) mustEqual logs.load[Foo](suffix)
      }
    }
    "squash multiple files down to one" in {
      withCleanup{ logs =>
        def l = logs.make[Foo](suffix)
        val (pa, pb, pc) = (l.log(a), l.log(b), l.log(c))
        val all = l.squash()
        all.getFileName.toString mustEqual s"all$suffix"
        Set(a, b, c) mustEqual l.load()
        Seq(pa, pb, pc, all).map(_.toFile.exists) mustEqual(Seq(false, false, false, true))
      }
    }
    "logAll(Set)" in {
      withCleanup{ logs =>
        def l = logs.make[Foo]("set")
        l.logAll(Set(a, b, c))
        Set(a, b, c) mustEqual logs.load[Foo]("set")
      }
    }
    "logAll(Seq)" in {
      withCleanup{ logs =>
        def l = logs.make[Foo]("seq")
        l.logAll(Seq(a, b, c))
        Seq(a, b, c) mustEqual logs.load[Foo]("seq")
      }
    }
    "clear() removes old entries" in {
      withCleanup{ logs =>
        def l = logs.make[Foo]("clear")
        l.logAll(Seq(a, b))
        Seq(a, b) mustEqual l.load()
        l.clear()
        l.logAll(Seq(b, c))
        Seq(b, c) mustEqual l.load()
      }
    }
  }

  def withCleanup(f: (Logs) => MatchResult[Any])  : MatchResult[Any] = {
    val dir = Files.createTempDirectory("logs")
    try {
      f(new Logs {
        override val logsPath = dir
      })
    }
    finally {
      Seq(dir).foreach(p => FileUtils.deleteDirectory(p.toFile))
    }
  }
}

case class Foo(a: String)