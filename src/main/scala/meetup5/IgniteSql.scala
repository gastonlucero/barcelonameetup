package meetup5

import java.text.SimpleDateFormat
import java.util.Date

import javax.cache.Cache
import meetup0.Utils._
import org.apache.ignite.cache.query.annotations.QuerySqlField
import org.apache.ignite.cache.query.{QueryCursor, ScanQuery, SqlQuery}
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.lang.IgniteBiPredicate
import org.apache.ignite.{IgniteCache, Ignition}

import scala.annotation.meta.field


//Para utilizar SqlQuery, la case class debe tener las anotaciones

case class AnuncioSql(@(QuerySqlField@field) fecha: String,
                      @(QuerySqlField@field)(index = true) id: String,
                      @(QuerySqlField@field) texto: String,
                      @(QuerySqlField@field)(index = true) pais: String,
                      @(QuerySqlField@field)(index = true) vertical: Int)

object IgniteSql extends App {

  val igniteConfig = new IgniteConfiguration()
  val cacheCfg = new CacheConfiguration[String, AnuncioSql](CACHE_NAME)

  //Sin esto no funcionan las queries SqlQuery
  cacheCfg.setIndexedTypes(Seq(classOf[String], classOf[AnuncioSql]): _*)

  igniteConfig.setCacheConfiguration(cacheCfg)
  val ignite = Ignition.start(igniteConfig)
  val cacheConSql: IgniteCache[String, AnuncioSql] = ignite.getOrCreateCache(cacheCfg)
  
  val anuncioSql1 = AnuncioSql(
    fecha = format.format(new Date()),
    id = "1",
    texto = "anuncio 1",
    pais = "es",
    vertical = 1)

  val anuncioSql2 = AnuncioSql(
    fecha = format.format(new Date()),
    id = "2",
    texto = "anuncio 2",
    pais = "es",
    vertical = 2)

  val anuncioSql3 = AnuncioSql(
    fecha = format.format(new Date()),
    id = "3",
    texto = "anuncio 3",
    pais = "it",
    vertical = 2)

  cacheConSql.put(anuncioSql1.id, anuncioSql1)
  cacheConSql.put(anuncioSql2.id, anuncioSql2)
  cacheConSql.put(anuncioSql3.id, anuncioSql3)

  println("***** Queries con Sql *****")
  // Anotaciones y registrar la clase en las configs
  val sqlText = s"pais = 'es'"
  val sql = new SqlQuery[String, AnuncioSql](classOf[AnuncioSql], sqlText)
  cacheConSql.query(sql).printAsScala()


  println("***** Queries con Predicado *****")
  val cursor = cacheConSql.query(
    new ScanQuery(new IgniteBiPredicate[String, AnuncioSql] {
      override def apply(key: String, entryValue: AnuncioSql): Boolean = entryValue.pais == "it" && entryValue.vertical == 2
    }))
  cursor.printAsScala()

  ignite.close()
  System.exit(1)

  implicit class CacheIterator[String, AnuncioSql](query: QueryCursor[Cache.Entry[String, AnuncioSql]]) {

    import scala.collection.JavaConverters._

    def printAsScala() = query.getAll.asScala.map(_.getValue).foreach(println)
  }

}
