package meetup6

import akka.actor.{Actor, Props}
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.SqlQuery

import scala.collection.JavaConverters._

class AdsActor(igniteCache: IgniteCache[String, AnuncioIgnite]) extends Actor {

  import AdsActor._

  override def preStart(): Unit = {
    println(s"Cache ${igniteCache.getName}")
  }

  override def receive: Receive = {
    case GetAnuncios => {
      sender() ! igniteCache.iterator().asScala.map(_.getValue).toSeq
    }
    case anuncio: GetAnunciosByPais => {
      val sqlText = s"pais = '${anuncio.pais}'"
      val sql = new SqlQuery[String, AnuncioIgnite](classOf[AnuncioIgnite], sqlText)
      sender() ! Option(igniteCache.query(sql).getAll.asScala.map(_.getValue))
    }
    case SaveAnuncio(anuncio) => {
      igniteCache.putIfAbsent(anuncio.id, anuncio)
    }
    case BorrarAnuncio(id) => {
      sender() ! igniteCache.remove(id)
    }
  }

}


object AdsActor {

  def props(igniteCache: IgniteCache[String, AnuncioIgnite]): Props = Props(classOf[AdsActor], igniteCache)

  case object GetAnuncios

  case class GetAnunciosByPais(pais: String)

  case class SaveAnuncio(anuncio: AnuncioIgnite)

  case class BorrarAnuncio(id: String)

}

