package meetup5

import org.apache.ignite.cache.query.annotations.QuerySqlField

import scala.annotation.meta.field

case class AnuncioIgnite(@(QuerySqlField@field) fecha: String,
                         @(QuerySqlField@field)(index = true) id: String,
                         @(QuerySqlField@field) texto: String,
                         @(QuerySqlField@field)(index = true) pais: String,
                         @(QuerySqlField@field)(index = true) vertical: Int)