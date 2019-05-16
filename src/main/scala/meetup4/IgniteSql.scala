package meetup4

import meetup.modelos.Anuncio
import org.apache.ignite.{IgniteCache, Ignition}

class IgniteSql {

  val ignite = Ignition.start()


  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache("anuncios")

}
