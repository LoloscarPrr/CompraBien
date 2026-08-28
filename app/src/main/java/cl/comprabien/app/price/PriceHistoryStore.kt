package cl.comprabien.app.price

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal data class StoredPricePoint(
    val productId: String,
    val retailerId: String,
    val retailerName: String,
    val price: Int,
    val capturedAtEpoch: Long,
    val label: String,
    val source: String,
    val confidence: Int
)

internal class PriceHistoryStore(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE price_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id TEXT NOT NULL,
                retailer_id TEXT NOT NULL,
                retailer_name TEXT NOT NULL,
                price INTEGER NOT NULL,
                captured_at_epoch INTEGER NOT NULL,
                label TEXT NOT NULL,
                source TEXT NOT NULL,
                confidence INTEGER NOT NULL,
                UNIQUE(product_id, retailer_id, captured_at_epoch)
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX idx_price_history_product_retailer ON price_history(product_id, retailer_id, captured_at_epoch)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS price_history")
        onCreate(db)
    }

    fun count(): Int = readableDatabase.rawQuery("SELECT COUNT(*) FROM price_history", null).use { cursor ->
        if (cursor.moveToFirst()) cursor.getInt(0) else 0
    }

    fun insert(point: StoredPricePoint) {
        writableDatabase.insertWithOnConflict(
            "price_history",
            null,
            ContentValues().apply {
                put("product_id", point.productId)
                put("retailer_id", point.retailerId)
                put("retailer_name", point.retailerName)
                put("price", point.price)
                put("captured_at_epoch", point.capturedAtEpoch)
                put("label", point.label)
                put("source", point.source)
                put("confidence", point.confidence)
            },
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    fun history(productId: String, retailerId: String): List<StoredPricePoint> {
        return readableDatabase.query(
            "price_history",
            arrayOf("product_id", "retailer_id", "retailer_name", "price", "captured_at_epoch", "label", "source", "confidence"),
            "product_id = ? AND retailer_id = ?",
            arrayOf(productId, retailerId),
            null,
            null,
            "captured_at_epoch ASC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        StoredPricePoint(
                            productId = cursor.getString(0),
                            retailerId = cursor.getString(1),
                            retailerName = cursor.getString(2),
                            price = cursor.getInt(3),
                            capturedAtEpoch = cursor.getLong(4),
                            label = cursor.getString(5),
                            source = cursor.getString(6),
                            confidence = cursor.getInt(7)
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "comprabien_prices.db"
        private const val DATABASE_VERSION = 1
    }
}
