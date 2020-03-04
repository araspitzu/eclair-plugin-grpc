package fr.acinq.eclairgrpc

import akka.util.Timeout
import fr.acinq.bitcoin.ByteVector32
import fr.acinq.eclair.Eclair
import fr.acinq.eclairgrpc.grpc.{Chain, GetInfoRequest, GetInfoResponse, LightningGrpc, Lnrpc}
import grizzled.slf4j.Logging
import io.grpc.stub.StreamObserver

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class LightningService(eclair: Eclair)(implicit ec: ExecutionContext) extends LightningGrpc.LightningImplBase with Logging {

  implicit val timeout = Timeout(30 seconds)

  override def getInfo(request: GetInfoRequest, responseObserver: StreamObserver[GetInfoResponse]): Unit = {
    eclair.getInfoResponse().map { info =>
      val lndGetInfoResponse = GetInfoResponse.newBuilder()
        .setAlias(info.alias)
        .setBlockHash(ByteVector32.Zeroes.toString())
        .setBestHeaderTimestamp(123445555)
        .setBlockHeight(info.blockHeight)
        .setIdentityPubkey(info.nodeId.toString())
        .setNumActiveChannels(0)
        .setNumPeers(1)
        .setNumPendingChannels(0)
        .setSyncedToChain(true)
        .setSyncedToGraph(true)
        .setVersion("eclair")
        .build()

      responseObserver.onNext(lndGetInfoResponse)
      responseObserver.onCompleted()
    }
  }


}
