package com.yamaokaya

import java.net.{URL,URI}
import java.nio.file.{FileSystems, FileSystem}

import awscala.Region
import awscala.s3.{S3Object, Bucket, S3}
import slick.driver.H2Driver.api._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import collection.JavaConversions._



object Handler {
  implicit val s3 = S3().at(Region.Tokyo)

  implicit class CSVWrapper(val p:Purchase) extends AnyVal{
    def toCSV() =
      s"""
        |${p.shopCode},
        |${p.shopName},
        |${p.itemCode},
        |${p.itemName}
      """.stripMargin.stripLineEnd
  }
  def main(args: Array[String]): Unit = {

    for (
      bucket <- s3.bucket("elis.tranfer.yamaokaya.com");
      file <- bucket.getObject("shire.csv")
    ){

      val db = Database.forConfig("h2mem1")

      try{
        val purchases:TableQuery[Purchases] = TableQuery[Purchases]

        val readAction:DBIO[Unit] = {
          DBIO.seq(
            createTable(file.publicUrl)
          )
        } andThen {
          purchases.sortBy(_.itemCode).result.map { p :Seq[Purchase] =>
            p.groupBy(_.vendorName).map{ i =>
              save(i,bucket)
            }
          }
        }
        Await.result( db.run(readAction),Duration.Inf)
      } finally db.close()

    }
  }

  def save(item: (String,Seq[Purchase]),bucket :Bucket) = {
    bucket.putObject(s"${item._1}.csv",item._2,null)

  }

  implicit def purchasesSequenceToCSVByteArray(records :Seq[Purchase]) : Array[Byte] = {
    records.map(r => r.toCSV()).mkString("¥r¥n").getBytes("Windows-31J")
  }

  def createTable(url:URL,encoding: String = "Windows-31J") : DBIO[Int] =  {
    sqlu"""
      create table purchases as select * from csvread('#$url',null,'#$encoding')
    """
  }
}
