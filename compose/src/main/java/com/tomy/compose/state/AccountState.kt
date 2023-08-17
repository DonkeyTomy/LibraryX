package com.tomy.compose.state

class AccountState(
    account: String? = null,
    isAccountValid: (String) -> Boolean = {it.isNotEmpty()},
    accountValidationError: (String) -> String = {"Not valid account"}
): TextFieldState(validator = isAccountValid, errorMsg = accountValidationError) {

    init {
        account?.let {
            text = it
        }
    }
}

val AccountStateSaver = textFieldStateSaver(AccountState())