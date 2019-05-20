package meetup2

import java.util.Date

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.{IgniteCache, Ignition}


object IgniteTcpDiscovery1 extends App with IgniteTcpDiscovery {

  //Discovery entre los especificando explicitamente TcpDiscoverySpi cambiando la ip de descubrimiento

  val cfg = new IgniteConfiguration()
  cfg.setDiscoverySpi(tcpSpiConfig)
  cfg.setFailureDetectionTimeout(10000) //Valor por defecto

  val ignite = Ignition.start(cfg)
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache(CACHE_NAME)

  val anuncioMulticast = Anuncio(
    fecha = format.format(new Date()),
    id = "10",
    texto = "multicast",
    pais = "es",
    vertical = 10)

  cacheAnuncios.put(anuncioMulticast.id, anuncioMulticast)
}
