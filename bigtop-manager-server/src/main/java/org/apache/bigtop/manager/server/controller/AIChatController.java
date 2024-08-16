package org.apache.bigtop.manager.server.controller;

import org.apache.bigtop.manager.server.enums.ResponseStatus;
import org.apache.bigtop.manager.server.model.converter.PlatformConverter;
import org.apache.bigtop.manager.server.model.dto.PlatformDTO;
import org.apache.bigtop.manager.server.model.req.PlatformReq;
import org.apache.bigtop.manager.server.model.vo.PlatformAuthorizedVO;
import org.apache.bigtop.manager.server.model.vo.PlatformVO;
import org.apache.bigtop.manager.server.service.AIChatService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.annotation.Resource;
import java.util.List;

@Tag(name = "AI Chat Controller")
@RestController
@RequestMapping("/ai/chat/")
public class AIChatController {

    @Resource
    private AIChatService chatService;

    @Operation(summary = "platforms", description = "Get all platforms")
    @GetMapping("/platforms")
    public ResponseEntity<List<PlatformVO>> platforms() {
        return ResponseEntity.success(chatService.platforms());
    }

    @Operation(summary = "platforms", description = "Get authorized platforms")
    @GetMapping("/platforms/authorized")
    public ResponseEntity<List<PlatformAuthorizedVO>> authorizedPlatforms() {
        return ResponseEntity.success(chatService.authorizedPlatforms());
    }

    @Operation(summary = "platforms", description = "Add authorized platforms")
    @PutMapping("/platforms")
    public ResponseEntity<PlatformVO> addAuthorizedPlatform(
            @RequestBody PlatformReq platformReq
    ) {
        PlatformDTO platformDTO = PlatformConverter.INSTANCE.fromReq2DTO(platformReq);
        return ResponseEntity.success(chatService.addAuthorizedPlatform(platformDTO));
    }

    @Operation(summary = "platforms", description = "Delete authorized platforms")
    @DeleteMapping("/platforms/{platformId}")
    public ResponseEntity deleteAuthorizedPlatform(@PathVariable Long platformId) {
        int code = chatService.deleteAuthorizedPlatform(platformId);
        if (code != 0) {
            return ResponseEntity.error(ResponseStatus.PARAMETER_ERROR,"权限不足");
        }
        return ResponseEntity.success();
    }
}
