package akka.dinesh

import akka.actor._

// Using constructorto Input a parameter
class HelloActor(message: String) extends Actor {
  def receive = {
    // display the message.
    case "hello" =>
      println(s"Hello Buddy, This is $message")
    case _ => println(s"And You?, said $message")
  }
}
object FirstAkka extends App {
  val system = ActorSystem("HelloSystem")
  // Using props. --> create and start the actor
  val helloActor =
    system.actorOf(Props(new HelloActor("Dinesh A")), name = "helloactor")
  helloActor ! "hello"
  helloActor ! "Good Morning"
}
