package meetup5

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object MeetupServer extends App with MeetupRoutes {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val iotActor = system.actorOf(AdsActor.props(igniteDataGrid("iotDeviceDataGrid", false)), "iotActor")

  //Http routes
  lazy val routes: Route = meetupRoutes

  val serverBindingFuture = Http().bindAndHandle(routes, "localhost", 8000)

}