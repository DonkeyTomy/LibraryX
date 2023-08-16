package com.tomy.compose.state

class AccountState(
    val account: String? = null,
    isAccountValid: (String) -> Boolean = {true},
    accountValidationError: (String) -> String = {""}
): TextFieldState(validator = isAccountValid, errorMsg = accountValidationError) {

    init {
        account?.let {
            text = it
        }
    }
}

val AccountStateSaver = textFieldStateSaver(AccountState())