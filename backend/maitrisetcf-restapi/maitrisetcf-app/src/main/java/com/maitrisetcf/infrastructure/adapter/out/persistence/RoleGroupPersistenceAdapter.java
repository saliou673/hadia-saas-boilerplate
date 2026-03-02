package com.maitrisetcf.infrastructure.adapter.out.persistence;

import com.maitrisetcf.domain.exceptions.RoleGroupNotFoundException;
import com.maitrisetcf.domain.exceptions.UserNotFoundException;
import com.maitrisetcf.domain.models.rbac.RoleGroup;
import com.maitrisetcf.domain.ports.out.persistenceport.RoleGroupPersistencePort;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.RoleGroupEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.maitrisetcf.infrastructure.adapter.out.persistence.mapper.RoleGroupMapper;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.RoleGroupRepository;
import com.maitrisetcf.infrastructure.adapter.out.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleGroupPersistenceAdapter implements RoleGroupPersistencePort {

    private final RoleGroupRepository roleGroupRepository;
    private final RoleGroupMapper roleGroupMapper;
    private final UserRepository userRepository;

    @Override
    public List<RoleGroup> findAll() {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> roleGroupMapper.toDomain(roleGroupRepository.findAll()),
                "Error fetching all role groups"
        );
    }

    @Override
    public Optional<RoleGroup> findById(Long id) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> roleGroupRepository.findById(id).map(roleGroupMapper::toDomain),
                "Error fetching role group by id"
        );
    }

    @Override
    public Set<RoleGroup> findByNames(Collection<String> names) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> roleGroupMapper.toDomain(roleGroupRepository.findByNameIn(names)),
                "Error fetching role groups by names"
        );
    }

    @Override
    public boolean existsByName(String name) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> roleGroupRepository.existsByName(name),
                "Error checking role group name existence"
        );
    }

    @Override
    @Transactional
    public RoleGroup save(RoleGroup roleGroup) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> roleGroupMapper.toDomain(roleGroupRepository.save(roleGroupMapper.toEntity(roleGroup))),
                "Error saving role group"
        );
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        AdapterPersistenceUtils.executeDbOperation(
                () -> roleGroupRepository.deleteById(id),
                "Error deleting role group"
        );
    }

    @Override
    @Transactional
    public void assignToUser(Long userId, Long roleGroupId) {
        AdapterPersistenceUtils.executeDbOperation(() -> {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
            RoleGroupEntity roleGroup = roleGroupRepository.findById(roleGroupId)
                    .orElseThrow(() -> new RoleGroupNotFoundException("Role group not found with id: " + roleGroupId));
            user.getRoleGroups().add(roleGroup);
            userRepository.save(user);
        }, "Error assigning role group to user");
    }

    @Override
    @Transactional
    public void revokeFromUser(Long userId, Long roleGroupId) {
        AdapterPersistenceUtils.executeDbOperation(() -> {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
            RoleGroupEntity roleGroup = roleGroupRepository.findById(roleGroupId)
                    .orElseThrow(() -> new RoleGroupNotFoundException("Role group not found with id: " + roleGroupId));
            user.getRoleGroups().remove(roleGroup);
            userRepository.save(user);
        }, "Error revoking role group from user");
    }
}
