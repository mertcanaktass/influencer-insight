package com.deneme.influencerinsight.config;

import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.model.RoleEntity;
import com.deneme.influencerinsight.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoleDataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        createRoleIfMissing(RoleType.ROLE_ADMIN);
        createRoleIfMissing(RoleType.ROLE_CUSTOMER);
    }

    private void createRoleIfMissing(RoleType roleType) {
        if (roleRepository.findByRoleType(roleType).isPresent()) {
            return;
        }

        RoleEntity role = RoleEntity.builder()
                .roleType(roleType)
                .build();
        roleRepository.save(role);
    }
}
