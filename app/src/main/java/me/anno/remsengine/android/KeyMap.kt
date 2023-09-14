package me.anno.remsengine.android

import android.view.KeyEvent.*
import me.anno.input.Key
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

    private fun put(android: Int, engine: Key, vararg buttons: String) {
        keyCodeMapping[android] = engine
        put(android, *buttons)
    }

    // Android -> GLFW
    val keyCodeMapping = HashMap<Int, Key>()

    fun defineKeys() {

        // this list is not complete, but at least it's a start :)

        keyMapping.clear()

        for (c in 'a'..'z') put(
            KEYCODE_A + (c.code - 'a'.code),
            Key.byId(Key.KEY_A.id + (c.code - 'a'.code)), "$c"
        )
        for (c in '0'..'9') put(
            KEYCODE_0 + (c.code - '0'.code),
            Key.byId(Key.KEY_0.id + (c.code - '0'.code)), "$c"
        )
        put(KEYCODE_SPACE, Key.KEY_SPACE, " ", "space")
        put(KEYCODE_ENTER, Key.KEY_ENTER, "\n", "enter")
        put(KEYCODE_DEL, Key.KEY_BACKSPACE, "<--", "backspace")
        put(KEYCODE_BACKSLASH, Key.KEY_BACKSLASH, "\\", "backslash")
        put(KEYCODE_SLASH, Key.KEY_SLASH, "/", "slash")
        put(KEYCODE_SEMICOLON, Key.KEY_SEMICOLON, ";", "semicolon")
        put(KEYCODE_EQUALS, Key.KEY_EQUAL, "=", "equal", "equals")
        // keys like äöü
        // put(KEYCODE_WORLD_1, "world-1")
        // put(KEYCODE_WORLD_2, "world-2")
        put(KEYCODE_TAB, Key.KEY_TAB, "\t", "tab")
        put(KEYCODE_INSERT, Key.KEY_INSERT, "insert")
        put(KEYCODE_FORWARD_DEL, Key.KEY_DELETE, "delete")
        put(KEYCODE_DPAD_LEFT, Key.KEY_ARROW_LEFT, "<-", "leftArrow", "arrowLeft")
        put(KEYCODE_DPAD_RIGHT, Key.KEY_ARROW_RIGHT, "->", "rightArrow", "arrowRight")
        put(KEYCODE_DPAD_UP, Key.KEY_ARROW_UP, "topArrow", "arrowTop", "upArrow", "arrowUp")
        put(
            KEYCODE_DPAD_DOWN, Key.KEY_ARROW_DOWN,
            "bottomArrow", "arrowBottom", "downArrow", "arrowDown"
        )
        put(KEYCODE_PAGE_UP, Key.KEY_PAGE_UP, "pageUp")
        put(KEYCODE_PAGE_DOWN, Key.KEY_PAGE_DOWN, "pageDown")
        for (i in 1..25) put(
            KEYCODE_F1 - 1 + i,
            Key.byId(Key.KEY_F1.id - i + 1), "f$i"
        )
        put(KEYCODE_NUMPAD_ADD, Key.KEY_KP_ADD, "+")
        put(KEYCODE_NUMPAD_SUBTRACT, Key.KEY_KP_SUBTRACT, "-")
        put(KEYCODE_NUMPAD_MULTIPLY, Key.KEY_KP_MULTIPLY, "*")
        put(KEYCODE_NUMPAD_DIVIDE, Key.KEY_KP_DIVIDE, "/")
        put(KEYCODE_NUMPAD_COMMA, Key.KEY_KP_DECIMAL, ",")
        put(KEYCODE_NUMPAD_DOT, Key.KEY_KP_DECIMAL, ".")
        put(KEYCODE_NUMPAD_ENTER, Key.KEY_KP_ENTER, "r-enter", "NUMPAD-enter")
        put(GLFW_MOUSE_BUTTON_LEFT, Key.BUTTON_LEFT, "left")
        put(GLFW_MOUSE_BUTTON_RIGHT, Key.BUTTON_RIGHT, "right")
        put(GLFW_MOUSE_BUTTON_MIDDLE, Key.BUTTON_MIDDLE, "middle")
        put(KEYCODE_FORWARD, Key.BUTTON_FORWARD, "mouseForward")
        put(KEYCODE_BACK, Key.BUTTON_BACK, "mouseBack") // could also be mapped to escape...
        for (i in 0..9) put(
            KEYCODE_NUMPAD_0 + i,
            Key.byId(Key.KEY_KP_0.id + i),
            "kp$i",
            "num$i",
            "numpad$i",
            "numblock$i"
        )
        put(KEYCODE_SYSRQ, Key.KEY_PRINT_SCREEN, "print", "printScreen")
        put(KEYCODE_MENU, Key.KEY_MENU, "menu", "printMenu")
        put(KEYCODE_CTRL_LEFT, Key.KEY_LEFT_CONTROL, "l-control", "l-ctrl", "control", "ctrl")
        put(KEYCODE_CTRL_RIGHT, Key.KEY_RIGHT_CONTROL, "r-control", "r-ctrl")
        put(KEYCODE_SHIFT_LEFT, Key.KEY_LEFT_SHIFT, "l-shift", "shift")
        put(KEYCODE_SHIFT_RIGHT, Key.KEY_RIGHT_SHIFT, "r-shift")
        put(KEYCODE_META_LEFT, Key.KEY_LEFT_SUPER, "l-windows", "l-super", "windows", "super")
        put(KEYCODE_META_RIGHT, Key.KEY_RIGHT_SUPER, "r-windows", "r-shift")
        put(KEYCODE_ALT_LEFT, Key.KEY_LEFT_ALT, "l-alt", "alt")
        put(KEYCODE_ALT_RIGHT, Key.KEY_RIGHT_ALT, "r-alt")
        put(KEYCODE_COMMA, Key.KEY_COMMA, ",", "comma")
        put(KEYCODE_PERIOD, Key.KEY_PERIOD, ".", "period", "dot")
        put(KEYCODE_ESCAPE, Key.KEY_ESCAPE, "esc", "escape")

    }
}