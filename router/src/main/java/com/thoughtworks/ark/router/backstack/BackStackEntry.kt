package com.thoughtworks.ark.router.backstack

import com.thoughtworks.ark.router.SchemeRequest

class BackStackEntry(val request: SchemeRequest) {
    override fun toString(): String {
        return "{scheme=${request.scheme}, class=${request.className}}"
    }
}