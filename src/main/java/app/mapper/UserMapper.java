package app.mapper;

import app.dto.auth.SignUpRequest;
import app.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity signUpDtoToUserEntity(SignUpRequest request);
}
