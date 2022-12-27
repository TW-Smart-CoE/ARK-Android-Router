package com.thoughtworks.ark.router.group

import com.thoughtworks.ark.router.SchemeRequest

class GroupEntry(val request: SchemeRequest) {
    override fun toString(): String {
        return "{scheme=${request.scheme}, class=${request.className}}"
    }
}