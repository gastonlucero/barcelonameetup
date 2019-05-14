package meetup2

import java.text.SimpleDateFormat
import java.util.Date

import meetup.modelos.Anuncio
import org.apache.ignite.{IgniteCache, Ignition}

object IgniteNodo2 extends App {

  val ignite = Ignition.start()
  val cacheAnuncios: IgniteCache[String, Anuncio] = ignite.getOrCreateCache("anuncios")
  val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  val anuncio = Anuncio(
    fecha = format.format(new Date()),
    id = "1",
    texto = "anuncio 1",
    pais = "es",
    vertical = 1)

  val anuncio2 = Anuncio(
    fecha = format.format(new Date()),
    id = "2",
    texto = "anuncio 2",
    pais = "es",
    vertical = 2)

  val anuncio3 = Anuncio(
    fecha = format.format(new Date()),
    id = "3",
    texto = "anuncio 3",
    pais = "it",
    vertical = 2)

  println(cacheAnuncios.get(anuncio.id))
  println(cacheAnuncios.get(anuncio2.id))
  println(cacheAnuncios.get(anuncio3.id))

}