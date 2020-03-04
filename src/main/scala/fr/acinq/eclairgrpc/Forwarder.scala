package fr.acinq.eclairgrpc

import akka.actor.ActorRef
import io.grpc.stub.StreamObserver

trait Forwarder[T, U] {

  val destActor: ActorRef

  def bind(inStream: StreamObserver[T]): StreamObserver[U] = {
    val outStream = new StreamObserver[U] {
      override def onNext(v: U): Unit = {
        destActor ! v
      }

      override def onError(throwable: Throwable): Unit = ???

      override def onCompleted(): Unit = {
        inStream.onCompleted()
      }
    }
    destActor ! RegisterStream(outStream)
    outStream
  }

}

case class RegisterStream[T](outStream: StreamObserver[T])