package meetup4

import javax.cache.expiry.{CreatedExpiryPolicy, Duration}
import meetup0.Anuncio
import meetup0.Utils._
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}

object IgniteReplication extends App {

  val igniteConfig = new IgniteConfiguration()
  val cacheCfg = new CacheConfiguration[String, Anuncio](CACHE_NAME)
  cacheCfg.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.ETERNAL))

  cacheCfg.setCacheMode(CacheMode.PARTITIONED)
  cacheCfg.setBackups(1)

  igniteConfig.setCacheConfiguration(cacheCfg)

  val ignite = Ignition.start(igniteConfig)

  ignite.getOrCreateCache(CACHE_NAME)


  ignite.close()
  System.exit(1)
}
