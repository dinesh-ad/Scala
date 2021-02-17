/* Comment and conclude */

/*By default when actors throw an exception they are restarted. Since supervision strategy is applied from parent to child, the TestParent exists to enforce the Stop directive on the children. Your original code would not work for this reason.
If you want top level actors (Those launched with system.actorOf) to stop on an exception, you could set the configuration property akka.actor.guardian-supervisor-strategy = "akka.actor.StoppingSupervisorStrategy" but in my example I prefer to use a parent 
actor since actor hierarchies are a normal way to organise supervision in Akka.
*/

import akka.actor._
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.pattern.ask
import akka.util.Timeout
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}

object test_Message{
  case object MessageSuccess
  case object MessageFailure
  case class create(name:String)
}

class Supervisors extends Actor {
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._
  import scala.concurrent.{Await, ExecutionContext, Future}
  import test_Message._

//It will not restart the child actor if its enabled so we lose our child (dead letters)
//override def supervisorStrategy: SupervisorStrategy =  SupervisorStrategy.stoppingStrategy

def createChilds(name:String)={var childs=context.actorOf(Props(new Childs),name)
                               context.watch(childs)
                                childs}

  def receive = {
    case p: Props => sender() ! context.actorOf(p)
    case create(name)=> sender() !createChilds(name)
                        
  }
}

class Childs extends Actor {
  import test_Message._
  var x=30
  val tab=Array(1,2,3)
  println("Constructor !!!!")
  override def preStart(){    // overriding preStart method
          println("preStart method is called "+self.path.name);
          }
override def postStop { println("postStop method is called "+self.path.name) }

override def preRestart(reason: Throwable, message: Option[Any]) {
    println("preRestart method is called********************* "+self.path.name)
     }

override def postRestart(reason: Throwable) {
    println("preRestart method is called "+self.path.name)
    super.postRestart(reason)
  }
 		  
		  

  def receive = {
       case MessageFailure => 
           throw new ArithmeticException 
          println("Failure")
       
  
    case MessageSuccess => 
       println("Success with: "+self.path.name)
  }
}

object FaultTolerant  extends App{
  import test_Message._
implicit val timeout:Timeout =  2 seconds
val system = ActorSystem("Master")
val chefMaster = system.actorOf(Props(new Supervisors), "master")
val chefMaster1 = system.actorOf(Props(new Supervisors), "master1")

val future2: Future[ActorRef] = chefMaster.ask(create("Child_11")).mapTo[ActorRef]
val childs11 = Await.result(future2, 5 second)

val future3: Future[ActorRef] = chefMaster.ask(create("Child_12")).mapTo[ActorRef]
val childs12 = Await.result(future3, 5 second)

val future4: Future[ActorRef] = chefMaster1.ask(create("Child_21")).mapTo[ActorRef]
val childs21 = Await.result(future4, 5 second)
childs11 ! MessageSuccess
childs12 ! MessageSuccess
childs21 ! MessageSuccess
childs11 ! MessageFailure
Thread.sleep(2000)
childs11 ! MessageSuccess
childs12 ! MessageSuccess
childs21 ! MessageSuccess
system.scheduler.scheduleOnce(15.seconds)(system.shutdown())
}
