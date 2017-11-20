

import assertk.assert
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import mformetal.metallic.util.SelectionHandler
import org.junit.Test

/**
 * Created by mbpeele on 11/19/17.
 */
class SelectionHandlerTests {

    @Test
    fun testSimpleSelection() {
        val handler = SelectionHandler<String>()
        handler.select(0, "item")
        assert(handler.items[0]).isEqualTo("item")
    }

    @Test
    fun testSelectionThenDeselection() {
        val handler = SelectionHandler<String>()
        handler.select(0, "item")
        handler.deselect(0)

        assert(handler.items.size()).isEqualTo(0)
    }

    @Test
    fun testRetrievingSelectedItems() {
        val handler = SelectionHandler<String>()
        handler.select(0, "zero")
        handler.select(1, "one")
        handler.select(2, "two")

        assert(handler.selectedItems) {
            contains("zero")
            contains("one")
            contains("two")
        }
    }

    @Test
    fun testSelectingItemReturnsSelectedBoolean() {
        val handler = SelectionHandler<String>()
        handler.select(0, "item")
        assert(handler.selected(0)).isEqualTo(true)
    }
}