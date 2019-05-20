package meetup7

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import javax.cache.Cache
import javax.cache.configuration.FactoryBuilder
import org.apache.ignite.Ignition
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.cache.store.CacheStoreAdapter
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}

import scala.util.{Failure, Success, Try}

case class Persona(dni: String, nombre: String)

object DB {
  lazy val postgres = {
    println("Creando bd")
    val pg = EmbeddedPostgres.start()
    pg.getDatabase("postgres", "postgres")
      .getConnection
      .prepareStatement(s"CREATE TABLE IF NOT EXISTS ignite_table (id text,nombre text)")
      .executeUpdate()
    pg
  }
}

object IgniteBDPersistence extends App {

  val config = new IgniteConfiguration()

  val JdbcPersistence = "ignite_table"
  val cacheCfg = new CacheConfiguration[String, Persona](JdbcPersistence)

  cacheCfg.setCacheStoreFactory(FactoryBuilder.factoryOf(classOf[CacheJdbcStore]))
  cacheCfg.setBackups(1)
  cacheCfg.setCacheMode(CacheMode.REPLICATED)
  cacheCfg.setReadThrough(true)
  cacheCfg.setWriteThrough(true)

  config.setCacheConfiguration(cacheCfg)

  val ignition = Ignition.start(config)
  val jdbcCache = ignition.getOrCreateCache[String, Persona](JdbcPersistence)


  jdbcCache.put("111", Persona("111", "Gaston"))
  jdbcCache.put("222", Persona("222", "Pepe"))


  println(jdbcCache.get("111"))
  println(jdbcCache.get("222"))

  jdbcCache.remove("111")

  println("****")
  val rs = DB.postgres.getDatabase("postgres", "postgres")
    .getConnection
    .prepareStatement(s"SELECT * FROM ignite_table").executeQuery()
  while (rs.next())
    println(Persona(rs.getString("id"), rs.getString("nombre")))

}

class CacheJdbcStore extends CacheStoreAdapter[String, Persona] {

  lazy val connection = DB.postgres.getDatabase("postgres", "postgres").getConnection

  override def write(entry: Cache.Entry[_ <: String, _ <: Persona]): Unit = Try {
    val ps = connection.prepareStatement("INSERT INTO ignite_table (id,nombre) VALUES (?,?)")
    ps.setString(1, entry.getKey)
    ps.setString(2, entry.getValue.nombre)
    ps.executeUpdate()
  } match {
    case Success(_) => println(s"guardado correctamente")
    case Failure(f) => println(s"Error al guardar $f")
  }

  override def delete(key: Any): Unit = Try {
    val ps = connection.prepareStatement(s"DELETE FROM ignite_table WHERE id = '$key'")
    ps.executeUpdate()
  } match {
    case Success(_) => println(s"Borrado correctamente")
    case Failure(f) => println(s"Error al borrar $f")
  }

  override def load(key: String): Persona = {
    val ps = connection.prepareStatement(s"SELECT * FROM ignite_table where id = '$key'")
    val rs = ps.executeQuery()
    if (rs.next())
      Persona(rs.getString("id"), rs.getString("nombre"))
    else
      null
  }

  //Tambien se puede implementar loadCache para realizar una carga inicial de los datos que se quieran
}