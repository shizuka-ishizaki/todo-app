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
import lib.persistence.default.{ToDoRepository, ToDoCategoryRepository}
import model.{ViewValueToDoList, ToDoWithCategory}
import lib.model.ToDo
import lib.model.ToDoCategory.CategoryColor
@Singleton
class ToDoController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def list() = Action async {implicit request: Request[AnyContent] =>
    for {
      todoSeq <- ToDoRepository.all()
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val todoList = todoSeq.map { todo =>
        ToDoWithCategory(
          todo.id,
          todo.v.categoryId,
          todo.v.title,
          todo.v.body,
          todo.v.status,
          categorySeq.collectFirst{case i if i.id == todo.v.categoryId => i.v.name}.getOrElse(""),
          categorySeq.collectFirst{case i if i.id == todo.v.categoryId => i.v.color}.getOrElse(CategoryColor.NONE),
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
