# File Backed Logs

A simple Scala API for working with line delimited log files. The common
use case here is when an app needs to record what it is doing locally in
a simple, thread safe manner. It is often overkill to have to spin up
an entire database and make up a bunch of serialization logic -- 
[Slick](http://slick.lightbend.com/) for example.

See [build.sbt](build.sbt) for dependencies. They are minimal. Make up some case 
classes and go. Data saved is in an intuitive [CSV format](https://github.com/jfalkner/cc2csv)
that can be edited by hand or in programs such as Excel, if needed.

## Usage

Add the version tag of this repo directly in SBT.

```
lazy val p = RootProject(uri("https://github.com/jfalkner/file_backed_logs.git#v0.0.1"))
lazy val root = project in file(".") dependsOn p
```

Use the `Logs` trait and make some custom logs based on a prefix.

```
object Main extends Logs {
  // where log files are saved
  override val logsPath = Paths.get("/home/my/app/logs")
  
  // a logger that auto-serializes Foo and saves data in the .foo.csv postfix
  val foo = make[Foo](".foo.csv")
  // similar to above. reuse case class but save to a different postfix
  val bar = make[Foo](".bar.csv")


  // persist data as needed
  foo.log(Foo("One", "1", Instant.now())
  foo.log(Foo("Two", "2", Instant.now())
  // above could have saved several instances at once with logAll()

  // read data back in to memory when needed
  foo.load().foreach(println)
  //  Set[Foo](
  //    Foo(One,1,<ts>)
  //    Foo(Two,2,<ts>)
  //  )
  
  // periodically call squash() to reduce logs files to one with all unique entries
  foo.squash()
}

case class Foo(a: String, b: Int, c: Instant)
```