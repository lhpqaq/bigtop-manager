package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.model.dto.PlatformDTO;
import org.apache.bigtop.manager.server.model.vo.PlatformAuthorizedVO;
import org.apache.bigtop.manager.server.model.vo.PlatformVO;

import java.util.List;

public interface AIChatService {
    List<PlatformVO> platforms();

    List<PlatformAuthorizedVO> authorizedPlatforms();

    PlatformVO addAuthorizedPlatform(PlatformDTO platformDTO);

    int deleteAuthorizedPlatform(Long platformId);
}
