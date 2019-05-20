package meetup6

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import akka.pattern._
import akka.util.Timeout
import meetup6.AdsActor._
import org.apache.ignite.IgniteCache
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait MeetupRoutes extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val anuncioFormat = jsonFormat5(AnuncioIgnite)

  implicit def system: ActorSystem

  implicit lazy val timeoutRequest = Timeout(10 second)

  def adsActor: ActorRef

  def cacheAnuncios: IgniteCache[String, AnuncioIgnite]

  lazy val meetupRoutes: Route =
    pathPrefix("ads") {
      path("all") {
        get {
          // GET -> /ads/all
          val futureResult: Future[Seq[AnuncioIgnite]] = (adsActor ? GetAnuncios)
            .mapTo[Seq[AnuncioIgnite]]
          complete(StatusCodes.OK, futureResult)
        }
      } ~
        // GET -> /ads/byId/[id]
        (get & path("byId" / Segment)) { id =>
          igniteDirective(id) {
            //Logica de negocio
            rejectEmptyResponse {
              complete("No esta en la cache")
            }
          }
        } ~
        pathEnd {
          // GET -> /ads?pais=[pais]
          (get & parameters('pais.as[String])) { pais =>
            val optionalDevice: Future[Option[Seq[AnuncioIgnite]]] = (adsActor ? GetAnunciosByPais(pais)).mapTo[Option[Seq[AnuncioIgnite]]]
            onComplete(optionalDevice) {
              case Success(value) => complete(value)
              case Failure(ex) => complete(StatusCodes.InternalServerError, s"Error ${ex.getMessage}")
            }
          }
        } ~
        // POST -> /ads
        post {
          entity(as[AnuncioIgnite]) {
            ad => {
              adsActor ! SaveAnuncio(ad)
              complete(201, "Ad Creado!!!") //201 = StatusCodes.Created
            }
          }
        }
    } ~
      // DELETE -> /ads/[id]
      path("ads" / Segment) {
        adsId => {
          delete {
            val deleted: Future[Boolean] = (adsActor ? BorrarAnuncio(adsId)).mapTo[Boolean]
            onSuccess(deleted) {
              result => complete(200, HttpEntity(s"Anuncio [$adsId] borrado = [$result]"))
            }

          }
        }
      }


  def igniteDirective(key: String): Directive0 =
    if (cacheAnuncios.containsKey(key)) {
      println("Desde la Directiva")
      complete(cacheAnuncios.get(key))
    }
    else {
      println("Sigue hasta el endpoint")
      mapInnerRoute {
        route =>
          ctx => {
            route(ctx)
          }
      }
    }

}
