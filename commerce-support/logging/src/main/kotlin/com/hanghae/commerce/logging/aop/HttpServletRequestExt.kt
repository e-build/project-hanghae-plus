package com.hanghae.commerce.logging.aop

import com.hanghae.commerce.logging.LogConstants
import jakarta.servlet.http.HttpServletRequest

fun HttpServletRequest.log(): String {
    return "[${LogConstants.WEB}] [${this.method} ${this.requestURI}]"
}
