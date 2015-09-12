package com.yamaokaya

import awscala.Region
import awscala.s3.{S3Object, S3}

object PoiHandler {
  implicit val s3 = S3().at(Region.Tokyo)
  def main(args: Array[String]): Unit = {
    for {
      bucket <- s3.bucket("elis.transfer.yamaokaya.com")
      file <- bucket.getObject("8月度仕入照合結果_0903.xls")
    } {
//        val workbook = Workbook(is= file.getObjectContent)

//      println(workbook)
    }

  }
}
