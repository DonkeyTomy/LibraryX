package com.tomy.compose.state

class AccountState(val account: String? = null)
    : TextFieldState(validator = ::isAccountValid, errorMsg = ::accountValidationError) {

    init {
        account?.let {
            text = it
        }
    }
}

private fun isAccountValid(account: String): Boolean {
    return true
}

private fun accountValidationError(account: String): String {
    return "Invalid account: $account"
}

val AccountStateSaver = textFieldStateSaver(AccountState())