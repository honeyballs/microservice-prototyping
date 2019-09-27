/**
 * Defines the Event properties that every event needs which are an id and a timestamp.
 */
interface Event {

    val originatingServiceName: String
    val id: String
    val eventCreatedAt: String
}