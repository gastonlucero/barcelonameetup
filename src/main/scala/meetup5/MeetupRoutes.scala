package meetup5

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives.{parameters, post, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import meetup.modelos.Anuncio
import meetup5.AdsActor._
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future
import scala.concurrent.duration._

trait MeetupRoutes extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val anuncioFormat = jsonFormat5(Anuncio)

  implicit def system: ActorSystem

  def adsActor: ActorRef

  implicit lazy val timeoutRequest = Timeout(10 second)

  lazy val meetupRoutes: Route =
    pathPrefix("ads") {
      path("all") {
        get {
          // GET -> /ads/all
          val futureResult: Future[Seq[Anuncio]] = (adsActor ? GetAnuncios)
            .mapTo[Seq[Anuncio]]
          complete(StatusCodes.OK, futureResult)
        }
      } ~
        pathEnd {
          // GET -> /ads?pais=[pais]
          (get & parameters('pais.as[String])) { pais =>
            val optionalDevice: Future[Option[Seq[Anuncio]]] = (adsActor ? GetAnunciosByPais(pais)).mapTo[Option[Seq[Anuncio]]]
            rejectEmptyResponse {
              complete(optionalDevice)
            }
          }
        } ~
        // POST -> /ads
        post {
          entity(as[Anuncio]) {
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

}


