package models.dao.entities
import org.squeryl.KeyedEntity
case class Product(id: String, title: String, description: String) extends KeyedEntity[String]