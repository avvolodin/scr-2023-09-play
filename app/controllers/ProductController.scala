package controllers

import com.google.inject.Inject
import controllers.actions.authAction
import models.{LoginService, ProductService}
import models.dto.{ProductCreateDTO, ProductDTO}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}

class ProductController @Inject()(loginService: LoginService, productService: ProductService) extends Controller{

  // POST /products
  def create(): Action[ProductCreateDTO] = authAction(loginService)(parse.json[ProductCreateDTO]){ rc =>
    Ok(Json.toJson(productService.create(rc.body)))
  }

  // PUT /products

  def update(): Action[ProductDTO] = authAction(loginService)(parse.json[ProductDTO]){ rc =>
    productService.update(rc.body) match {
      case Some(x) => Ok(Json.toJson(x))
      case None => NotFound("Product not found.")
    }
  }

  // DELETE /products/{id}
  def delete(productId: String): Action[AnyContent] = authAction(loginService){ rc =>
    if (productService.delete(productId))
      Ok
    else
      NotFound
  }

  // GET /products
  def list(): Action[AnyContent] = authAction(loginService) {
    Ok(Json.toJson(productService.list()))
  }

  // GET /products?title="..."
  def find(text: String): Action[AnyContent] = authAction(loginService){ rc =>
    Ok(Json.toJson(productService.find(text)))
  }



}
