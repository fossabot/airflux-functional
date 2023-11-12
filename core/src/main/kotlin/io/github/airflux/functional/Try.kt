/*
 * Copyright 2023-2023 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("TooManyFunctions")

package io.github.airflux.functional

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public sealed class Try<out T> {

    public class Success<out T>(public val result: T) : Try<T>()

    public class Failure(public val exception: Throwable) : Try<Nothing>()

    public companion object {

        public inline operator fun <T> invoke(block: () -> T): Try<T> =
            try {
                Success(block())
            } catch (expected: Throwable) {
                if (expected.isFatal())
                    throw expected
                else
                    Failure(expected)
            }

        public val asNull: Try<Nothing?> = Success(null)

        public val asTrue: Try<Boolean> = Success(true)

        public val asFalse: Try<Boolean> = Success(false)

        public val asUnit: Try<Unit> = Success(Unit)

        public val asEmptyList: Try<List<Nothing>> = Success(emptyList())

        public fun of(value: Boolean): Try<Boolean> = if (value) asTrue else asFalse

        @PublishedApi
        internal inline fun <T> tryRun(block: () -> Try<T>): Try<T> =
            try {
                block()
            } catch (expected: Throwable) {
                if (expected.isFatal()) throw expected else Failure(expected)
            }
    }
}

@OptIn(ExperimentalContracts::class)
public fun <T> Try<T>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Try.Success<T>)
        returns(false) implies (this@isSuccess is Try.Failure)
    }
    return this is Try.Success<T>
}

@OptIn(ExperimentalContracts::class)
public fun <T> Try<T>.isSuccess(predicate: (T) -> Boolean): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Try.Success<T>)
        returns(false) implies (this@isSuccess is Try.Failure)
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }
    return this is Try.Success<T> && predicate(result)
}

@OptIn(ExperimentalContracts::class)
public fun <T> Try<T>.isError(): Boolean {
    contract {
        returns(false) implies (this@isError is Try.Success<T>)
        returns(true) implies (this@isError is Try.Failure)
    }
    return this is Try.Failure
}

@OptIn(ExperimentalContracts::class)
public fun <T> Try<T>.isError(predicate: (Throwable) -> Boolean): Boolean {
    contract {
        returns(false) implies (this@isError is Try.Success<T>)
        returns(true) implies (this@isError is Try.Failure)
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }
    return this is Try.Failure && predicate(exception)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> Try<T>.fold(ifSuccess: (T) -> R, ifFailure: (Throwable) -> R): R {
    contract {
        callsInPlace(ifFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(ifSuccess, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) ifSuccess(result) else ifFailure(exception)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R> Try<T>.map(transform: (T) -> R): Try<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) Try { transform(this.result) } else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R> Try<T>.bind(transform: (T) -> Try<R>): Try<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) Try.tryRun { transform(result) } else this
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> Try<T>.onSuccess(block: (T) -> Unit): Try<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.result) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T> Try<T>.onFailure(block: (Throwable) -> Unit): Try<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isError()) block(it.exception) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> Try<T>.recover(block: (Throwable) -> T): Try<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else Try { block(exception) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> Try<T>.recoverWith(block: (Throwable) -> Try<T>): Try<T> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else Try.tryRun { block(exception) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> Try<T>.getOrForward(block: (Try.Failure) -> Nothing): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) result else block(this)
}

public fun <T> Try<T>.getOrNull(): T? = if (isSuccess()) result else null

public infix fun <T> Try<T>.getOrElse(default: T): T = if (isSuccess()) result else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> Try<T>.getOrElse(default: (Throwable) -> T): T {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) result else default(exception)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T> Try<T>.orElse(default: () -> Try<T>): Try<T> {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else Try.tryRun { default() }
}

public fun <T> Try<T>.orThrow(): T = if (isSuccess()) result else throw exception

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E : Throwable> Try<T>.orThrow(transform: (Throwable) -> E): T {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) result else throw transform(exception)
}

public fun <T> Iterable<Try<T>>.sequence(): Try<List<T>> {
    val result = mutableListOf<T>()
    val iter = iterator()
    while (iter.hasNext()) {
        val item = iter.next()
        if (item.isSuccess()) result.add(item.result) else return item
    }
    return if (result.isNotEmpty()) Try.Success(result) else Try.asEmptyList
}

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T, R> Iterable<T>.traverse(block: (T) -> Try<R>): Try<List<R>> {
    contract {
        callsInPlace(block, InvocationKind.UNKNOWN)
    }
    val result = mutableListOf<R>()
    val iter = iterator()
    while (iter.hasNext()) {
        val item = block(iter.next())
        if (item.isSuccess()) result.add(item.result) else return item
    }
    return if (result.isNotEmpty()) Try.Success(result) else Try.asEmptyList
}
