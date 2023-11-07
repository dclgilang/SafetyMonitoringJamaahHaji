package id.co.dif.smj.base

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */


data class DataList<T>(
    val list: List<T>,
    val page : Int,
    val limit : Int,
    val total : Int
)
