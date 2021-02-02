/*By default when actors throw an exception they are restarted. Since supervision strategy is applied from parent to child, the TestParent exists to enforce the Stop directive on the children. Your original code would not work for this reason.
 
If you want top level actors (Those launched with system.actorOf) to stop on an exception, you could set the configuration property akka.actor.guardian-supervisor-strategy = "akka.actor.StoppingSupervisorStrategy" but in my example I prefer to use a parent 
actor since actor hierarchies are a normal way to organise supervision in Akka.
*/
import akka.actor._
import akka.actor.{Actor, ActorRef, Props, ActorSystem,PoisonPill}
import scala.concurrent.{Await,ExecutionContext,Future}
import akka.pattern.ask
import akka.util.Timeout
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}
 

object test{
  case object MessageSuccess
  case object MessageFailure
  case class create(name:String)
}
 

class Supervisor extends Actor{
import test._
 
 
import akka.actor.OneForOneStrategy
 import akka.actor.SupervisorStrategy.{Stop, Resume, Restart, Escalate}
  import scala.concurrent.duration._
 
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute){
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception                => Escalate
   }
 
def createChild(name:String)={var child=context.actorOf(Props(new Child),name)
                               context.watch(child)
                                child}
 

  def receive ={
    case p: Props => sender() ! context.actorOf(p)
    case create(name)=> sender() !createChild(name)

 }
}
 
 
class Child extends Actor{
  import test._
  var x=30
val tab=Array(1,2,3)  
println("Constructor !!!!")
  override def preStart(){   // overriding preStart method
          println("preStart method is called "+self.path.name);
        }
override def postStop{ println(" postStop method is called "+self.path.name)}
 
override def preRestart(reason: Throwable, message: Option[Any]){
    println("preRestart method is called "+self.path.name)
    }
 
override def postRestart(reason: Throwable){
    println("preRestart method is called "+self.path.name)
    super.postRestart(reason)
 }

 
  def receive ={

       case MessageFailure => 
          // try the other exceptions
           throw new Exception//Exception//IllegalArgumentException//ArithmeticException//NullPointerException  
          println("Failure")

    case MessageSuccess => 
       println("Success with: "+self.path.name)
 }
}

object CustomStrategy  extends App{
  import test._
implicit val timeout:Timeout =  2 seconds
val system = ActorSystem("Master")
val chefMaster = system.actorOf(Props(new Supervisor), "Master")
val chefMaster1 = system.actorOf(Props(new Supervisor), "Master-1")
val future2: Future[ActorRef] = chefMaster.ask(create("Child-11")).mapTo[ActorRef]
val child11 = Await.result(future2, 5 second)
 
 val future3: Future[ActorRef] = chefMaster.ask(create("Child-12")).mapTo[ActorRef]
val child12 = Await.result(future3, 5 second)
 
 val future4: Future[ActorRef] = chefMaster1.ask(create("Child-21")).mapTo[ActorRef]
val child21 = Await.result(future4, 5 second)
child11 ! MessageSuccess
child12 ! MessageSuccess
child21 ! MessageSuccess
child11 ! MessageFailure
Thread.sleep(2000)
child11 ! MessageSuccess
child12 ! MessageSuccess
child21 ! MessageSuccess
system.scheduler.scheduleOnce(15.seconds)(system.shutdown())
}