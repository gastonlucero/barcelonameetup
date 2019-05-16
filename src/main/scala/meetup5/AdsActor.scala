package meetup5

import akka.actor.{Actor, Props}
import meetup.modelos.Anuncio
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.{ScanQuery, SqlQuery}

import scala.collection.JavaConverters._

class AdsActor(igniteCache: IgniteCache[String, Anuncio]) extends Actor {

  import AdsActor._

  override def preStart(): Unit = {
    println(s"Cache ${igniteCache.getName}")
  }

  override def receive: Receive = {
    case GetAnuncios => {
      sender() ! igniteCache.iterator().asScala.map(_.getValue).toSeq
    }
    case anuncio: GetAnunciosByPais => {
      val cursor = igniteCache.query(new ScanQuery[String, Anuncio]((key, entryValue) => entryValue.pais == anuncio.pais))
      val anuncios = cursor.getAll
      val resultado = if (!anuncios.isEmpty) {
        Some(anuncios.asScala.map(_.getValue))
      } else None
      sender() ! resultado

      val sqlText = s"pais = '${anuncio.pais}'"
      val sql = new SqlQuery[String, Anuncio](classOf[Anuncio], sqlText)
      sender() ! Some(igniteCache.query(sql).getAll.asScala.map(_.getValue))
    }
    case SaveAnuncio(anuncio) => {
      igniteCache.putIfAbsent(anuncio.id, anuncio)
    }
    case BorrarAnuncio(id) => {
      igniteCache.remove(id)
      sender() ! id
    }
  }

}


object AdsActor {

  def props(igniteCache: IgniteCache[String, Anuncio]): Props = Props(classOf[Anuncio], igniteCache)

  case object GetAnuncios

  case class GetAnunciosByPais(pais: String)

  case class SaveAnuncio(anuncio: Anuncio)

  case class BorrarAnuncio(id: String)

}

