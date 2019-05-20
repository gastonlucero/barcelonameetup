package meetup2

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.{IgniteCache, Ignition}

object IgniteDefaultDiscoveryNodo1 extends App {

  //Discovery entre los nodos por defecto (TcpDiscoverySpi)
  val ignite = Ignition.start()
  val cacheCfg = new CacheConfiguration[String, Anuncio](CACHE_NAME)
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache(cacheCfg)

  cacheAnuncios.put(anuncio1.id, anuncio1)
  cacheAnuncios.put(anuncio2.id, anuncio2)
  cacheAnuncios.put(anuncio3.id, anuncio3)


}