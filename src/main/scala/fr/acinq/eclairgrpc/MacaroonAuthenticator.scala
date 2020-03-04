package fr.acinq.eclairgrpc

import com.github.nitram509.jmacaroons.{MacaroonsBuilder, MacaroonsVerifier}
import grizzled.slf4j.Logging
import io.grpc._

import scala.collection.JavaConverters._
import scala.util.Try


class MacaroonAuthenticator(adminMacaroon: String) extends ServerInterceptor with Logging {

  def getAsciiMetadata(key: String): Metadata.Key[String] = Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)
  val MACAROON_KEY: Metadata.Key[String] = getAsciiMetadata("macaroon")

  override def interceptCall[ReqT, RespT](serverCall: ServerCall[ReqT, RespT], metadata: Metadata, serverCallHandler: ServerCallHandler[ReqT, RespT]): ServerCall.Listener[ReqT] = {
    Try {
      val clientMacaroon = MacaroonsBuilder.deserialize(metadata.get(MACAROON_KEY))
      val verifier = new MacaroonsVerifier(clientMacaroon)
      logger.info(s"clientMacaroon: ${clientMacaroon.inspect()}")
      logger.info(s"macaroon okay: ${verifier.isValid(adminMacaroon)}")
    }

    logger.info(s"METADATA:")
    metadata.keys().asScala.foreach { key =>
      logger.info(s"$key: ${metadata.get(getAsciiMetadata(key))}")
    }

    // continue
    serverCallHandler.startCall(serverCall, metadata)
  }
}
