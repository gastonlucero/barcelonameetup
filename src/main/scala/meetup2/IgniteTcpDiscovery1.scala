package meetup2

import java.util.Date

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.{IgniteCache, Ignition}

//SPI = Service Provider Interface
object IgniteTcpDiscovery1 extends App {

  //TcpDiscoverySpi es la implementacion por defecto
  //Discovery SPI can be configured for Multicast and Static IP based node discovery
  val spi = new TcpDiscoverySpi
  val spiMulticast = new TcpDiscoveryMulticastIpFinder
  spiMulticast.setMulticastGroup("228.10.10.170")
  spi.setIpFinder(spiMulticast)

  val cfg = new IgniteConfiguration()
  cfg.setDiscoverySpi(spi)
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
