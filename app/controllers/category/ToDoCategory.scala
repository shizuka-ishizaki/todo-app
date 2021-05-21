/**
 *
 * to do sample project
 *
 */

package controllers.category

import javax.inject._
import play.api.mvc._
import play.api.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import lib.persistence.default.{ToDoRepository, ToDoCategoryRepository}
import model.{ViewValueToDoCategoryList, Category, ViewValueError, ViewValueToDoCategoryAdd}

import lib.model.ToDoCategory
import lib.model.ToDoCategory.CategoryColor

/**
 * 追加・更新用Form
 */
case class ToDoCategoryFormData (
  name:     String,
  slug:     String,
  color:    Short,
)

@Singleton
class ToDoCategoryController @Inject()(val controllerComponents: ControllerComponents)
    extends BaseController with I18nSupport {

  /**
   * ToDo登録用のFormオブジェクト
   */
  val categoryForm = Form(
    mapping(
      "name"    -> nonEmptyText,
      "slug"    -> nonEmptyText.verifying(
                     constraint = _.matches("""^[0-9a-zA-Z]+$"""),
                     error="半角英数だけで入力してください。"
                   ),
      "color"   -> shortNumber(min = 0, max = 4),
    )(ToDoCategoryFormData.apply)(ToDoCategoryFormData.unapply)
  )

  /**
   * ToDoカテゴリー一覧画面の表示用
   */
  def list() = Action async {implicit request: Request[AnyContent] =>
    for {
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val categoryList = categorySeq.map { category =>
        Category(
          category.id,
          category.v.name,
          category.v.slug,
          category.v.color,
        )
      }
      val vv = ViewValueToDoCategoryList(
        title           = "ToDoカテゴリーリスト",
        cssSrc          = Seq("main.css", "category/list.css"),
        jsSrc           = Seq("main.js"),
        categoryList    = categoryList
      )
      Ok(views.html.category.List(vv))
    }
  }


  /**
   * 登録画面の表示用
   */
  def register() = Action async { implicit request: Request[AnyContent] =>
    for {
      categorySeq <- ToDoCategoryRepository.all()
    } yield {
      val vv = ViewValueToDoCategoryAdd(
        title         = "ToDoカテゴリー登録",
        cssSrc        = Seq("main.css", "category/add.css"),
        jsSrc         = Seq("main.js"),
        categoryForm  = categoryForm,
      )
      Ok(views.html.category.Add(vv))
    }
  }

  /**
   * 登録処理
   */
  def add() = Action async { implicit request: Request[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    categoryForm
      .bindFromRequest()
      .fold(
        // 処理が失敗した場合に呼び出される関数
        // 処理失敗の例: バリデーションエラー
        (formWithErrors: Form[ToDoCategoryFormData]) => {
          for {
            categorySeq <- ToDoCategoryRepository.all()
          } yield {
            val vv = ViewValueToDoCategoryAdd(
              title     = "ToDoカテゴリー登録",
              cssSrc    = Seq("main.css", "category/add.css"),
              jsSrc     = Seq("main.js"),
              categoryForm  = formWithErrors,
            )
            BadRequest(views.html.category.Add(vv))
          }
        },

        // 処理が成功した場合に呼び出される関数
        (categoryForm: ToDoCategoryFormData) => {
          // 登録が完了したら一覧画面へリダイレクトする
          val categoryWithNoId = new ToDoCategory(
            id       = None,
            name     = categoryForm.name,
            slug     = categoryForm.slug,
            color    = CategoryColor.apply(categoryForm.color),
          ).toWithNoId
          for {
            _ <- ToDoCategoryRepository.add(categoryWithNoId)
          } yield {
            Redirect(routes.ToDoCategoryController.list())
                .flashing("success" -> "ToDoカテゴリーを追加しました!")
          }
        }
      )
  }

}
