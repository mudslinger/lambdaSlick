package com.yamaokaya

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import slick.driver.H2Driver.api._



case class Purchase(
                              purchasedDate: DateTime,
                              shopCode:Int,
                              shopName: String,
                              itemCode:Int,
                              itemName:String,
                              vendorCode:String,
                              vendorName:String,
                              itemDivCode:Option[Int],
                              itemDivName:Option[String],
                              tempZoneCode:Int,
                              tempZoneName:String,
                              pcs:Int,
                              price:Int,
                              amount:Int
                              )

class Purchases(tag:Tag) extends Table[Purchase](tag,"PURCHASES") {
  implicit val dateColumnType = MappedColumnType.base [DateTime,String](
    {dt => dt.toString("yyyyMMdd")},
    {in => DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(in)}
  )
  def purchasedDate = column[DateTime]("仕入計上日")
  def shopCode = column[Int]("店舗コード")
  def shopName = column[String]("店舗名")
  def itemCode = column[Int]("商品コード")
  def itemName = column[String]("商品名")
  def vendorCode = column[String]("メーカーコード")
  def vendorName = column[String]("メーカー名")
  def itemDivCode = column[Option[Int]]("商品区分")
  def itemDivName = column[Option[String]]("商品区分名")
  def tempZoneCode = column[Int]("温度帯区分")
  def tempZoneName = column[String]("温度帯名")
  def pcs = column[Int]("仕入数量")
  def price = column[Int]("仕入単価")
  def amount = column[Int]("仕入金額")

  def * =  (purchasedDate,shopCode,shopName,itemCode,itemName,vendorCode,vendorName,itemDivCode,itemDivName,tempZoneCode,tempZoneName,pcs,price,amount) <> (Purchase.tupled, Purchase.unapply)
}

