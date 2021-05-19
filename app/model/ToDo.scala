/**
 *
 * to do sample project
 *
 */

package model

import lib.model.{ToDo, ToDoCategory}
import lib.model.ToDo.ToDoStatus

// ToDo一覧ページのviewvalue
case class ViewValueToDoList(
  title:    String,
  cssSrc:   Seq[String],
  jsSrc:    Seq[String],
  toDoList: Seq[ToDo],
) extends ViewValueCommon

//todo.listで使用するカテゴリーの情報を持ったcase class
case class ViewToDo(
 id:          ToDo.Id,
 categoryId:  ToDoCategory.Id,
 title:       String,
 body:        String,
 state:       ToDoStatus
)

