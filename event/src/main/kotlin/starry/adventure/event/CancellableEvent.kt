package starry.adventure.event

abstract class CancellableEvent : Event() {

    var cancelled: Boolean = false

}