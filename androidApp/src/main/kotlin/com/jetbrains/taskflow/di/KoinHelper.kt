package com.jetbrains.taskflow.di

import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

inline fun <reified T> getDependency(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    return GlobalContext.get().get(
        qualifier = qualifier,
        parameters = parameters,
    )
}