package starry.adventure.core.util

import kotlin.reflect.KProperty

class Delegate<T, V>(val callback: T.(property: KProperty<*>) -> V) {

    operator fun getValue(thisRef: T, property: KProperty<*>) = thisRef.callback(property)

}
