package pl.pawel.diet_app_mobile.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.pawel.diet_app_mobile.domain.model.Product

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>

    suspend fun addProduct(product: Product)

    suspend fun updateProduct(product: Product)

    suspend fun deleteProduct(productId: Long)
}
