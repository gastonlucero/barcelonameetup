package meetup2

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.{IgniteCache, Ignition}

object IgniteDefaultDiscoveryNodo2 extends App {

  //Discovery entre los nodos por defecto
  val ignite = Ignition.start()
  val cacheCfg = new CacheConfiguration[String, Anuncio]("cacheAnuncios")
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache(CACHE_NAME)

  println(s"Anuncio 1 = ${cacheAnuncios.get("1")}")
  println(s"Anuncio 2 = ${cacheAnuncios.get("2")}")
  println(s"Anuncio 3 = ${cacheAnuncios.get("3")}")

}