package meetup6

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import meetup5.AnuncioSql
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder
import org.apache.ignite.{IgniteCache, Ignition}

object MeetupServer extends App with MeetupRoutes {

  val CACHE_NAME = "anunciosHttp"

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val spi = new TcpDiscoverySpi
  val spiMulticast = new TcpDiscoveryMulticastIpFinder
  spiMulticast.setMulticastGroup("228.10.10.170")
  spi.setIpFinder(spiMulticast)
  val cfg = new IgniteConfiguration()
  cfg.setDiscoverySpi(spi)
  cfg.setFailureDetectionTimeout(10000) //Valor por defecto
  val cacheCfg = new CacheConfiguration[String, AnuncioSql](CACHE_NAME)
  cacheCfg.setIndexedTypes(Seq(classOf[String], classOf[AnuncioIgnite]): _*)
  cfg.setCacheConfiguration(cacheCfg)

  val ignite = Ignition.start(cfg)

  val cacheAnuncios: IgniteCache[String, AnuncioIgnite] = ignite.getOrCreateCache(CACHE_NAME)

  val adsActor = system.actorOf(AdsActor.props(cacheAnuncios))

  //Http routes
  lazy val routes: Route = meetupRoutes

  val serverBindingFuture = Http().bindAndHandle(routes, "localhost", 9000)

}
