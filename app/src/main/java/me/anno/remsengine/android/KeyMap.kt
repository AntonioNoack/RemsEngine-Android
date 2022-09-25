package me.anno.remsengine.android

import android.view.KeyEvent.*
import me.anno.input.KeyCombination
import me.anno.input.KeyCombination.Companion.keyMapping
import me.anno.remsengine.MainActivity.Companion.GLFW_KEY_ESCAPE
import me.anno.remsengine.MainActivity.Companion.GLFW_MOUSE_BUTTON_LEFT
import me.anno.remsengine.MainActivity.Companion.GLFW_MOUSE_BUTTON_MIDDLE
import me.anno.remsengine.MainActivity.Companion.GLFW_MOUSE_BUTTON_RIGHT

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

        for (c in 'a'..'z') keyMapping["$c"] = KEYCODE_A + (c.code - 'a'.code)
        for (c in '0'..'9') keyMapping["$c"] = KEYCODE_0 + (c.code - '0'.code)
        put(KEYCODE_SPACE, " ", "space")
        put(KEYCODE_ENTER, "\n", "enter")
        put(KEYCODE_DEL, "<--", "backspace")
        put(KEYCODE_BACKSLASH, "\\", "backslash")
        put(KEYCODE_SLASH, "/", "slash")
        put(KEYCODE_SEMICOLON, ";", "semicolon")
        put(KEYCODE_EQUALS, "=", "equal", "equals")
        // keys like äöü
        // put(KEYCODE_WORLD_1, "world-1")
        // put(KEYCODE_WORLD_2, "world-2")
        put(KEYCODE_TAB, "\t", "tab")
        put(KEYCODE_INSERT, "insert")
        put(KEYCODE_FORWARD_DEL, "delete")
        put(KEYCODE_DPAD_LEFT, "<-", "leftArrow", "arrowLeft")
        put(KEYCODE_DPAD_RIGHT, "->", "rightArrow", "arrowRight")
        put(KEYCODE_DPAD_UP, "topArrow", "arrowTop", "upArrow", "arrowUp")
        put(KEYCODE_DPAD_DOWN, "bottomArrow", "arrowBottom", "downArrow", "arrowDown")
        put(KEYCODE_PAGE_UP, "pageUp")
        put(KEYCODE_PAGE_DOWN, "pageDown")
        for (i in 1..25) put(KEYCODE_F1 - 1 + i, "f$i")
        put(KEYCODE_NUMPAD_ADD, "+")
        put(KEYCODE_NUMPAD_SUBTRACT, "-")
        put(KEYCODE_NUMPAD_MULTIPLY, "*")
        put(KEYCODE_NUMPAD_DIVIDE, "/")
        put(KEYCODE_NUMPAD_COMMA, ",")
        put(KEYCODE_NUMPAD_DOT, ".")
        put(KEYCODE_NUMPAD_ENTER, "r-enter", "NUMPAD-enter")
        put(GLFW_MOUSE_BUTTON_LEFT, "left")
        put(GLFW_MOUSE_BUTTON_RIGHT, "right")
        put(GLFW_MOUSE_BUTTON_MIDDLE, "middle")
        put(KEYCODE_FORWARD, "mouseForward")
        put(KEYCODE_BACK, GLFW_KEY_ESCAPE, "escape")
        for (i in 0..9) put(KEYCODE_NUMPAD_0 + i, "kp$i", "num$i", "numpad$i", "numblock$i")
        put(KEYCODE_SYSRQ, "print", "printScreen")
        put(KEYCODE_MENU, "menu", "printMenu")
        put(KEYCODE_CTRL_LEFT, "l-control", "l-ctrl", "control", "ctrl")
        put(KEYCODE_CTRL_RIGHT, "r-control", "r-ctrl")
        put(KEYCODE_SHIFT_LEFT, "l-shift", "shift")
        put(KEYCODE_SHIFT_RIGHT, "r-shift")
        put(KEYCODE_META_LEFT, "l-windows", "l-super", "windows", "super")
        put(KEYCODE_META_RIGHT, "r-windows", "r-shift")
        put(KEYCODE_ALT_LEFT, "l-alt", "alt")
        put(KEYCODE_ALT_RIGHT, "r-alt")
        put(KEYCODE_COMMA, ",", "comma")
        put(KEYCODE_PERIOD, ".", "period", "dot")
        put(KEYCODE_ESCAPE, "esc", "escape")

    }
}