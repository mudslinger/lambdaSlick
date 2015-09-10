package com.yamaokaya

import java.net.URL

import awscala.Region
import awscala.s3.{S3Object, Bucket, S3}
import slick.driver.H2Driver.api._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Handler {
  implicit val s3 = S3().at(Region.Tokyo)
  def main(args: Array[String]): Unit = {

    val f: Option[S3Object] = (for {
      bucket <- s3.bucket("elis.tranfer.yamaokaya.com")
      file <- bucket.getObject("shire.csv")
    } yield(file) )

    val db = Database.forConfig("h2mem1")
    try{
      val readAction:DBIO[Unit] = {
        DBIO.seq(
          createTable(f.get.publicUrl)
        )
      } andThen {
        DBIO.seq (
          select().map(println)
        )
      }

      Await.result( db.run(readAction),Duration.Inf))
    } finally db.close()
  }

  def createTable(url:URL,encoding: String = "Windows-31J") : DBIO[Int] =  {
    sqlu"""
      create table purchases as select * from csvread('#$url',null,'#$encoding')
    """
  }
  def select() :DBIO[Seq[Int]] = {
    sql"select * from purchases".as[(Int)]
  }
}
