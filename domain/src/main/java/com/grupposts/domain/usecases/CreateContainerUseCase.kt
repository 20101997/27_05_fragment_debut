package com.grupposts.domain.usecases

import com.grupposts.domain.models.Container
import com.grupposts.domain.repositories.ContainerRepository
import com.grupposts.domain.util.InvalidParamsException
import javax.inject.Inject

class CreateContainerUseCase @Inject constructor(private val repository: ContainerRepository) {

    suspend operator fun invoke(travelId: Int?, container: Container?): Container {
        if (travelId == null || container == null) {
            throw InvalidParamsException()
        } else {
            return repository.createContainer(travelId, container)
        }
    }

}