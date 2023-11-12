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

package io.github.airflux.functional

import io.github.airflux.functional.kotest.shouldBeError
import io.github.airflux.functional.kotest.shouldBeSuccess
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.junit.jupiter.api.assertDoesNotThrow

internal class ResultTest : FreeSpec() {

    companion object {
        private const val ORIGINAL_VALUE = "10"
        private const val ALTERNATIVE_VALUE = "20"
    }

    init {

        "The `Result` type properties" - {

            "the `asNull` property should return the `Result#Success` type with the `null` value" {
                val result = Result.asNull

                result.shouldBeSuccess()
                result.value shouldBe null
            }

            "the `asTrue` property should return the `Result#Success` type with the `true` value" {
                val result = Result.asTrue

                result.shouldBeSuccess()
                result.value shouldBe true
            }

            "the `asFalse` property should return the `Result#Success` type with the `false` value" {
                val result = Result.asFalse

                result.shouldBeSuccess()
                result.value shouldBe false
            }

            "the `asUnit` property should return the `Result#Success` type with the `Unit` value" {
                val result = Result.asUnit

                result.shouldBeSuccess()
                result.value shouldBe Unit
            }

            "the `asEmptyList` property should return the `Result#Success` type with the `empty list` value" {
                val result = Result.asEmptyList

                result.shouldBeSuccess()
                result.value shouldBe emptyList()
            }
        }

        "The `Result` type functions" - {

            "the `of` function" - {

                "when a parameter has the `true` value" - {
                    val param = true

                    "then should return the `Result#Success` type with the `true` value" {
                        val result = Result.of(param)
                        result.value shouldBe true
                    }
                }

                "when a parameter has the `false` value" - {
                    val param = false

                    "then should return the `Result#Success` type with the `true` value" {
                        val result = Result.of(param)
                        result.value shouldBe false
                    }
                }
            }

            "the `isSuccess` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return the true value" {
                        val result: Boolean = original.isSuccess()

                        result shouldBe true
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the false value" {
                        val result: Boolean = original.isSuccess()

                        result shouldBe false
                    }
                }
            }

            "the `isSuccess` function with predicate" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "when the predicate return the true value" - {
                        val predicate: (String) -> Boolean = { it == ORIGINAL_VALUE }

                        "then should return the true value" {
                            val result: Boolean = original.isSuccess(predicate)

                            result shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (String) -> Boolean = { it != ORIGINAL_VALUE }

                        "then should return the true value" {
                            val result: Boolean = original.isSuccess(predicate)

                            result shouldBe false
                        }
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()
                    val predicate: (String) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        val result: Boolean = original.isSuccess(predicate)

                        result shouldBe false
                    }
                }
            }

            "the `isError` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return the false value" {
                        val result: Boolean = original.isError()

                        result shouldBe false
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the true value" {
                        val result: Boolean = original.isError()

                        result shouldBe true
                    }
                }
            }

            "the `isError` function with predicate" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()
                    val predicate: (Error) -> Boolean = { throw IllegalStateException() }

                    "then the predicate is not invoked and should return the false value" {
                        val result: Boolean = original.isError(predicate)

                        result shouldBe false
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "when the predicate return the true value" - {
                        val predicate: (Error) -> Boolean = { it == Error.Empty }

                        "then should return the true value" {
                            val result: Boolean = original.isError(predicate)

                            result shouldBe true
                        }
                    }

                    "when the predicate return the false value" - {
                        val predicate: (Error) -> Boolean = { it != Error.Empty }

                        "then should return the true value" {
                            val result: Boolean = original.isError(predicate)

                            result shouldBe false
                        }
                    }
                }
            }

            "the `fold` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val result = original.fold(ifError = { ALTERNATIVE_VALUE }, ifSuccess = { it })

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the null value" {
                        val result = original.fold(ifError = { ALTERNATIVE_VALUE }, ifSuccess = { it })

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `map` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a result of applying the [transform] function to the value" {
                        val result = original.map { it.toInt() }

                        result.shouldBeSuccess()
                        result.value shouldBe ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return an original do not apply the [transform] function to a value" {
                        val result = original.map { it.toInt() }

                        result shouldBe original
                    }
                }
            }

            "the `bind` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a result of applying the [transform] function to the value" {
                        val result = original.bind { result -> result.toInt().success() }

                        result.shouldBeSuccess()
                        result.value shouldBe ORIGINAL_VALUE.toInt()
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return an original do not apply the [transform] function to a value" {
                        val result = original.bind { success ->
                            success.toInt().success()
                        }

                        result shouldBe original
                    }
                }
            }

            "the `mapError` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return an original do not apply the [transform] function to an error" {
                        val result = original.mapError { Error.Blank }

                        result.shouldBeSuccess()
                        result.value shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return a result of applying the [transform] function to an error" {
                        val result = original.mapError { Error.Blank }

                        result.shouldBeError()
                        result.cause shouldBe Error.Blank
                    }
                }
            }

            "the `onSuccess` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should not anything do" {
                        assertDoesNotThrow {
                            original.onSuccess { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `onError` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should not anything do" {
                        assertDoesNotThrow {
                            original.onError { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then a code block should execute" {
                        shouldThrow<IllegalStateException> {
                            original.onError { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `getOrForward` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val result = original.getOrForward { throw IllegalStateException() }

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.getOrForward { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `recover` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return an original value" {
                        val result = original.recover { ALTERNATIVE_VALUE }

                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the result of invoking the recovery function" {
                        val result = original.recover { ALTERNATIVE_VALUE }

                        result.shouldBeSuccess()
                        result.value shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `recoverWith` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return an original value" {
                        val result = original.recoverWith { ALTERNATIVE_VALUE.success() }

                        result shouldBeSameInstanceAs original
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the result of invoking the recovery function" {
                        val result = original.recoverWith { ALTERNATIVE_VALUE.success() }

                        result.shouldBeSuccess()
                        result.value shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrNull` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val result = original.getOrNull()

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the null value" {
                        val result = original.getOrNull()

                        result.shouldBeNull()
                    }
                }
            }

            "the `getOrElse` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the defaultValue value" {
                        val result = original.getOrElse(ALTERNATIVE_VALUE)

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `getOrElse` function with a predicate" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return a value from a handler" {
                        val result = original.getOrElse { ALTERNATIVE_VALUE }

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `orElse` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val elseResult: Result<String, Error> = ALTERNATIVE_VALUE.success()

                        val result = original.orElse { elseResult }

                        result shouldBe original
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return the defaultValue value" {
                        val elseResult: Result<String, Error> = ALTERNATIVE_VALUE.success()

                        val result = original.orElse { elseResult }

                        result shouldBe elseResult
                    }
                }
            }

            "the `orThrow` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val result = original.orThrow { throw IllegalStateException() }

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should return an exception" {
                        shouldThrow<IllegalStateException> {
                            original.orThrow { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `forEach` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, Error> = ORIGINAL_VALUE.success()

                    "then should thrown exception" {
                        shouldThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, Error> = Error.Empty.error()

                    "then should not thrown exception" {
                        shouldNotThrow<IllegalStateException> {
                            original.forEach { throw IllegalStateException() }
                        }
                    }
                }
            }

            "the `merge` function" - {

                "when a variable has the `Result#Success` type" - {
                    val original: Result<String, String> = ORIGINAL_VALUE.success()

                    "then should return a value" {
                        val result = original.merge()

                        result shouldBe ORIGINAL_VALUE
                    }
                }

                "when a variable has the `Result#Error` type" - {
                    val original: Result<String, String> = ALTERNATIVE_VALUE.error()

                    "then should return the error value" {
                        val result = original.merge()

                        result shouldBe ALTERNATIVE_VALUE
                    }
                }
            }

            "the `sequence` function" - {

                "when a collection is empty" - {
                    val original: List<Result<String, Error>> = listOf()

                    "then should return the value of the asEmptyList property" {
                        val result = original.sequence()

                        result.shouldBeSuccess()
                        result shouldBeSameInstanceAs Result.asEmptyList
                    }
                }

                "when a collection has items only the `Result#Success` type" - {
                    val original: List<Result<String, Error>> =
                        listOf(ORIGINAL_VALUE.success(), ALTERNATIVE_VALUE.success())

                    "then should return a list with all values" {
                        val result = original.sequence()

                        result.shouldBeSuccess()
                        result.value shouldContainExactly listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    }
                }

                "when a collection has a item of the `Result#Error` type" - {
                    val original: List<Result<String, Error>> = listOf(ORIGINAL_VALUE.success(), Error.Empty.error())

                    "then should return the error value" {
                        val result = original.sequence()

                        result.shouldBeError()
                        result.cause shouldBe Error.Empty
                    }
                }
            }

            "the `traverse` function" - {

                "when a collection is empty" - {
                    val original: List<String> = listOf()
                    val transform: (String) -> Result<Int, Error> = { it.toInt().success() }

                    "then should return the value of the asEmptyList property" {
                        val result: Result<List<Int>, Error> = original.traverse(transform)

                        result.shouldBeSuccess()
                        result shouldBeSameInstanceAs Result.asEmptyList
                    }
                }

                "when a transform function returns items only the `Result#Success` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> Result<Int, Error> = { it.toInt().success() }

                    "then should return a list with all transformed values" {
                        val result: Result<List<Int>, Error> = original.traverse(transform)

                        result.shouldBeSuccess()
                        result.value shouldContainExactly listOf(ORIGINAL_VALUE.toInt(), ALTERNATIVE_VALUE.toInt())
                    }
                }

                "when a transform function returns any item of the `Result#Error` type" - {
                    val original: List<String> = listOf(ORIGINAL_VALUE, ALTERNATIVE_VALUE)
                    val transform: (String) -> Result<Int, Error> = {
                        val res = it.toInt()
                        if (res > 10) Error.Empty.error() else res.success()
                    }

                    "then should return the error value" {
                        val result: Result<List<Int>, Error> = original.traverse(transform)

                        result.shouldBeError()
                        result.cause shouldBe Error.Empty
                    }
                }
            }
        }

        "The `success` function should return the `Result#Success` type with the passed value" {
            val result: Result<String, Error.Empty> = ORIGINAL_VALUE.success()

            result.shouldBeSuccess()
            result.value shouldBe ORIGINAL_VALUE
        }

        "The `error` function should return the `Result#Error` type with the passed value" {
            val error: Result<String, Error.Empty> = Error.Empty.error()

            error.shouldBeError()
            error.cause shouldBe Error.Empty
        }
    }

    internal sealed interface Error {
        data object Empty : Error
        data object Blank : Error
    }
}
