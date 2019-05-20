package meetup2

import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder

trait IgniteTcpDiscovery {

  //SPI = Service Provider Interface

  def tcpSpiConfig() = {
    val spi = new TcpDiscoverySpi
    val spiMulticast = new TcpDiscoveryMulticastIpFinder
    spiMulticast.setMulticastGroup("228.10.10.170")
    spi.setIpFinder(spiMulticast)
    spi
  }

}