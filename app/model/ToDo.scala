/**
 *
 * to do sample project
 *
 */

package model

import lib.model.{ToDo, ToDoCategory}
import lib.model.ToDo.ToDoStatus
import lib.model.ToDoCategory.CategoryColor

// ToDo一覧ページのviewvalue
case class ViewValueToDoList(
  title:    String,
  cssSrc:   Seq[String],
  jsSrc:    Seq[String],
  toDoList: Seq[ToDoWithCategory],
) extends ViewValueCommon

//todo.listで使用するカテゴリーの情報を持ったcase class
case class ToDoWithCategory(
  id:             ToDo.Id,
  categoryId:     ToDoCategory.Id,
  title:          String,
  body:           String,
  status:         ToDoStatus,
  categoryName:   String,
  categoryColor:  CategoryColor,
)

