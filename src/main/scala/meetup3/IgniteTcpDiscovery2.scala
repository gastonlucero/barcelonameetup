package meetup3

package meetup3

import java.text.SimpleDateFormat

import meetup.modelos.Anuncio
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.{IgniteCache, Ignition}

object IgniteTcpDiscovery2 extends App {


  //TcpDiscoverySpi es la implementacion por defecto
  //Discovery SPI can be configured for Multicast and Static IP based node discovery
  val spi = new TcpDiscoverySpi
  val spiMulticast = new TcpDiscoveryMulticastIpFinder
  spiMulticast.setMulticastGroup("228.10.10.170")
  spi.setIpFinder(spiMulticast)

  val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val cfg = new IgniteConfiguration()
  cfg.setDiscoverySpi(spi)
  val ignite = Ignition.start(cfg)
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache("anuncios")

  println(cacheAnuncios.get("1"))
}