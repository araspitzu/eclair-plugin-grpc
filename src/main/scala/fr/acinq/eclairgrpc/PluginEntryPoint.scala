package fr.acinq.eclairgrpc

import com.typesafe.config.Config
import fr.acinq.eclair.{EclairImpl, Kit, Plugin, Setup}
import grizzled.slf4j.Logging

class PluginEntryPoint extends Plugin with Logging {

  var config: Config = null

  override def onSetup(setup: Setup): Unit = {
    logger.info(s"got onSetup")
    config = setup.config
  }

  override def onKit(kit: Kit): Unit = {
    logger.info(s"starting grpc API")
    val grpcConfig = fromConf(config)
    val eclair = new EclairImpl(kit)
    implicit val ec = kit.system.dispatcher
    val server = new Server(eclair, grpcConfig)
    server.start()
    logger.info(s"GRPC API started on port ${grpcConfig.port}")
  }

}
