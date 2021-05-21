/**
 *
 * to do sample project
 *
 */

package model

import lib.model.{ToDo, ToDoCategory}
import lib.model.ToDo.ToDoStatus
import lib.model.ToDoCategory.CategoryColor
import controllers.todo.ToDoFormData
import play.api.data.Form

// ToDoカテゴリーリスト画面のviewvalue
case class ViewValueToDoCategoryList(
  title:        String,
  cssSrc:       Seq[String],
  jsSrc:        Seq[String],
  categoryList: Seq[Category],
) extends ViewValueCommon

// ToDoカテゴリー追加画面のviewvalue
case class ViewValueToDoCategoryAdd(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  toDoForm:   Form[ToDoFormData],
  categorys:  Seq[(String, String)],
) extends ViewValueCommon

// ToDoカテゴリー編集画面のviewvalue
case class ViewValueToDoCategoryEdit(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  id:         Long,
  toDoForm:   Form[ToDoFormData],
  categorys:  Seq[(String, String)],
) extends ViewValueCommon

// category.listで使用するカテゴリーの情報を持ったcase class
case class Category(
  id:     ToDoCategory.Id,
  name:   String,
  slug:   String,
  color:  CategoryColor,
)

