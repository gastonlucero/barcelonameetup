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
  val JDBC_TABLE = "ignite_table"

  lazy val postgres = {
    println("Creando bd")
    val pg = EmbeddedPostgres.start()
    pg.getDatabase("postgres", "postgres")
      .getConnection
      .prepareStatement(s"CREATE TABLE IF NOT EXISTS $JDBC_TABLE (id text,nombre text)")
      .executeUpdate()
    pg
  }

  lazy val pgConnection = postgres.getDatabase("postgres", "postgres").getConnection
}

object IgniteBDPersistence extends App {

  import DB._

  val config = new IgniteConfiguration()

  val cacheCfg = new CacheConfiguration[String, Persona](JDBC_TABLE)
  cacheCfg.setCacheStoreFactory(FactoryBuilder.factoryOf(classOf[CacheJdbcStore]))
  cacheCfg.setBackups(1)
  cacheCfg.setCacheMode(CacheMode.REPLICATED)

  //Configurando estos parametros, se indica si se debe leer/escribir en la bd cuando se realizan peticiones get/ put/remove sobre la cache
  cacheCfg.setReadThrough(true)
  cacheCfg.setWriteThrough(true)

  config.setCacheConfiguration(cacheCfg)
  val ignition = Ignition.start(config)
  val jdbcCache = ignition.getOrCreateCache[String, Persona](JDBC_TABLE)


  println("\n** Put a la cache => Insert a la bd **")
  jdbcCache.put("111", Persona("111", "Paco"))
  jdbcCache.put("222", Persona("222", "Pepe"))

  println("\n** Get a la cache => Select a la bd **")
  println(jdbcCache.get("111"))
  println(jdbcCache.get("222"))

  println("\n** Remove a la cache => Delete a la bd **")
  jdbcCache.remove("111")

  println("\n** Queries a Postgres **")
  val rs = pgConnection.prepareStatement(s"SELECT * FROM $JDBC_TABLE").executeQuery()
  while (rs.next())
    println(Persona(rs.getString("id"), rs.getString("nombre")))

  jdbcCache.close()
  System.exit(1)
}

class CacheJdbcStore extends CacheStoreAdapter[String, Persona] {

  import DB._

  override def write(entry: Cache.Entry[_ <: String, _ <: Persona]): Unit = Try {
    val ps = pgConnection.prepareStatement(s"INSERT INTO $JDBC_TABLE (id,nombre) VALUES (?,?)")
    ps.setString(1, entry.getKey)
    ps.setString(2, entry.getValue.nombre)
    ps.executeUpdate()
  } match {
    case Success(_) => println(s"Guardado correctamente ${entry.getKey}")
    case Failure(f) => println(s"Error al guardar $f")
  }

  override def delete(key: Any): Unit = Try {
    val ps = pgConnection.prepareStatement(s"DELETE FROM $JDBC_TABLE WHERE id = '$key'")
    ps.executeUpdate()
  } match {
    case Success(_) => println(s"Borrado correctamente $key")
    case Failure(f) => println(s"Error al borrar $f")
  }

  override def load(key: String): Persona = {
    val ps = pgConnection.prepareStatement(s"SELECT * FROM $JDBC_TABLE where id = '$key'")
    val rs = ps.executeQuery()
    if (rs.next())
      Persona(rs.getString("id"), rs.getString("nombre"))
    else
      null
  }

  //Tambien se puede implementar loadCache para realizar una carga inicial de los datos que se quieran
}