

import assertk.assert
import assertk.assertions.contains
import assertk.assertions.isEmpty
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
        assert(handler.selected(0)).isEqualTo(true)
        assert(handler.selectedItems).contains("item")
    }

    @Test
    fun testSelectionThenDeselection() {
        val handler = SelectionHandler<String>()
        handler.select(0, "item")
        handler.deselect(0)
        assert(handler.selected(0)).isEqualTo(true)
        assert(handler.selectedItems).isEmpty()
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