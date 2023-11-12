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

public fun <T> T.success(): Result.Success<T> = Result.Success(this)

public fun <E> E.error(): Result.Error<E> = Result.Error(this)

public sealed class Result<out T, out E> {

    public data class Success<out T>(public val value: T) : Result<T, Nothing>()

    public data class Error<out E>(public val cause: E) : Result<Nothing, E>()

    public companion object {

        public val asNull: Success<Nothing?> = Success(null)

        public val asTrue: Success<Boolean> = Success(true)

        public val asFalse: Success<Boolean> = Success(false)

        public fun of(value: Boolean): Success<Boolean> = if (value) asTrue else asFalse

        public val asUnit: Success<Unit> = Success(Unit)

        public val asEmptyList: Success<List<Nothing>> = Success(emptyList())
    }
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isSuccess(): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Result.Success<T>)
        returns(false) implies (this@isSuccess is Result.Error<E>)
    }
    return this is Result.Success<T>
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isSuccess(predicate: (T) -> Boolean): Boolean {
    contract {
        returns(true) implies (this@isSuccess is Result.Success<T>)
        returns(false) implies (this@isSuccess is Result.Error<E>)
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }
    return this is Result.Success<T> && predicate(value)
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isError(): Boolean {
    contract {
        returns(false) implies (this@isError is Result.Success<T>)
        returns(true) implies (this@isError is Result.Error<E>)
    }
    return this is Result.Error<E>
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.isError(predicate: (E) -> Boolean): Boolean {
    contract {
        returns(false) implies (this@isError is Result.Success<T>)
        returns(true) implies (this@isError is Result.Error<E>)
        callsInPlace(predicate, InvocationKind.AT_MOST_ONCE)
    }
    return this is Result.Error<E> && predicate(cause)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R, E> Result<T, E>.fold(ifSuccess: (T) -> R, ifError: (E) -> R): R {
    contract {
        callsInPlace(ifError, InvocationKind.AT_MOST_ONCE)
        callsInPlace(ifSuccess, InvocationKind.AT_MOST_ONCE)
    }

    return if (isSuccess()) ifSuccess(value) else ifError(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.map(transform: (T) -> R): Result<R, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return bind { value -> transform(value).success() }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, R, E> Result<T, E>.bind(transform: (T) -> Result<R, E>): Result<R, E> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) transform(value) else this
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E, R> Result<T, E>.mapError(transform: (E) -> R): Result<T, R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else transform(cause).error()
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.onSuccess(block: (T) -> Unit): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isSuccess()) block(it.value) }
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, E> Result<T, E>.onError(block: (E) -> Unit): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return also { if (it.isError()) block(it.cause) }
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.recover(block: (E) -> T): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause).success()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.recoverWith(block: (E) -> Result<T, E>): Result<T, E> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else block(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.getOrForward(block: (Result.Error<E>) -> Nothing): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else block(this)
}

public fun <T, E> Result<T, E>.getOrNull(): T? = if (isSuccess()) value else null

public infix fun <T, E> Result<T, E>.getOrElse(default: T): T = if (isSuccess()) value else default

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.getOrElse(default: (E) -> T): T {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else default(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.orElse(default: () -> Result<T, E>): Result<T, E> {
    contract {
        callsInPlace(default, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) this else default()
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.orThrow(exceptionBuilder: (E) -> Throwable): T {
    contract {
        callsInPlace(exceptionBuilder, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) value else throw exceptionBuilder(cause)
}

@OptIn(ExperimentalContracts::class)
public inline infix fun <T, E> Result<T, E>.forEach(block: (T) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return if (isSuccess()) block(value) else Unit
}

public fun <T> Result<T, T>.merge(): T = if (isSuccess()) this.value else this.cause

public fun <T, E> Iterable<Result<T, E>>.sequence(): Result<List<T>, E> {
    val items = buildList {
        val iter = this@sequence.iterator()
        while (iter.hasNext()) {
            val item = iter.next()
            if (item.isSuccess()) add(item.value) else return item
        }
    }
    return if (items.isNotEmpty()) items.success() else Result.asEmptyList
}

@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T, R, E> Iterable<T>.traverse(transforn: (T) -> Result<R, E>): Result<List<R>, E> {
    contract {
        callsInPlace(transforn, InvocationKind.UNKNOWN)
    }
    val items = buildList {
        val iter = this@traverse.iterator()
        while (iter.hasNext()) {
            val item = transforn(iter.next())
            if (item.isSuccess()) add(item.value) else return item
        }
    }
    return if (items.isNotEmpty()) items.success() else Result.asEmptyList
}
