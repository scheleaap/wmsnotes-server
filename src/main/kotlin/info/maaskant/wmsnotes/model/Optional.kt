package info.maaskant.wmsnotes.model

// Source: https://medium.com/@joshfein/handling-null-in-rxjava-2-0-10abd72afa0b
data class Optional<T>(val value: T?) {
    val isPresent = value != null
}


//// Source: https://www.reddit.com/r/androiddev/comments/6758oj/handling_null_in_rxjava_20/dgnu7xi/
//sealed class Optional<out T> {
//    class Some<out T>(val element: T): Optional<T>()
//    object None: Optional<Nothing>()
//}