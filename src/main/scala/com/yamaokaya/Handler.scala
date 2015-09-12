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
            p.groupBy(_.vendorName).foreach( g =>
//              bucket.putObject()
              bucket.putObject(key = s"${g._1}.csv",bytes = g._2.toString().getBytes(),metadata = null)

            )
          }
        }
        Await.result( db.run(readAction),Duration.Inf)
      } finally db.close()

    }
  }

  def createTable(url:URL,encoding: String = "Windows-31J") : DBIO[Int] =  {
    sqlu"""
      create table purchases as select * from csvread('#$url',null,'#$encoding')
    """
  }
}

//purchases.groupBy(_.vendorCode).map { case (venderCode,group) =>
//venderCode
//}.result.map{ v=>
//println(v)
//v.foreach { x =>
//val xx = db.run(purchases.filter(_.vendorCode === x).result.map(println))
//}
//}