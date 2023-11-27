package models

import models.dto.{ProductCreateDTO, ProductDTO, ProductItemDTO}
import com.google.inject.Inject
import models.dao.entities
import models.dao.entities.{Product, ProductItem}
import models.dao.repositories.ProductRepository
import models.dto.{ProductDTO, ProductItemDTO}

import java.util.UUID
import scala.collection.mutable


trait ProductService {
  def list() : Seq[ProductDTO]
  def update(productDTO: ProductDTO): Option[ProductDTO]
  def create(productDTO: ProductCreateDTO): ProductDTO
  def delete(productId: String): Boolean
  def get(productId: String): Option[ProductDTO]
  def find(text: String): Seq[ProductDTO]

}

class ProductServiceImpl @Inject()(val productRepository: ProductRepository) extends ProductService {


  override def list(): Seq[ProductDTO] = {
    productRepository.listProductsWithItems()
      .groupBy(pp => pp._1)
      .map(g => ProductDTO(g._1.id, g._1.title, g._1.description,
        if (g._2.size == 1 && g._2.head._2.isEmpty) {
          List.empty[ProductItemDTO]
        } else {
          g._2.filter(_._2.isDefined).map(_._2.map(pi => ProductItemDTO(pi.id, pi.price, pi.quantity, pi.isAvailable)).orNull)
        }
      )
      ).toSeq
  }

  override def update(productDTO: ProductDTO): Option[ProductDTO] =  {

    productRepository.updateProduct(Product(productDTO.id, productDTO.title, productDTO.description),
      productDTO.items.map(i => ProductItem(i.id, i.price, i.quantity, i.isAvailable, productDTO.id)))

    get(productDTO.id)
  }

  override def create(productDTO: ProductCreateDTO): ProductDTO = {
    get(productRepository.insertProduct(Product("", productDTO.title, productDTO.description),
      productDTO.items.map(i => ProductItem("", i.price, i.quantity, i.isAvailable, productId = ""))).id).orNull
  }

  override def delete(productId: String): Boolean = productRepository.deleteProduct(productId)

  override def get(productId: String): Option[ProductDTO] =
    productRepository.getProductWithItems(productId).map(p => ProductDTO(p._1.id, p._1.title, p._1.description,
      p._2.map(i => ProductItemDTO(i.id, i.price, i.quantity, i.isAvailable))))

  override def find(text: String): Seq[ProductDTO] = {
    productRepository.findProducts(text)
      .groupBy(pp => pp._1)
      .map(g => ProductDTO(g._1.id, g._1.title, g._1.description,
        if (g._2.size == 1 && g._2.head._2.isEmpty) {
          List.empty[ProductItemDTO]
        } else {
          g._2.filter(_._2.isDefined).map(_._2.map(pi => ProductItemDTO(pi.id, pi.price, pi.quantity, pi.isAvailable)).orNull)
        }
      )
      ).toSeq
  }
}
