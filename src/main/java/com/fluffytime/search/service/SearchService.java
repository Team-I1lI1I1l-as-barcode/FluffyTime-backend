package com.fluffytime.search.service;

import com.fluffytime.domain.Profile;
import com.fluffytime.domain.User;
import com.fluffytime.repository.ProfileRepository;
import com.fluffytime.repository.UserRepository;
import com.fluffytime.search.dto.request.SearchRequestDto;
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
    public List<User> findMatchingUsers(SearchRequestDto searchRequestDto) {
        return userRepository.findByNicknameContaining(searchRequestDto.getQuery());
    }

    @Transactional
    public List<Profile> findMatchingPetName(SearchRequestDto searchRequestDto) {
        return profileRepository.findByPetNameContaining(searchRequestDto.getQuery());
    }

}
