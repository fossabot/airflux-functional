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

package io.github.airflux.functional.kotest

import io.github.airflux.functional.Result
import io.github.airflux.functional.isError
import io.github.airflux.functional.isSuccess
import io.kotest.assertions.failure
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.shouldBeSuccess(): Result.Success<T> {
    contract {
        returns() implies (this@shouldBeSuccess is Result.Success<T>)
    }

    val message =
        "The result type is not as expected. Expected type: `Result.Success`, actual type: `Result.Failure` ($this)."
    return if (isSuccess()) this else throw failure(message = message)
}

@OptIn(ExperimentalContracts::class)
public fun <T, E> Result<T, E>.shouldBeError(): Result.Error<E> {
    contract {
        returns() implies (this@shouldBeError is Result.Error<E>)
    }

    val message =
        "The result type is not as expected. Expected type: `Result.Failure`,  actual type: `Result.Success` ($this)."
    return if (isError()) this else throw failure(message = message)
}
