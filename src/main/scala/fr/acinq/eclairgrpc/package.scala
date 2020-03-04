package fr.acinq

import java.io.File

import com.typesafe.config.Config

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


}
