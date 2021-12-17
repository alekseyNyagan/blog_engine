package main.mapper;

import main.dto.IncompleteUserDTO;
import main.model.User;
import org.springframework.stereotype.Component;

@Component
public class IncompleteUserMapper extends AbstractMapper<User, IncompleteUserDTO> {

    IncompleteUserMapper() {
        super(User.class, IncompleteUserDTO.class);
    }
}
