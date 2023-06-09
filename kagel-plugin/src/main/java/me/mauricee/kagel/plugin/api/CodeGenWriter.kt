package me.mauricee.kagel.plugin.api

import com.squareup.kotlinpoet.FileSpec

interface CodeGenWriter<T> {
    fun write(file: FileSpec, containingFile: T?)
}
