package models.dto

import play.api.libs.json.Json

case class ProductItemDTO(id: String, price: Int, quantity: Int, isAvailable: Boolean)

object ProductItemDTO {
  implicit val reads = Json.reads[ProductItemDTO]
  implicit val writes = Json.writes[ProductItemDTO]
}