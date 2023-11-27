package models.dao.schema

import models.dao.entities.{Product, ProductItem}
import org.squeryl.Schema


object ProductSchema extends Schema {

  val products = table[Product]

  val productItems = table[ProductItem]

}