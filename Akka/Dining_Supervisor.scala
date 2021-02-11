//package com.dinesh
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import akka.actor.{Actor, ActorRef, Props, ActorSystem,PoisonPill}

object Disable
case class Request_Eating(id:Int) 
case class Finish_Eating(id:Int)
case object Init
case object AttackFood

class Dining_Supervisor(nb:Int) extends Actor {
    import context._
    var forks:Array[Int]=Array.fill(nb) {0}
    var philoshe:Array[ActorRef]=Array.fill(nb) {self}

    override def preStart ={
      for (i<-0 to nb-1) //nb is philosophers.
             philoshe(i) = context.actorOf(Props(new Dining_Philosopher(i,self)),"Philosopher"+i)
        for (i<-0 to nb-1)
                philoshe(i) !Init
        }

    def left(id:Int)={if (id==0) nb-1 else id-1}
    def right(id:Int)={if (id==nb-1) 0 else id+1}

    def receive={
        case Request_Eating(id)=>
            var p=left(id)
            var n=right(id)

            if (forks(p)!=1 && forks(n)!=1) {
                forks(id)=1
                sender !AttackFood
                }
            else {
                forks(id)=(-1)
            }
            
        case Finish_Eating(id) => forks(id)=2   
                var p=left(id)
                var n=right(id)

        if (forks(p)==(-1) && forks(left(p))!=(1))
        { 
            forks(p)=1
            philoshe(p) !AttackFood}

        if (forks(n)==(-1) && forks(right(n))!=1){
            forks(n)=1
            philoshe(n) !AttackFood}
        }
}