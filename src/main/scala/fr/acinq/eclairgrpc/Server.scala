package fr.acinq.eclairgrpc

import java.io.File

import fr.acinq.eclair.Eclair
import fr.acinq.eclairgrpc.Server.GreeterImpl
import fr.acinq.eclairgrpc.grpc.{GreeterGrpc, HelloReply, HelloRequest}
import grizzled.slf4j.Logging
import io.grpc.{ManagedChannelBuilder, ServerBuilder}
import io.grpc.stub.StreamObserver

import scala.concurrent.ExecutionContext

class Server(eclair: Eclair, grpcConfig: GrpcConfig)(implicit ec: ExecutionContext) extends Logging {

  val server = ServerBuilder
    .forPort(grpcConfig.port)
    .intercept(new MacaroonAuthenticator(grpcConfig.macaroon))
    .useTransportSecurity(grpcConfig.certFile, grpcConfig.keyFile)
    .addService(new LightningService(eclair))
    .addService(new GreeterImpl)
    .build()

  def start() = server.start()

}

object Server {

  class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    override def sayHello(request: HelloRequest, responseObserver: StreamObserver[HelloReply]): Unit = {
      val reply = HelloReply.newBuilder().setMessage(s"Hey hey hey ${request.getName}!").build()
      responseObserver.onNext(reply)
      responseObserver.onCompleted()
    }
  }
}
