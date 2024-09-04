package com.fluffytime.domain.search.service;

import com.fluffytime.domain.user.entity.Profile;
import com.fluffytime.domain.user.entity.User;
import com.fluffytime.domain.user.repository.ProfileRepository;
import com.fluffytime.domain.user.repository.UserRepository;
import com.fluffytime.domain.search.dto.request.SearchRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public List<User> findMatchingUsers(SearchRequest searchRequest) {
        return userRepository.findByNicknameContaining(searchRequest.getQuery());
    }

    @Transactional
    public List<Profile> findMatchingPetName(SearchRequest searchRequest) {
        return profileRepository.findByPetNameContaining(searchRequest.getQuery());
    }

}
