package module

import models.dao.repositories.{PhoneRecordRepository, PhoneRecordRepositoryImpl, ProductRepository, ProductRepositoryImpl}
import models.{LoginService, LoginServiceImpl, ProductService, ProductServiceImpl}

class ScrModule extends AppModule {
  override def configure(): Unit = {
    bindSingleton[LoginService, LoginServiceImpl]
    bindSingleton[PhoneRecordRepository, PhoneRecordRepositoryImpl]
    bindSingleton[ProductRepository, ProductRepositoryImpl]
    bindSingleton[ProductService, ProductServiceImpl]
  }
}
