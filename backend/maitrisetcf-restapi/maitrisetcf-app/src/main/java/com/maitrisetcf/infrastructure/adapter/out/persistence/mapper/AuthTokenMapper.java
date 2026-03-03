package com.maitrisetcf.infrastructure.adapter.out.persistence.mapper;

import com.maitrisetcf.domain.models.auth.AuthToken;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AuthTokenEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = UserMapper.class
)
/** MapStruct mapper between {@link com.maitrisetcf.infrastructure.adapter.out.persistence.entity.AuthTokenEntity} and {@link com.maitrisetcf.domain.models.auth.AuthToken}. */
public interface AuthTokenMapper {

    default AuthToken toDomain(AuthTokenEntity entity, @Context UserMapper userMapper) {
        if (entity == null) {
            return null;
        }
        return AuthToken.rehydrate(
                entity.getId(),
                entity.getAccessToken(),
                entity.getRefreshToken(),
                entity.getRememberMe(),
                entity.getExpiryDate(),
                userMapper.toDomain(entity.getUser()),
                entity.getCreationDate()
        );
    }

    @Mapping(target = "creationDate", ignore = true)
    AuthTokenEntity toEntity(AuthToken domain);
}
