package pl.pawel.diet_app_mobile.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.pawel.diet_app_mobile.data.local.dao.ProductDao
import pl.pawel.diet_app_mobile.data.local.entity.ProductEntity
import pl.pawel.diet_app_mobile.domain.model.Product
import pl.pawel.diet_app_mobile.domain.repository.ProductRepository

class RoomProductRepository @Inject constructor(
    private val productDao: ProductDao,
) : ProductRepository {
    override fun observeProducts(): Flow<List<Product>> =
        productDao.observeAll().map { products -> products.map(ProductEntity::toDomain) }

    override suspend fun addProduct(product: Product) {
        val now = System.currentTimeMillis()
        productDao.insert(product.toEntity(createdAt = now, updatedAt = now))
    }

    override suspend fun updateProduct(product: Product) {
        val existingProduct = productDao.getById(product.id) ?: return
        productDao.update(
            product.toEntity(
                createdAt = existingProduct.createdAt,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }
}

private fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    caloriesPer100g = caloriesPer100g,
    proteinPer100g = proteinPer100g,
    fatPer100g = fatPer100g,
    carbsPer100g = carbsPer100g,
)

private fun Product.toEntity(
    createdAt: Long,
    updatedAt: Long,
): ProductEntity = ProductEntity(
    id = id,
    name = name.trim(),
    caloriesPer100g = caloriesPer100g,
    proteinPer100g = proteinPer100g,
    fatPer100g = fatPer100g,
    carbsPer100g = carbsPer100g,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
