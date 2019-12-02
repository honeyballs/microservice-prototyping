package com.example.worktimeadministration.model.saga

/**
 * Converts a comma separated list of Strings into a List of Strings.
 * Returns an empty list when an empty String is provided
 */
fun convertStringToList(str: String): MutableList<String> {
    return str.split(",").filter { it != "" }.toMutableList()
}

/**
 * Converts a list of Strings into a comma separated String of the values.
 * Returns an empty String if the list is empty.
 */
fun convertListToString(strings: List<String>): String {
    if (strings.isEmpty()) {
        return ""
    }
    return strings.reduce { acc, s ->  "$acc,$s"}.removePrefix(",")
}