package main.mapper;

import main.dto.AbstractDTO;
import main.model.AbstractEntity;

public interface Mapper<E extends AbstractEntity, D extends AbstractDTO> {

    E toEntity(D dto);

    D toDTO(E entity);
}
