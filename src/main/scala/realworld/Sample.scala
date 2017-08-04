package realworld

trait Sample {
  def m: String
}

class SampleImpl extends Sample {
  override def m: String = "impl"
}

case class Runner(sample: Sample)

trait Module {

  import com.softwaremill.macwire._

  val sample: Sample = wire[SampleImpl]

  val runner: Runner = wire[Runner]
}

object Main extends App with Module {

  println(runner.sample.m)

}