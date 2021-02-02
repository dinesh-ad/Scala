//package com.dinesh

import akka.actor.{Actor, ActorRef, Props, ActorSystem,PoisonPill}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
//import akka.actor.SuppressedDeadLetter


object Ad_Dining extends App{

// import Dining_Philosopher._
// Here actor is System.
val system = ActorSystem("Dinner")

// Here actor is supervisor.
val supervisor = system.actorOf(Props(new Dining_Supervisor(5)))

println("Let's Start Dining")

//var my_Philosophers = List("Aristoteles", "Plato", "Decartes", "Kant", "Nitzsche") //The Philosophers 
supervisor ! Disable
//supervisor ! "End Dining"
//Thread.sleep(900)
system.scheduler.scheduleOnce(15.seconds)(system.shutdown())
//system.terminate()
}