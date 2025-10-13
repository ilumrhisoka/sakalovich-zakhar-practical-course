package inno.project.userservice.mapper;

import inno.project.userservice.model.dto.CardInfoRequest;
import inno.project.userservice.model.dto.CardInfoResponse;
import inno.project.userservice.model.entity.CardInfo;
import inno.project.userservice.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CardInfoMapper {

    @Mapping(source = "userId", target = "user")
    CardInfo toEntity(CardInfoRequest dto);

    @Mapping(source = "user", target = "userId")
    CardInfoResponse toDto(CardInfo entity);

    @Mapping(source = "userId", target = "user")
    void update(@MappingTarget CardInfo entity, CardInfoRequest dto);

    default User map(Long userId) {
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setId(userId);
        return user;
    }

    default Long map(User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }
}