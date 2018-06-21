package com.distributedsystems.multi.di

import android.content.Context
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import com.distributedsystems.multi.BuildConfig
import com.distributedsystems.multi.networking.scalars.BigNumber
import com.distributedsystems.multi.networking.scalars.EthereumAddressHexValue
import com.distributedsystems.multi.networking.scalars.EthereumTransactionHashHexValue
import com.distributedsystems.multi.type.CustomType
import dagger.Module
import dagger.Provides
import io.reactivex.annotations.NonNull
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [AppModule::class])
class GraphModule {

    companion object {
        val LOG_TAG = GraphModule::class.java.simpleName!!
    }

    @Provides
    @Singleton
    fun loggingInterceptor() : HttpLoggingInterceptor = HttpLoggingInterceptor {
        Log.i(LOG_TAG, it)
    }.setLevel(HttpLoggingInterceptor.Level.BASIC)

    @Provides
    @Singleton
    fun cacheFile(context: Context) : File = File(context.cacheDir, "okhttp_cache")

    @Provides
    @Singleton
    fun cache(cacheFile: File) : Cache = Cache(cacheFile, 10 * 1000 * 1000)

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor, cache: Cache) : OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .cache(cache)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

    @Provides
    @Singleton
    fun providesEthereumAddressCustomTypeAdapter() : CustomTypeAdapter<EthereumAddressHexValue> {
        return object : CustomTypeAdapter<EthereumAddressHexValue> {
            override fun decode(value: CustomTypeValue<*>): EthereumAddressHexValue {
                return EthereumAddressHexValue(value.value.toString())
            }

            override fun encode(value: EthereumAddressHexValue): CustomTypeValue<*> {
                return CustomTypeValue.GraphQLString(value.toString())
            }
        }
    }

    @Provides
    @Singleton
    fun providesEthereumTransactionHashCustomTypeAdapter() : CustomTypeAdapter<EthereumTransactionHashHexValue> {
        return object : CustomTypeAdapter<EthereumTransactionHashHexValue> {
            override fun decode(value: CustomTypeValue<*>): EthereumTransactionHashHexValue {
                return EthereumTransactionHashHexValue(value.value.toString())
            }

            override fun encode(value: EthereumTransactionHashHexValue): CustomTypeValue<*> {
                return CustomTypeValue.GraphQLString(value.toString())
            }
        }
    }

    @Provides
    @Singleton
    fun providesBigNumberCustomTypeAdapter() : CustomTypeAdapter<BigNumber> {
        return object : CustomTypeAdapter<BigNumber> {
            override fun decode(value: CustomTypeValue<*>): BigNumber {
                return BigNumber(value.value.toString().toBigDecimal())
            }

            override fun encode(value: BigNumber): CustomTypeValue<*> {
                return CustomTypeValue.GraphQLString(value.toString())
            }
        }
    }

    @Provides
    @Singleton
    fun provideApolloClient (@NonNull okHttpClient: OkHttpClient,
                             @NonNull addressHexAdapter: CustomTypeAdapter<EthereumAddressHexValue>,
                             @NonNull bigNumberAdapter: CustomTypeAdapter<BigNumber>,
                             @NonNull transactionHashAdapter: CustomTypeAdapter<EthereumTransactionHashHexValue>) : ApolloClient =
            ApolloClient.builder()
                    .serverUrl("https://${BuildConfig.BASE_URL}")
                    .okHttpClient(okHttpClient)
                    .addCustomTypeAdapter(CustomType.ETHEREUMADDRESSHEXVALUE, addressHexAdapter)
                    .addCustomTypeAdapter(CustomType.ETHEREUMTRANSACTIONHASHHEXVALUE, transactionHashAdapter)
                    .addCustomTypeAdapter(CustomType.BIGNUMBER, bigNumberAdapter)
                    .build()

}