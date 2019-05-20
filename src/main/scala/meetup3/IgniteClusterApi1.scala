package meetup3

import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.Ignition
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.lang.IgniteRunnable

import scala.collection.JavaConverters._


//Primero iniciar el node con el ROLE ->WORKER
object IgniteClusterApi1 extends App {

  val igniteConfig = new IgniteConfiguration()
  val cacheCfg = new CacheConfiguration[String, Anuncio](CACHE_NAME)
  igniteConfig.setCacheConfiguration(cacheCfg)

  //Este nodo tiene el atributo ROLE -> MASTER
  igniteConfig.setUserAttributes(Map[String, Any]("ROLE" -> "MASTER").asJava)
  val ignite = Ignition.start(igniteConfig)

  //obteniendo referencia al clsuster el nodo MASTER mediante broadcast, envia computo a los nodos con el ROLE ->WORKER
  val igniteCluster = ignite.cluster()

  //Envia este mensaje a todos los nodos con el ROLE WORKER
  ignite.compute(igniteCluster.forAttribute("ROLE", "WORKER"))
    .broadcast(new IgniteRunnable {
      override def run(): Unit = {
        println(s"Hola Worker ")
      }
    })

  ignite.getOrCreateCache(CACHE_NAME)
}
