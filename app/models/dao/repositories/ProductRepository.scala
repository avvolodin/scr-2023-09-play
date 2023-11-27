package models.dao.repositories

import models.dao.entities.{Product, ProductItem}
import models.dao.schema.{PhoneBookSchema, ProductSchema}
import org.squeryl.PrimitiveTypeMode.transaction

import java.util.UUID
import scala.language.postfixOps

trait ProductRepository {

  def insertProduct(product: Product, items: List[ProductItem]): Product
  def insertItem(item:ProductItem): Unit

  def deleteProduct(productId: String): Boolean
  def deleteItem(itemId: String): Unit

  def getProductWithItems(productId: String): Option[(Product, List[ProductItem])]
  def updateProduct(product: Product, items: List[ProductItem]): Unit
  def updateItem(item: ProductItem): Unit

  def listProductsWithItems(): List[(Product, Option[ProductItem])]

  def findProducts(term: String): List[(Product, Option[ProductItem])]
}

class ProductRepositoryImpl extends ProductRepository {
  val products = ProductSchema.products
  val productItems = ProductSchema.productItems

  import org.squeryl.PrimitiveTypeMode._

  override def insertProduct(product: Product, items: List[ProductItem]): Product = transaction {
    val id = UUID.randomUUID().toString
    val newProduct = Product(id, product.title, product.description)
    products.insert(newProduct)
    items.foreach(i => productItems.insert(ProductItem(UUID.randomUUID().toString, i.price, i.quantity, i.isAvailable, id)))
    newProduct
  }

  override def insertItem(item: ProductItem): Unit = transaction(productItems.insert(item))

  override def deleteProduct(productId: String): Boolean = transaction{
    val exist: Boolean = from(products)(p => {
      where(p.id === productId)
      groupBy(p.id)
      compute(countDistinct(p.id))
    }).map(v => v.measures).headOption match {
      case Some(x) => x==1
      case None => false
    }
    products.deleteWhere( _.id === productId )
    return exist
  }

  override def deleteItem(itemId: String): Unit = transaction(productItems.deleteWhere(_.id === itemId))

  override def getProductWithItems(productId: String): Option[(Product, List[ProductItem])] = transaction {
    products
      .where(_.id === productId)
      .headOption
      .map(v => (v, productItems.where(_.productId === productId).toList))
  }

  override def updateProduct(product: Product, i: List[ProductItem]): Unit = transaction{
    products.update(product)
    i.foreach( item => if(item.id.isEmpty){
      productItems.insert(ProductItem(UUID.randomUUID().toString, item.price, item.quantity, item.isAvailable, product.id))
    } else {
      productItems.update(item)
    })
  }

  override def updateItem(item: ProductItem): Unit = transaction(productItems.update(item))

  override def listProductsWithItems(): List[(Product, Option[ProductItem])] = transaction{
    join(products, productItems.leftOuter)((p, i) =>
      select(p, i)
        .orderBy(p.id asc, i.map(_.id) asc)
        .on(Some(p.id) === i.map(_.productId))
    ).toList
  }

  override def findProducts(term: String): List[(Product, Option[ProductItem])] = transaction {
    join(products, productItems.leftOuter)((p, i) =>
      where(p.title like s"%${term}%")
        .select(p, i)
        .orderBy(p.id asc, i.map(_.id) asc)
        .on(Some(p.id) === i.map(_.productId))
    ).toList
  }

}