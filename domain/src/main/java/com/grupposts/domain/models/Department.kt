package com.grupposts.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Department (
    val id : Int?,
    val name : String?,
    val referentName : String?,
    val referentPhone : String?,
    val floor : String?,
    val structureId : Int?,
    val showPosition : Int?,
    val productCategorise : List<String>?
) : Parcelable