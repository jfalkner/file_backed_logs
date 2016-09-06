package jfalkner.logs

import java.nio.file.Files
import java.time.Instant

import org.apache.commons.io.FileUtils
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification


class LogsSpec extends Specification {

  val (a, b, c) = (Foo("A"), Foo("B"), Foo("C"))

  "Logs" should {
    "log and read single value" in {
      withCleanup{ logs =>
        val prefix = "one"
        def l = logs.make[Foo](prefix)
        l.log(a)
        Set(a) mustEqual l.load
      }
    }
    "log and read multiple values" in {
      withCleanup{ logs =>
        val prefix = "many"
        def l = logs.make[Foo](prefix)
        l.log(a)
        l.log(b)
        l.log(c)
        Set(a, b, c) mustEqual l.load
      }
    }
    "avoid overwriting existing log files" in {
      withCleanup{ logs =>
        val prefix = "override"
        val ts = Instant.now()
        logs.log(prefix, ts)(a)
        logs.log(prefix, ts)(b)
        logs.log(prefix, ts)(c)
        Set(a, b, c) mustEqual logs.load[Foo](prefix)
      }
    }
    "squash multiple files down to one" in {
      withCleanup{ logs =>
        val prefix = "squash"
        def l = logs.make[Foo](prefix)
        val (pa, pb, pc) = (l.log(a), l.log(b), l.log(c))
        val all = l.squash()
        Set(a, b, c) mustEqual logs.load(prefix)
        Seq(pa, pb, pc, all).map(_.toFile.exists) mustEqual(Seq(false, false, false, true))
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