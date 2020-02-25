package queries

import org.apache.ignite.cache.CacheMode
import org.apache.ignite.cache.affinity.AffinityKey
import org.apache.ignite.cache.query.annotations.QuerySqlField
import org.apache.ignite.cache.query.{SqlFieldsQuery, SqlQuery}
import org.apache.ignite.configuration.CacheConfiguration
import org.apache.ignite.{IgniteCache, Ignition}

import scala.annotation.meta.field
import scala.io.StdIn

case class Pais(@(QuerySqlField@field)(index = true) codigo: String,
                @(QuerySqlField@field)(index = true) nombre: String)

case class Ciudad(affinityKey: AffinityKey[String],
                  @(QuerySqlField@field)(index = true) pais: String,
                  @(QuerySqlField@field)(index = true) nombre: String,
                  @(QuerySqlField@field)(index = true) codigo: String,
                  @(QuerySqlField@field) poblacion: Long)

object InnodataIgnite extends App {


  val server = args(0)

  if (server == "server") {
    val ignite = Ignition.start()
    val cacheCiudadCfg = new CacheConfiguration[AffinityKey[String], Ciudad]("ciudadCache")
    cacheCiudadCfg.setCacheMode(CacheMode.PARTITIONED)
    cacheCiudadCfg.setBackups(1)
    cacheCiudadCfg.setIndexedTypes(Seq(classOf[AffinityKey[String]], classOf[Ciudad]): _*)
    val cacheCiudadSql: IgniteCache[AffinityKey[String], Ciudad] = ignite.getOrCreateCache(cacheCiudadCfg)

    val cachePaisCfg = new CacheConfiguration[String, Pais]("paisCache")
    cachePaisCfg.setIndexedTypes(Seq(classOf[String], classOf[Pais]): _*)
    cachePaisCfg.setCacheMode(CacheMode.PARTITIONED)
    cachePaisCfg.setBackups(1)
    val cachePaisSql: IgniteCache[String, Pais] = ignite.getOrCreateCache(cachePaisCfg)
    val pais = args(1)
    if (pais == "es") {
      val sevillaKey = new AffinityKey("sev", "es")
      val valenciaKey = new AffinityKey("val", "es")
      val zaragozaKey = new AffinityKey("zar", "es")
      val bilbaoKey = new AffinityKey("bil", "es")
      val España = Pais("es", "España")
      val Sevilla = Ciudad(sevillaKey, "es", "sev", "Sevilla", 688711)
      val Valencia = Ciudad(valenciaKey, "es", "val", "Valencia", 791413)
      val Zaragoza = Ciudad(zaragozaKey, "es", "zar", "Zaragoza", 666880)
      val Bilbao = Ciudad(bilbaoKey, "es", "bil", "Bilbao", 345821)

      cachePaisSql.put("es", España)
      cacheCiudadSql.put(Sevilla.affinityKey, Sevilla)
      cacheCiudadSql.put(Valencia.affinityKey, Valencia)
      cacheCiudadSql.put(Zaragoza.affinityKey, Zaragoza)
      cacheCiudadSql.put(Bilbao.affinityKey, Bilbao)
      println("Cargados datos de España en la caches")
    }
    else {
      val lisboaKey = new AffinityKey("lis", "pt")
      val oportoKey = new AffinityKey("opo", "pt")
      val coimbraKey = new AffinityKey("opo", "pt")
      val Portugal = Pais("pt", "Portugal")
      val Lisboa = Ciudad(lisboaKey, "pt", "lis", "Lisboa", 517802)
      val Oporto = Ciudad(oportoKey, "pt", "opo", "Oporto", 249633)
      val Coimbra = Ciudad(coimbraKey, "pt", "coi", "coimbra", 106582)
      cachePaisSql.put("pt", Portugal)
      cacheCiudadSql.put(Lisboa.affinityKey, Lisboa)
      cacheCiudadSql.put(Oporto.affinityKey, Oporto)
      cacheCiudadSql.put(Coimbra.affinityKey, Coimbra)
      println("Cargados datos de Portugal en la caches")
    }
  } else {
    Ignition.setClientMode(true)
    val ignite = Ignition.start()
    val cacheCiudadCfg = new CacheConfiguration[AffinityKey[String], Ciudad]("ciudadCache")
    cacheCiudadCfg.setCacheMode(CacheMode.PARTITIONED)
    cacheCiudadCfg.setIndexedTypes(Seq(classOf[AffinityKey[String]], classOf[Ciudad]): _*)
    val cacheCiudadSql: IgniteCache[AffinityKey[String], Ciudad] = ignite.getOrCreateCache(cacheCiudadCfg)

    val cachePaisCfg = new CacheConfiguration[String, Pais]("paisCache")
    cachePaisCfg.setIndexedTypes(Seq(classOf[String], classOf[Pais]): _*)
    cachePaisCfg.setCacheMode(CacheMode.PARTITIONED)
    val cachePaisSql: IgniteCache[String, Pais] = ignite.getOrCreateCache(cachePaisCfg)
    import scala.collection.JavaConverters._
    while (true) {
      var line = StdIn.readLine()
      line match {
        case "exit" => System.exit(1)
        case s: String if s.trim.startsWith("query es") => {
          val sqlText = s"pais = 'es' AND poblacion > 500000 "
          println(s"***** Query:  $sqlText  *****")
          val sql = new SqlQuery[String, Ciudad](classOf[Ciudad], sqlText)
          cacheCiudadSql.query(sql).asScala.foreach(p => println(s"Ciudad = $p"))
          println("***** *****\n")
        }
        case s: String if s.trim.startsWith("query pt") => {
          val sqlText = s"pais = 'pt' AND poblacion > 100000 "
          println(s"***** Query:  $sqlText  *****")
          val sql = new SqlQuery[String, Ciudad](classOf[Ciudad], sqlText)
          cacheCiudadSql.query(sql).asScala.foreach(p => println(s"Ciudad = $p"))
          println("***** *****\n")
        }
        case s: String if s.trim.startsWith("join es") => {
          val joinSql = "Select c.* from Ciudad as c, \"paisCache\".Pais p where c.pais=p.codigo and c.pais= 'es' AND c.poblacion > 500000"
          println(s"***** JoinQuery:  $joinSql *****")
          cacheCiudadSql.query(new SqlFieldsQuery(joinSql).setArgs(Seq(500000.asInstanceOf[AnyRef]): _*)).asScala.foreach(p => println(s"Ciudad = $p"))
          println("**********\n")
        }
        case s: String if s.trim.startsWith("join pt") => {
          val joinSql = "Select c.* from Ciudad as c inner join \"paisCache\".Pais p on c.pais=p.codigo where  c.pais= 'pt' AND c.poblacion > 300000"
          println(s"***** JoinQuery:  $joinSql *****")
          cacheCiudadSql.query(new SqlFieldsQuery(joinSql).setArgs(Seq(500000.asInstanceOf[AnyRef]): _*)).asScala.foreach(p => println(s"Ciudad = $p"))
          println("**********\n")
        }
        case s: String if s.trim.startsWith("sum") => {
          val joinAggSql = "Select sum(c.poblacion) as total ,p.nombre from Ciudad as c, \"paisCache\".Pais p where c.pais=p.codigo group by(c.pais)"
          println(s"***** JoinQuery:  $joinAggSql *****")
          cacheCiudadSql.query(new SqlFieldsQuery(joinAggSql).setArgs(Seq(500000.asInstanceOf[AnyRef]): _*)).asScala.foreach(p => println(s"Total Poblacion = $p"))
          println("**********\n")
        }
      }
    }

  }


}
