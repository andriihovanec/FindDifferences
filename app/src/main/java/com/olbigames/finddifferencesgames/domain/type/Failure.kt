package com.olbigames.finddifferencesgames.domain.type

/**
 * Base Class for handling errors/failures/exceptions.
 */

sealed class Failure {
    object NetworkConnectionError : Failure()
    object ServerError : Failure()
}