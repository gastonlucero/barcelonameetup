package meetup0

import java.text.SimpleDateFormat
import java.util.Date

case class Anuncio(fecha: String, id: String, texto: String, pais: String, vertical: Int)

object Utils {

  val CACHE_NAME = "anuncios"

  val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  val anuncio1 = Anuncio(
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

}