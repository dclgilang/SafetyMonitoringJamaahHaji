package id.co.dif.smj.base

import id.co.dif.smj.data.BaseData

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 02:41
 *
 */
data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T
) : BaseData()

data class BaseRes(
    val status: Int,
    val message: String,
    val data: Any
) : BaseData()
