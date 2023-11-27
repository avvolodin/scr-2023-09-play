package module

import models.dao.repositories.{PhoneRecordRepository, PhoneRecordRepositoryImpl}
import models.{LoginService, LoginServiceImpl, ProductService, ProductServiceImpl}

class ScrModule extends AppModule {
  override def configure(): Unit = {
    bindSingleton[LoginService, LoginServiceImpl]
    bindSingleton[PhoneRecordRepository, PhoneRecordRepositoryImpl]
    bindSingleton[ProductService, ProductServiceImpl]
  }
}
