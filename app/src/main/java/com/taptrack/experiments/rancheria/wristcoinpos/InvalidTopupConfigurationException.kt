package com.taptrack.experiments.rancheria.wristcoinpos

internal class InvalidTopupConfigurationException : Exception {
    constructor() {}

    constructor(message: String) : super(message) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}

    constructor(cause: Throwable) : super(cause) {}

}