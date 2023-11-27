package models.dao.entities
import org.squeryl.KeyedEntity

case class ProductItem(id: String, price: Int, quantity: Int, isAvailable: Boolean, productId: String) extends KeyedEntity[String]

