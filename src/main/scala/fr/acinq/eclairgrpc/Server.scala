package fr.acinq.eclairgrpc


import fr.acinq.eclair.Eclair
import grizzled.slf4j.Logging
import io.grpc.ServerBuilder

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
