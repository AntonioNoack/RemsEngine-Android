package me.anno.remsengine.android

import android.view.KeyEvent.*
import me.anno.input.Key
import me.anno.remsengine.android.MainActivity.Companion.GLFW_MOUSE_BUTTON_LEFT
import me.anno.remsengine.android.MainActivity.Companion.GLFW_MOUSE_BUTTON_MIDDLE
import me.anno.remsengine.android.MainActivity.Companion.GLFW_MOUSE_BUTTON_RIGHT

object KeyMap {

    private fun put(android: Int, engine: Key) {
        keyCodeMapping[android] = engine
    }

    // Android -> GLFW
    val keyCodeMapping = HashMap<Int, Key>()

    fun defineKeys() {

        // this list is not complete, but at least it's a start :)
        for (c in 'a'..'z') put(
            KEYCODE_A + (c.code - 'a'.code),
            Key.byId(Key.KEY_A.id + (c.code - 'a'.code))
        )
        for (c in '0'..'9') put(
            KEYCODE_0 + (c.code - '0'.code),
            Key.byId(Key.KEY_0.id + (c.code - '0'.code))
        )
        put(KEYCODE_SPACE, Key.KEY_SPACE)
        put(KEYCODE_ENTER, Key.KEY_ENTER)
        put(KEYCODE_DEL, Key.KEY_BACKSPACE)
        put(KEYCODE_BACKSLASH, Key.KEY_BACKSLASH)
        put(KEYCODE_SLASH, Key.KEY_SLASH)
        put(KEYCODE_SEMICOLON, Key.KEY_SEMICOLON)
        put(KEYCODE_EQUALS, Key.KEY_EQUAL)
        // keys like äöü
        // put(KEYCODE_WORLD_1, "world-1")
        // put(KEYCODE_WORLD_2, "world-2")
        put(KEYCODE_TAB, Key.KEY_TAB)
        put(KEYCODE_INSERT, Key.KEY_INSERT)
        put(KEYCODE_FORWARD_DEL, Key.KEY_DELETE)
        put(KEYCODE_DPAD_LEFT, Key.KEY_ARROW_LEFT)
        put(KEYCODE_DPAD_RIGHT, Key.KEY_ARROW_RIGHT)
        put(KEYCODE_DPAD_UP, Key.KEY_ARROW_UP)
        put(KEYCODE_DPAD_DOWN, Key.KEY_ARROW_DOWN)
        put(KEYCODE_PAGE_UP, Key.KEY_PAGE_UP)
        put(KEYCODE_PAGE_DOWN, Key.KEY_PAGE_DOWN)
        for (i in 1..25) put(KEYCODE_F1 - 1 + i, Key.byId(Key.KEY_F1.id - i + 1))
        put(KEYCODE_NUMPAD_ADD, Key.KEY_KP_ADD)
        put(KEYCODE_NUMPAD_SUBTRACT, Key.KEY_KP_SUBTRACT)
        put(KEYCODE_NUMPAD_MULTIPLY, Key.KEY_KP_MULTIPLY)
        put(KEYCODE_NUMPAD_DIVIDE, Key.KEY_KP_DIVIDE)
        put(KEYCODE_NUMPAD_COMMA, Key.KEY_KP_DECIMAL)
        put(KEYCODE_NUMPAD_DOT, Key.KEY_KP_DECIMAL)
        put(KEYCODE_NUMPAD_ENTER, Key.KEY_KP_ENTER)
        put(GLFW_MOUSE_BUTTON_LEFT, Key.BUTTON_LEFT)
        put(GLFW_MOUSE_BUTTON_RIGHT, Key.BUTTON_RIGHT)
        put(GLFW_MOUSE_BUTTON_MIDDLE, Key.BUTTON_MIDDLE)
        put(KEYCODE_FORWARD, Key.BUTTON_FORWARD)
        put(KEYCODE_BACK, Key.BUTTON_BACK) // could also be mapped to escape...
        for (i in 0..9) put(KEYCODE_NUMPAD_0 + i, Key.byId(Key.KEY_KP_0.id + i))
        put(KEYCODE_SYSRQ, Key.KEY_PRINT_SCREEN)
        put(KEYCODE_MENU, Key.KEY_MENU)
        put(KEYCODE_CTRL_LEFT, Key.KEY_LEFT_CONTROL)
        put(KEYCODE_CTRL_RIGHT, Key.KEY_RIGHT_CONTROL)
        put(KEYCODE_SHIFT_LEFT, Key.KEY_LEFT_SHIFT)
        put(KEYCODE_SHIFT_RIGHT, Key.KEY_RIGHT_SHIFT)
        put(KEYCODE_META_LEFT, Key.KEY_LEFT_SUPER)
        put(KEYCODE_META_RIGHT, Key.KEY_RIGHT_SUPER)
        put(KEYCODE_ALT_LEFT, Key.KEY_LEFT_ALT)
        put(KEYCODE_ALT_RIGHT, Key.KEY_RIGHT_ALT)
        put(KEYCODE_COMMA, Key.KEY_COMMA)
        put(KEYCODE_PERIOD, Key.KEY_PERIOD)
        put(KEYCODE_ESCAPE, Key.KEY_ESCAPE)

    }
}