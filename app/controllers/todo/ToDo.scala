/**
 *
 * to do sample project
 *
 */

package controllers.todo

import javax.inject._
import play.api.mvc._
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lib.persistence.default.{ToDoRepository, ToDoCategoryRepository}
import model.{ViewValueToDoList, ToDoWithCategory, ViewValueToDoAdd}
import lib.model.ToDo
import lib.model.ToDo.ToDoStatus
import lib.model.ToDoCategory
import lib.model.ToDoCategory.CategoryColor

/**
 * 追加・更新用Form
 */
case class ToDoFormData (
  title:      String,
  body:       String,
  status:     Short,
  categoryId: Long,
)

@Singleton
class ToDoController @Inject()(val controllerComponents: ControllerComponents)
    extends BaseController with I18nSupport {

  /**
   * ToDo登録用のFormオブジェクト
   */
  val toDoForm = Form(
    mapping(
      "title"       -> nonEmptyText,
      "body"        -> nonEmptyText,
      "status"      -> shortNumber(min = 0, max = 2),
      "categoryId"  -> longNumber
    )(ToDoFormData.apply)(ToDoFormData.unapply)
  )

  /**
   * ToDo一覧画面の表示用
   */
  def list() = Action async {implicit request: Request[AnyContent] =>
    for {
      todoSeq <- ToDoRepository.all()
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val todoList = todoSeq.map { todo =>
        val category = categorySeq.find(c => c.id == todo.v.categoryId)
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
        cssSrc   = Seq("main.css", "todo/list.css"),
        jsSrc    = Seq("main.js"),
        toDoList = todoList
      )
      Ok(views.html.todo.List(vv))
    }
  }

  /**
   * 登録画面の表示用
   */
  def register() = Action async { implicit request: Request[AnyContent] =>
    for {
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val vv = ViewValueToDoAdd(
        title     = "ToDo登録",
        cssSrc    = Seq("main.css", "todo/add.css"),
        jsSrc     = Seq("main.js"),
        toDoForm  = toDoForm,
        categorys = categorySeq.map( category =>
          (category.id.toString -> category.v.name)
        )
      )
      Ok(views.html.todo.Add(vv))
    }
  }

  /**
   * 登録処理
   */
  def add() = Action async { implicit request: Request[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    toDoForm
      .bindFromRequest()
      .fold(
        // 処理が失敗した場合に呼び出される関数
        // 処理失敗の例: バリデーションエラー
        (formWithErrors: Form[ToDoFormData]) => {
          for {
            categorySeq <- ToDoCategoryRepository.all()
          } yield {
            val vv = ViewValueToDoAdd(
              title     = "ToDo登録",
              cssSrc    = Seq("main.css", "todo/add.css"),
              jsSrc     = Seq("main.js"),
              toDoForm  = formWithErrors,
              categorys = categorySeq.map( category =>
                (category.id.toString -> category.v.name)
              )
            )
            BadRequest(views.html.todo.Add(vv))
          }
      },

      // 処理が成功した場合に呼び出される関数
      (toDoForm: ToDoFormData) => {
        // 登録処理としてSeqに画面から受け取ったコンテンツを持つTweetを追加
        // 登録が完了したら一覧画面へリダイレクトする
        val todoWithNoId = new ToDo(
          id          = None,
          categoryId  = ToDoCategory.Id(toDoForm.categoryId),
          title       = toDoForm.title,
          body        = toDoForm.body,
          status      = ToDoStatus.apply(toDoForm.status),
        ).toWithNoId
        for {
          _ <- ToDoRepository.add(todoWithNoId)
        } yield {
          Redirect(routes.ToDoController.list())
            .flashing("success" -> "Todoを追加しました!!")
        }
      }
    )
  }
}
