package fr.acinq.eclairgrpc

import com.github.nitram509.jmacaroons.{MacaroonsBuilder, MacaroonsVerifier}
import grizzled.slf4j.Logging
import io.grpc._

import scala.util.{Failure, Success, Try}

class MacaroonAuthenticator(adminMacaroon: String) extends ServerInterceptor with Logging {

  val MACAROON_KEY: Metadata.Key[String] = Metadata.Key.of("macaroon", Metadata.ASCII_STRING_MARSHALLER)

  override def interceptCall[ReqT, RespT](serverCall: ServerCall[ReqT, RespT], metadata: Metadata, serverCallHandler: ServerCallHandler[ReqT, RespT]): ServerCall.Listener[ReqT] = {
    Try {
      val clientMacaroon = MacaroonsBuilder.deserialize(metadata.get(MACAROON_KEY))
      val verifier = new MacaroonsVerifier(clientMacaroon)
      verifier.isValid(adminMacaroon)
    } match {
      case Success(_) =>
        serverCallHandler.startCall(serverCall, metadata)
      case Failure(ex) =>
        logger.error("Macaroon failed:", ex)
        throw ex
    }
  }
}
