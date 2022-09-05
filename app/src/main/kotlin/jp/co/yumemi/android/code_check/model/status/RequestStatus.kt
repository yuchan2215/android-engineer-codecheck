package jp.co.yumemi.android.code_check.model.status

import android.view.View
import retrofit2.Response

sealed class RequestStatus<T>(
    val isShowLoading: Int = View.GONE,
    val isShowResult: Int = View.INVISIBLE,
    val isShowError: Int = View.GONE
) {
    /**成功していてBodyがある時に使用する。*/
    data class OnSuccess<T>(val body: T) : RequestStatus<T>(
        isShowResult = View.VISIBLE
    )

    /**失敗していて[ErrorStatus]がある時に使用する。*/
    data class OnError<T>(val error: ErrorStatus) : RequestStatus<T>(
        isShowError = View.VISIBLE
    )

    /**読み込み中の時に利用する。*/
    class OnLoading<T> : RequestStatus<T>(
        isShowLoading = View.VISIBLE
    )

    /**何もない時（初期化されてすぐ）等に利用する。*/
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
