package fr.acinq

import java.io.File

import com.typesafe.config.Config
import fr.acinq.eclairgrpc.grpc.{GreeterGrpc, HelloReply, HelloRequest}
import io.grpc.stub.StreamObserver

package object eclairgrpc {

  case class GrpcConfig(
     host: String,
     port: Int,
     certFile: File,
     keyFile: File,
     macaroon: String
  )

  def fromConf(conf: Config): GrpcConfig = GrpcConfig(
    host = conf.getString("api.binding-ip"),
    port = conf.getInt("grpc.port"),
    certFile = new File(conf.getString("grpc.cert")),
    keyFile = new File(conf.getString("grpc.key")),
    macaroon = conf.getString("grpc.adminmacaroon")
  )

  class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    override def sayHello(request: HelloRequest, responseObserver: StreamObserver[HelloReply]): Unit = {
      val reply = HelloReply.newBuilder().setMessage(s"Hey hey hey ${request.getName}!").build()
      responseObserver.onNext(reply)
      responseObserver.onCompleted()
    }
  }


}
