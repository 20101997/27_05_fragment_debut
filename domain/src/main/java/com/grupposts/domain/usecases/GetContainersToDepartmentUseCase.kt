package com.grupposts.domain.usecases

import com.grupposts.domain.models.Container
import com.grupposts.domain.models.Department
import com.grupposts.domain.repositories.ContainerRepository
import com.grupposts.domain.util.InvalidParamsException
import javax.inject.Inject

class GetContainersToDepartmentUseCase @Inject constructor(private val repository: ContainerRepository) {

    suspend operator fun invoke(travelId: Int?, departmentTo: Department?): List<Container> {
        if (travelId == null || departmentTo?.id == null) {
            throw InvalidParamsException()
        }
        return repository.getDepartmentContainers(travelId, null, departmentTo)
    }

}