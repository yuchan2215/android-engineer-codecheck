package jp.co.yumemi.android.code_check.model.status

import android.view.View
import retrofit2.Response

/**リクエストのステータス*/
sealed class RequestStatus<T>(
    val isShowLoading: Int = View.GONE,
    val isShowResult: Int = View.INVISIBLE,
    val isShowError: Int = View.GONE
) {
    /**成功していてBodyがある*/
    data class OnSuccess<T>(val body: T) : RequestStatus<T>(
        isShowResult = View.INVISIBLE
    )

    /**失敗していて[ErrorStatus]がある*/
    data class OnError<T>(val error: ErrorStatus) : RequestStatus<T>(
        isShowError = View.INVISIBLE
    )

    /**読み込み中である*/
    class OnLoading<T> : RequestStatus<T>(
        isShowLoading = View.VISIBLE
    )

    /**何もない*/
    class Nothing<T> : RequestStatus<T>()

    companion object {
        /**Retrofitの[Response]から[RequestStatus]を作成する*/
        fun <T> createStatusFromRetrofit(response: Response<T>): RequestStatus<T> {
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody != null) {
                OnSuccess(responseBody)
            } else {
                val errorStatus = ErrorStatus.fromRetrofitResponse(response)
                OnError(errorStatus)
            }
        }

        /**[Throwable]から[OnError]を作成する*/
        fun <T> createErrorStatusFromThrowable(throwable: Throwable): OnError<T> {
            val errorStatus = ErrorStatus.fromThrowable(throwable)
            return OnError(errorStatus)
        }
    }
}
