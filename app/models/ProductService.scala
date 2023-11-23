package models

import models.dto.{ProductDTO, ProductItemDTO}

import java.util.UUID
import scala.collection.mutable


trait ProductService {
  def list() : Seq[ProductDTO]
  def update(productDTO: ProductDTO): Option[ProductDTO]
  def create(productDTO: ProductDTO): ProductDTO
  def delete(productId: String): Boolean
  def get(productId: String): Option[ProductDTO]
  def find(text: String): Seq[ProductDTO]

}

class ProductServiceImpl extends ProductService {

  //private val products: mutable.Map[String, ProductDTO] = scala.collection.mutable.Map[String, ProductDTO]()


  private val productsRep: mutable.Map[String, Product] = scala.collection.mutable.Map[String, Product]()
  private val itemsRep: mutable.Map[String, ProductItem] = scala.collection.mutable.Map[String, ProductItem]()
  override def list(): Seq[ProductDTO] = {
    productsRep.values.map(p => new ProductDTO(p.id,
      p.title,
      p.description,
      itemsRep.values
        .filter(i => i.productId == p.id)
        .map(i => new ProductItemDTO(i.id, i.price, i.quantity, i.isAvailable)).toList)
    ).toSeq
    //val seq = products.values.toSeq
    //seq
  }

  override def update(productDTO: ProductDTO): Option[ProductDTO] = if(productsRep.contains(productDTO.id)){

    val n = productDTO.items.map(_.id).toSet
    val e = itemsRep.values.filter(_.productId == productDTO.id).map(_.id).toSet
    (e &~ n).foreach(itemsRep.remove)
    productDTO.items.foreach(i => if(itemsRep.contains(i.id)){
      itemsRep(i.id) = ProductItem(i.id, i.price, i.quantity, i.isAvailable, productDTO.id)
    } else {
      val newItemId = UUID.randomUUID().toString
      itemsRep(newItemId) = ProductItem(newItemId, i.price, i.quantity, i.isAvailable, productDTO.id)
    })
    productsRep(productDTO.id) = Product(productDTO.id, productDTO.title, productDTO.description)
    get(productDTO.id)
  } else None

  override def create(productDTO: ProductDTO): ProductDTO = {
    val pId = UUID.randomUUID().toString
    val items = productDTO.items
      .map(i=> ProductItem(UUID.randomUUID().toString, i.price, i.quantity, i.isAvailable, pId))
    val newProduct = Product(pId, productDTO.title, productDTO.description)
    productsRep(newProduct.id) = newProduct
    items.foreach(i => itemsRep(i.id) = i)
    ProductDTO(pId, newProduct.title, newProduct.description, items.map(i=>ProductItemDTO(i.id, i.price, i.quantity, i.isAvailable)))
  }

  override def delete(productId: String): Boolean = if(productsRep.contains(productId)) {
    productsRep.remove(productId)
    val keys = itemsRep.values.filter(i=>i.productId==productId).map(i=>i.id)
    keys.foreach(itemsRep.remove)
    true
  } else {
    false
  }

  override def get(productId: String): Option[ProductDTO] =
    productsRep
      .get(productId)
      .map(p=>ProductDTO(p.id, p.title, p.description,
        itemsRep
          .values
          .filter(i=>i.productId==productId)
          .map(i=>ProductItemDTO(i.id, i.price, i.quantity, i.isAvailable)).toList)
      )

  override def find(text: String): Seq[ProductDTO] =
    productsRep
      .values
      .filter(p => p.title.contains(text))
      .map(p => ProductDTO(p.id, p.title, p.description,
        itemsRep
          .values
          .filter(i => i.productId == p.id)
          .map(i => ProductItemDTO(i.id, i.price, i.quantity, i.isAvailable)).toList)
      ).toSeq
}
