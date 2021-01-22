import akka.actor._;
class ActorHello extends Actor {
  def receive = {
    case msg: String => println("I receive message " + msg)
    case _           => println("Other thing !!!!")
  }
}
object Main extends App {
//def main(args:Array[String]){
  println("Hello !!!")
  var actorSystem = ActorSystem("ActorSystem");
  var actor = actorSystem.actorOf(Props[ActorHello], "ActorHello");
  actor ! "Bonjour Actor"
  actor ! 123
  println("Bye")
  Thread.sleep(9000)
  actorSystem.terminate
//}
}
