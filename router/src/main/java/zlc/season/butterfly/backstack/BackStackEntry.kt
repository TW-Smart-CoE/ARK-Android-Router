package zlc.season.butterfly.backstack

import zlc.season.butterfly.SchemeRequest

class BackStackEntry(val request: SchemeRequest) {
    override fun toString(): String {
        return "{scheme=${request.scheme}, class=${request.className}}"
    }
}