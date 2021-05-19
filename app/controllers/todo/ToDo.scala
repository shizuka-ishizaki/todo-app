/**
 *
 * to do sample project
 *
 */

package controllers.todo

import javax.inject._
import play.api.mvc._
import play.api.Configuration
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lib.persistence.default.ToDoRepository
import model.ViewValueToDoList
import lib.model.ToDo

@Singleton
class ToDoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def list() = Action async {implicit request: Request[AnyContent] =>
    for (
      list <- ToDoRepository.all()
    ) yield {
      val todoList = list.map { todo =>
        ToDo(
          todo.v.id,
          todo.v.categoryId,
          todo.v.title,
          todo.v.body,
          todo.v.state
        )
      }
      val vv = ViewValueToDoList(
        title    = "ToDoリスト",
        cssSrc   = Seq("main.css"),
        jsSrc    = Seq("main.js"),
        toDoList = todoList
      )
      Ok(views.html.todo.List(vv))
    }
  }
}
