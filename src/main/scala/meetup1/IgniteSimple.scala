package meetup1

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.{Ignite, IgniteCache, Ignition}

object IgniteSimple extends App {

  val ignite: Ignite = Ignition.start()
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache(CACHE_NAME)

  cacheAnuncios.put(anuncio1.id, anuncio1)
  cacheAnuncios.put(anuncio2.id, anuncio2)
  cacheAnuncios.put(anuncio3.id, anuncio3)

  println(s"Anuncion2 ${cacheAnuncios.get(anuncio2.id)}")
  println(s"Eliminado anuncio2 = ${cacheAnuncios.remove(anuncio2.id)}")
  println(s"Anuncio2 ${cacheAnuncios.get(anuncio2.id)}")
}
