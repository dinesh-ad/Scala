//package com.dinesh

import akka.actor.{Actor, ActorRef, Props, ActorSystem,PoisonPill}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class Dining_Philosopher(id:Int,supervisor:ActorRef) extends Actor{
    import context._

    def receive={
        case Init => Thread.sleep(Random.nextInt(900))
        supervisor !Request_Eating(id) //Requesting with the ID.
        become(Waiting)
    }

    def Waiting:Receive ={
        case AttackFood => //The supervisor gave permission to eat to philosopher.
        println("The Philosopher: "+id+" Eating." )
        Thread.sleep(Random.nextInt(900))
        supervisor !Finish_Eating(id)
        println("The Philosopher: "+id+" Finished." )
        self !PoisonPill //Self is Current Philosopher. ~ to kill themselves.
    }
}