package jp.co.yumemi.android.code_check.model.status

import android.text.format.DateFormat
import androidx.annotation.StringRes
import java.net.UnknownHostException
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import jp.co.yumemi.android.code_check.CodeCheckApplication
import jp.co.yumemi.android.code_check.R
import jp.co.yumemi.android.code_check.constant.HTTPResponseCode
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * 基本的には[RequestStatus]の[RequestStatus.OnError]のフィールドとして所有される。
 * @param errorDescription エラーが発生した時に、ユーザーに表示されるエラーの説明
 */
sealed class ErrorStatus(val errorDescription: String) {

    /**
     * Retrofitがエラーレスポンスすら返さない時（ネットワークエラー）等に利用する。
     */
    data class ThrowableError(val throwable: Throwable) : ErrorStatus(
        if (throwable is UnknownHostException)
            getString(R.string.unknown_host_exception_error_description)
        else
            throwable.toString()
    )

    // HTTPResponseCodeにて定義されたHTTPエラー一覧
    /**
     * APIのレート制限等に達するとこのエラーになる。
     */
    data class ForbiddenError(private val resetDate: Date?) : ErrorStatus(
        if (resetDate == null)
            getString(R.string.forbidden_error_description)
        else {
            val jpCal = Calendar.getInstance()
            jpCal.time = resetDate
            jpCal.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
            val formattedLocaledDateTime =
                DateFormat.format("MM/dd HH:mm:ss", jpCal)
            CodeCheckApplication.instance.getString(
                R.string.forbidden_error_description_with_time,
                formattedLocaledDateTime.toString()
            )
        }
    )

    /**
     * 0文字でAPIを叩くなど、不正な文字列で叩くとこのエラーになる。
     */
    object ValidationError : ErrorStatus(
        getString(R.string.validation_error_description)
    )

    /**
     * おそらくGitHubのサーバーが不安定または障害のある時に発生する。
     */
    object ServiceUnavailableError : ErrorStatus(
        getString(R.string.service_unavailable_error_description)
    )

    /**
     * データが更新されていないことを示すエラー。
     * 何回かAPIを叩いたが、これが帰ってくることはあまり無さそう。
     */
    object NotModifiedError : ErrorStatus(
        getString(R.string.not_modified_error_description)
    )

    /**
     * 認証できなかったエラー。
     * トークンが間違っている場合などに出る。
     */
    object UnAuthorizedError : ErrorStatus(
        getString(R.string.un_authorized_error_description)
    )

    /**
     * その他のエラー。
     * [HTTPResponseCode]にて定義されていないエラーの時に利用される。
     */
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
                HTTPResponseCode.FORBIDDEN -> {
                    val unixString = response.headers()["x-ratelimit-reset"]
                    val date = unixString?.toLongOrNull()?.let {
                        Date(it * 1000L)
                    }
                    ForbiddenError(date)
                }
                HTTPResponseCode.NOT_MODIFIED -> NotModifiedError
                HTTPResponseCode.VALIDATION_FAILED -> ValidationError
                HTTPResponseCode.SERVICE_UNAVAILABLE -> ServiceUnavailableError
                HTTPResponseCode.UNAUTHORIZED -> UnAuthorizedError
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
