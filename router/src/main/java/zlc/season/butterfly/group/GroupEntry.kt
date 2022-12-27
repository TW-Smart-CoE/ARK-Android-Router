package zlc.season.butterfly.group

import zlc.season.butterfly.SchemeRequest

class GroupEntry(val request: SchemeRequest) {
    override fun toString(): String {
        return "{scheme=${request.scheme}, class=${request.className}}"
    }
}