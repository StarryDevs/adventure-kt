package starry.adventure.core.event

abstract class CancellableEvent : Event() {

    var cancelled: Boolean = false

}