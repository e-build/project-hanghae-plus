package com.hanghae.commerce.logging.aop

import java.util.*

class TransactionContextHolder {
    companion object {
        private val transactionStack: ThreadLocal<Stack<String>?> = ThreadLocal<Stack<String>?>()
        fun startTransaction(transactionId: String?) {
            var stack: Stack<String>? = transactionStack.get()
            if (stack == null) {
                stack = Stack()
                transactionStack.set(stack)
            }
            stack.push(transactionId)
        }

        fun endTransaction() {
            val stack: Stack<String>? = transactionStack.get()
            if (!stack.isNullOrEmpty()) {
                stack.pop()
                if (stack.isEmpty()) {
                    transactionStack.remove()
                }
            }
        }

        fun getCurrentTransactionId(): String? {
            val stack: Stack<String>? = transactionStack.get()
            return if (!stack.isNullOrEmpty()) stack.peek() else null
        }
    }
}
