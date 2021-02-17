import akka.actor.{Actor,ActorSystem,Props}
import akka.pattern._
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
import scala.io.StdIn._
import akka.event.Logging


class MyActor extends Actor {
  val log = Logging(context.system, this)
  override def preStart() = {
    log.debug("Starting????????????????????????????????")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }
  def receive = {
    case "test" => log.info("Received test")
    case _     => log.warning("Received unknown messag!!!!!e: {}")
  }
}

object Logging_Test extends App{
println("Starting *************************************")
 var actorSystem = ActorSystem("ActorSystem")
  var actor = actorSystem.actorOf(Props[MyActor],"PropExample")  
    actor ! "Hello from#######################" 
    actor ! "test"

}
