package me.anno.remsengine.android

import android.view.KeyEvent.*
import me.anno.input.KeyCombination
import me.anno.input.KeyCombination.Companion.keyMapping
import me.anno.remsengine.android.MainActivity.Companion.GLFW_MOUSE_BUTTON_LEFT
import me.anno.remsengine.android.MainActivity.Companion.GLFW_MOUSE_BUTTON_MIDDLE
import me.anno.remsengine.android.MainActivity.Companion.GLFW_MOUSE_BUTTON_RIGHT

object KeyMap {

    private fun put(key: Int, vararg buttons: String) {
        for (button in buttons) {
            KeyCombination.put(key, button)
        }
    }

    private fun put(key: Int, glfw: Int, vararg buttons: String) {
        keyCodeMapping[key] = glfw
        put(key, *buttons)
    }

    // Android -> GLFW
    val keyCodeMapping = HashMap<Int, Int>()

    fun defineKeys() {

        // this list is not complete, but at least it's a start :)

        keyMapping.clear()

        for (c in 'a'..'z') put(KEYCODE_A + (c.code - 'a'.code), 65 + (c.code - 'a'.code), "$c")
        for (c in '0'..'9') put(KEYCODE_0 + (c.code - '0'.code), 48 + (c.code - '0'.code), "$c")
        put(KEYCODE_SPACE, 32, " ", "space")
        put(KEYCODE_ENTER, 257, "\n", "enter")
        put(KEYCODE_DEL, 261, "<--", "backspace")
        put(KEYCODE_BACKSLASH, 92, "\\", "backslash")
        put(KEYCODE_SLASH, 47, "/", "slash")
        put(KEYCODE_SEMICOLON, 59, ";", "semicolon")
        put(KEYCODE_EQUALS, 61, "=", "equal", "equals")
        // keys like äöü
        // put(KEYCODE_WORLD_1, "world-1")
        // put(KEYCODE_WORLD_2, "world-2")
        put(KEYCODE_TAB, 258, "\t", "tab")
        put(KEYCODE_INSERT, 260, "insert")
        put(KEYCODE_FORWARD_DEL, 261, "delete")
        put(KEYCODE_DPAD_LEFT, 263, "<-", "leftArrow", "arrowLeft")
        put(KEYCODE_DPAD_RIGHT, 262, "->", "rightArrow", "arrowRight")
        put(KEYCODE_DPAD_UP, 265, "topArrow", "arrowTop", "upArrow", "arrowUp")
        put(KEYCODE_DPAD_DOWN, 264, "bottomArrow", "arrowBottom", "downArrow", "arrowDown")
        put(KEYCODE_PAGE_UP, 266, "pageUp")
        put(KEYCODE_PAGE_DOWN, 267, "pageDown")
        for (i in 1..25) put(KEYCODE_F1 - 1 + i, 290 - i + 1, "f$i")
        put(KEYCODE_NUMPAD_ADD, 334, "+")
        put(KEYCODE_NUMPAD_SUBTRACT, 333, "-")
        put(KEYCODE_NUMPAD_MULTIPLY, 332, "*")
        put(KEYCODE_NUMPAD_DIVIDE, 331, "/")
        put(KEYCODE_NUMPAD_COMMA, 330, ",")
        put(KEYCODE_NUMPAD_DOT, 330, ".")
        put(KEYCODE_NUMPAD_ENTER, 335, "r-enter", "NUMPAD-enter")
        put(GLFW_MOUSE_BUTTON_LEFT, "left")
        put(GLFW_MOUSE_BUTTON_RIGHT, "right")
        put(GLFW_MOUSE_BUTTON_MIDDLE, "middle")
        put(KEYCODE_FORWARD, 5, "mouseForward")
        put(KEYCODE_BACK, 256, "escape")
        for (i in 0..9) put(
            KEYCODE_NUMPAD_0 + i,
            320 + i,
            "kp$i",
            "num$i",
            "numpad$i",
            "numblock$i"
        )
        put(KEYCODE_SYSRQ, 283, "print", "printScreen")
        put(KEYCODE_MENU, 384, "menu", "printMenu")
        put(KEYCODE_CTRL_LEFT, 341, "l-control", "l-ctrl", "control", "ctrl")
        put(KEYCODE_CTRL_RIGHT, 345, "r-control", "r-ctrl")
        put(KEYCODE_SHIFT_LEFT, 340, "l-shift", "shift")
        put(KEYCODE_SHIFT_RIGHT, 344, "r-shift")
        put(KEYCODE_META_LEFT, 343, "l-windows", "l-super", "windows", "super")
        put(KEYCODE_META_RIGHT, 347, "r-windows", "r-shift")
        put(KEYCODE_ALT_LEFT, 342, "l-alt", "alt")
        put(KEYCODE_ALT_RIGHT, 346, "r-alt")
        put(KEYCODE_COMMA, 44, ",", "comma")
        put(KEYCODE_PERIOD, 46, ".", "period", "dot")
        put(KEYCODE_ESCAPE, 256, "esc", "escape")

    }
}