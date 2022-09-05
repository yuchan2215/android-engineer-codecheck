package jp.co.yumemi.android.code_check.model.status

import jp.co.yumemi.android.code_check.constant.HTTPResponseCode
import okhttp3.ResponseBody
import retrofit2.Response

/**エラーステータス*/
sealed class ErrorStatus {

    /**[Throwable]を所持する[ErrorStatus]*/
    data class ThrowableError(val throwable: Throwable) : ErrorStatus()

    // HTTPResponseCodeにて定義されたHTTPエラー一覧
    /**拒否エラー*/
    object ForbiddenError : ErrorStatus()

    /**バリデーションエラー*/
    object ValidationError : ErrorStatus()

    /**サービスが提供されないエラー*/
    object ServiceUnavailableError : ErrorStatus()

    /**データが更新されていないことを示すエラー*/
    object NotModifiedError : ErrorStatus()

    /**その他のエラー*/
    data class OtherResponseError(val errorBody: ResponseBody?, val errorCode: Int) : ErrorStatus()

    companion object {

        /**Retrofitの[Response]から[ErrorStatus]を作成します*/
        fun <T> fromRetrofitResponse(response: Response<T>): ErrorStatus {
            return when (val errorCode = response.code()) {
                HTTPResponseCode.OK -> throw IllegalStateException("通常は到達しないレスポンスコード")
                HTTPResponseCode.FORBIDDEN -> ForbiddenError
                HTTPResponseCode.NOT_MODIFIED -> NotModifiedError
                HTTPResponseCode.VALIDATION_FAILED -> ValidationError
                HTTPResponseCode.SERVICE_UNAVAILABLE -> ServiceUnavailableError
                else -> OtherResponseError(response.errorBody(), errorCode)
            }
        }

        /**[Throwable]から[ErrorStatus]を作成します*/
        fun fromThrowable(throwable: Throwable): ThrowableError {
            return ThrowableError(throwable)
        }

        /**リソースから[String]を取得します。*/
        private fun getString(@StringRes resId: Int): String {
            return CodeCheckApplication.instance.getString(resId)
        }
    }
}
