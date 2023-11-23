package models.dto

import play.api.libs.json.{JsPath, JsValue, Json, Reads, Writes}

import java.util.UUID

case class ProductDTO(id: String, title: String, description: String, items: List[ProductItemDTO])

object ProductDTO {
  implicit val reads = Json.reads[ProductDTO]
  implicit val writes = Json.writes[ProductDTO]

  def createByTemplate(productDTO: ProductDTO): ProductDTO = new ProductDTO(UUID.randomUUID().toString, productDTO.title, productDTO.description,
    productDTO.items.map(v=>new ProductItemDTO(UUID.randomUUID().toString, v.price, v.quantity, v.isAvailable)))
}
