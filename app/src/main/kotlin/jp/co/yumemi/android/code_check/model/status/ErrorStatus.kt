package jp.co.yumemi.android.code_check.model.status

import androidx.annotation.StringRes
import java.net.UnknownHostException
import jp.co.yumemi.android.code_check.CodeCheckApplication
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.constant.HTTPResponseCode
import okhttp3.ResponseBody
import retrofit2.Response

/**エラーステータス*/
sealed class ErrorStatus(val errorDescription: String) {

    /**[Throwable]を所持する[ErrorStatus]*/
    data class ThrowableError(val throwable: Throwable) : ErrorStatus(
        if (throwable is UnknownHostException)
            getString(R.string.unknown_host_exception_error_description)
        else
            throwable.toString()
    )

    // HTTPResponseCodeにて定義されたHTTPエラー一覧
    /**拒否エラー*/
    object ForbiddenError : ErrorStatus(
        getString(R.string.forbidden_error_description)
    )

    /**バリデーションエラー*/
    object ValidationError : ErrorStatus(
        getString(R.string.validation_error_description)
    )

    /**サービスが提供されないエラー*/
    object ServiceUnavailableError : ErrorStatus(
        getString(R.string.service_unavailable_error_description)
    )

    /**データが更新されていないことを示すエラー*/
    object NotModifiedError : ErrorStatus(
        getString(R.string.not_modified_error_description)
    )

    /**その他のエラー*/
    data class OtherResponseError(val errorBody: ResponseBody?, val errorCode: Int) : ErrorStatus(
        "${errorBody?.toString()}\n" +
            getString(R.string.response_code_prefix) +
            errorCode
    )

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
