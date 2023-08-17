package com.tomy.compose.state

open class PasswordState(
    isPasswordValid: (String) -> Boolean = {
        it.isNotEmpty()
    },
    passwordValidationError: (String) -> String = {"Not valid password"}
) :
    TextFieldState(validator = isPasswordValid, errorMsg = passwordValidationError)

class ConfirmPasswordState(
    private val passwordState: PasswordState,
    private val passwordConfirmationError: () -> String = {""}
    ) : TextFieldState() {
    override val isValid
        get() = passwordState.isValid && text == passwordState.text

    override fun getErrorMsg(): String? {
        return if (canShowError()) {
            passwordConfirmationError()
        } else {
            null
        }
    }
}

