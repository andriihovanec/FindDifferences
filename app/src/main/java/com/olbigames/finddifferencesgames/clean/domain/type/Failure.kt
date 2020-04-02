package com.olbigames.finddifferencesgames.clean.domain.type

/**
 * Base Class for handling errors/failures/exceptions.
 */

sealed class Failure {
    object NetworkConnectionError : Failure()
    object ServerError : Failure()
}