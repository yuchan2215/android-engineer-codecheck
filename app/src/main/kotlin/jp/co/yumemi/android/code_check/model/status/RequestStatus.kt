package jp.co.yumemi.android.code_check.model.status

import jp.co.yumemi.android.code_check.model.status.request.FetchQuery
import retrofit2.Response

sealed interface RequestStatus<T> {
    /**成功していてBodyがある時に使用する。*/
    data class OnSuccess<T>(val body: T) : RequestStatus<T>

    /**失敗していて[ErrorStatus]がある時に使用する。*/
    data class OnError<T>(val error: ErrorStatus, val fetchQuery: FetchQuery) : RequestStatus<T>

    /**読み込み中の時に利用する。*/
    class OnLoading<T> : RequestStatus<T>

    /**何もない時（初期化されてすぐ）等に利用する。*/
    class Nothing<T> : RequestStatus<T>

    companion object {
        /**Retrofitの[Response]から[RequestStatus]を作成する*/
        fun <T> createStatusFromRetrofit(
            response: Response<T>,
            fetchQuery: FetchQuery
        ): RequestStatus<T> {
            val responseBody = response.body()
            return if (response.isSuccessful && responseBody != null) {
                OnSuccess(responseBody)
            } else {
                val errorStatus = ErrorStatus.fromRetrofitResponse(response)
                OnError(errorStatus, fetchQuery)
            }
        }

        /**[Throwable]から[OnError]を作成する*/
        fun <T> createErrorStatusFromThrowable(
            throwable: Throwable,
            fetchQuery: FetchQuery
        ): OnError<T> {
            val errorStatus = ErrorStatus.fromThrowable(throwable)
            return OnError(errorStatus, fetchQuery)
        }
    }
}
