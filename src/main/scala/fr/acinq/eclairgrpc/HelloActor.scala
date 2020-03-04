package fr.acinq.eclairgrpc

import akka.actor.{Actor, ActorRef, PoisonPill}
import com.google.protobuf.Descriptors.{Descriptor, FieldDescriptor}
import fr.acinq.eclairgrpc.grpc.{HelloReply, HelloRequest, StreamGreeterGrpc}
import grizzled.slf4j.Logging
import io.grpc.stub.StreamObserver

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Random

class HelloActor extends Actor with Logging {

  implicit val ec: ExecutionContext = context.dispatcher
  val names = "Andrea" :: "Satoshi":: "Hal" :: Nil
  var outStream: StreamObserver[HelloReply] = null

  override def receive: Receive = {
    case r:RegisterStream[_] =>
      logger.info(s"Registering helloreply stream")
      outStream = r.outStream.asInstanceOf[StreamObserver[HelloReply]]

    case s: String =>
      if (outStream != null){
        logger.info(s"sending out a salutation reply")
        val reply = HelloReply.newBuilder().setMessage(s"Hey $s").build()
        outStream.onNext(reply)
      } else {
        logger.info("outStream == null")
      }
  }

  context.system.scheduler.schedule(initialDelay = 5 seconds, 3 seconds) {
    self ! Random.shuffle(names).head
  }


}
class StreamGreeterImpl(val destActor: ActorRef) extends StreamGreeterGrpc.StreamGreeterImplBase with Forwarder[HelloReply, HelloRequest] {

  override def streamHellos(responseObserver: StreamObserver[HelloReply]): StreamObserver[HelloRequest] = bind(responseObserver)

}