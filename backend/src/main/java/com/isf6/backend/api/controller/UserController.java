package com.isf6.backend.api.controller;

import com.isf6.backend.api.Request.ReportSaveReqDto;
import com.isf6.backend.service.ReportService;
import com.isf6.backend.service.UserService;
import com.isf6.backend.common.oauth.OauthToken;
import com.isf6.backend.config.jwt.JwtProperties;
import com.isf6.backend.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    // 프론트에서 인가코드 받아오는 url
    @GetMapping("/oauth/token")
    public ResponseEntity getLogin(@RequestParam("code") String code) { //(1)
        log.info("code : {} ", code);

        // 넘어온 인가 코드를 통해 access_token 발급
        OauthToken oauthToken = userService.getAccessToken(code);
        log.info("oauthToken : {} ", oauthToken);

        //(2)
        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = userService.saveUserAndGetToken(oauthToken.getAccess_token());
        log.info("jwtToken : {} ", jwtToken);

        //(3)
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
        log.info("headers : {} ", headers);

        //(4)
        return ResponseEntity.ok().headers(headers).body("success");
    }

    //유저 정보 조회
    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) { //(1)
        //(2)
        User user = userService.getUser(request);
        //(3)
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/user/report/{reportedUserCode}")
    public ResponseEntity report(@PathVariable Long reportedUserCode, @RequestBody ReportSaveReqDto reportSaveReqDto) {
        Map<String, Object> response = new HashMap<>();

        reportService.saveReport(reportedUserCode, reportSaveReqDto);
        response.put("reason", "신고 성공");

        return ResponseEntity.status(200).body(response);
    }



}