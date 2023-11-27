package models.dto

import play.api.libs.json.{JsPath, JsValue, Json, Reads, Writes}
import models.{Product, ProductItem}

import java.util.UUID

case class ProductDTO(id: String, title: String, description: String, items: List[ProductItemDTO])

object ProductDTO {
  implicit val reads = Json.reads[ProductDTO]
  implicit val writes = Json.writes[ProductDTO]

  def fromProduct(product: Product, items: Iterable[ProductItem]) : ProductDTO = new ProductDTO(product.id,
    product.title,
    product.description,
    items
      .map(i => new ProductItemDTO(i.id, i.price, i.quantity, i.isAvailable)).toList
  )

  def createByTemplate(productDTO: ProductDTO): ProductDTO = new ProductDTO(UUID.randomUUID().toString, productDTO.title, productDTO.description,
    productDTO.items.map(v=>new ProductItemDTO(UUID.randomUUID().toString, v.price, v.quantity, v.isAvailable)))
}

case class ProductCreateDTO(title: String, description: String, items: List[ProductItemCreateDTO])

object ProductCreateDTO {
  implicit val reads = Json.reads[ProductCreateDTO]
}