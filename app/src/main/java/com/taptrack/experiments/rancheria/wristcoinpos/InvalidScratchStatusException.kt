package com.taptrack.experiments.rancheria.wristcoinpos


internal class InvalidScratchStatusException : Exception {
    constructor() {}

    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}

    constructor(cause: Throwable) : super(cause) {}

}