package com.kpn.falcon.domain.entities

import com.kpn.falcon.data.models.PropertyStatus

object RoleAccessManager {

    fun canEditStoreSpecs(role: UserRole, phase: Int): Boolean =
        role == UserRole.BD_EXECUTIVE && phase == 1

    fun canEditGeoIQ(role: UserRole): Boolean =
        role == UserRole.BD_MANAGER

    fun canEditScoring(role: UserRole): Boolean =
        role == UserRole.STATE_HEAD

    fun canEditCommercials(role: UserRole): Boolean =
        role in listOf(UserRole.BD_HEAD, UserRole.BD_EXECUTIVE)

    fun canApprove(role: UserRole, phase: Int): Boolean = when (phase) {
        3 -> role == UserRole.STATE_HEAD
        4 -> role == UserRole.BD_HEAD
        5 -> role in listOf(UserRole.CEO, UserRole.CFO)
        else -> false
    }

    fun canDelete(role: UserRole, status: PropertyStatus): Boolean = when (status) {
        PropertyStatus.DRAFT -> true
        PropertyStatus.SUBMITTED -> role in listOf(
            UserRole.BD_MANAGER,
            UserRole.STATE_HEAD,
            UserRole.BD_HEAD
        )
        else -> false
    }

    fun canAddProperty(role: UserRole): Boolean =
        role == UserRole.BD_EXECUTIVE

    fun canViewAll(role: UserRole): Boolean =
        role in listOf(UserRole.STATE_HEAD, UserRole.BD_HEAD, UserRole.CEO, UserRole.CFO)
}
