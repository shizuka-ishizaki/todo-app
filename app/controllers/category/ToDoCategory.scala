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
import model.{ViewValueToDoCategoryList, Category, ViewValueError}

import lib.model.ToDoCategory
import lib.model.ToDoCategory.CategoryColor


@Singleton
class ToDoCategoryController @Inject()(val controllerComponents: ControllerComponents)
    extends BaseController with I18nSupport {

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
}
