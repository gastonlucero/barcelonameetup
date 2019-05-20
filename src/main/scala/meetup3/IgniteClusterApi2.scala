package meetup3

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}

import scala.collection.JavaConverters._

object IgniteClusterApi2 extends App {

  val igniteConfig = new IgniteConfiguration()
  val cacheCfg = new CacheConfiguration[String, Anuncio](CACHE_NAME)
  igniteConfig.setCacheConfiguration(cacheCfg)

  //Este nodo tiene el atributo ROLE -> MASTER
  igniteConfig.setUserAttributes(Map[String, Any]("ROLE" -> "WORKER").asJava)
  val ignite = Ignition.start(igniteConfig)

  ignite.getOrCreateCache(CACHE_NAME)

}
