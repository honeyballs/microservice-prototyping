package com.example.projectadministration.services

/**
 * Define mapping functions to convert entities to dtos and the other way around
 */
interface MappingService<EntityType, DtoType> {

    fun mapEntityToDto(entity: EntityType): DtoType
    fun mapDtoToEntity(dto: DtoType): EntityType
    fun mapEntitiesToDtos(entities: List<EntityType>): List<DtoType>
    fun mapDtosToEntities(dtos: List<DtoType>): List<EntityType>

}