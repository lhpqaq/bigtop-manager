package org.apache.bigtop.manager.server.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.PlatformDTO;
import org.apache.bigtop.manager.server.model.vo.PlatformAuthorizedVO;
import org.apache.bigtop.manager.server.model.vo.PlatformVO;
import org.apache.bigtop.manager.server.service.AIChatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class AIChatServiceImpl implements AIChatService {
    @Override
    public List<PlatformVO> platforms() {
        List<PlatformVO> platforms = new ArrayList<>();
        platforms.add(
                new PlatformVO(1L,"OpenAI","GPT-3.5,GPT-4o")
        );
        platforms.add(
                new PlatformVO(2L,"ChatGLM","GPT-3.5,GPT-4o")
        );
        return platforms;
    }

    @Override
    public List<PlatformAuthorizedVO> authorizedPlatforms() {
        List<PlatformAuthorizedVO> authorizedPlatforms = new ArrayList<>();
        authorizedPlatforms.add(
                new PlatformAuthorizedVO(1L,"OpenAI","sk-xxxxxxxxxxxxx","GPT-3.5,GPT-4o")
        );
        authorizedPlatforms.add(
                new PlatformAuthorizedVO(2L,"ChatGLM","sk-yyyyyyyyyyyy","GPT-4o")
        );
        return authorizedPlatforms;
    }

    @Override
    public PlatformVO addAuthorizedPlatform(PlatformDTO platformDTO) {
        log.info("Adding authorized platform: {}", platformDTO);
        return new PlatformVO(1L,"OpenAI","GPT-3.5,GPT-4o");
    }

    @Override
    public int deleteAuthorizedPlatform(Long platformId) {
        Random random = new Random();
        int randomInt = random.nextInt();
        return randomInt % 2;
    }
}
