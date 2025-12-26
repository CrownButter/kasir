package com.dwincomputer.kasir.auth.service;

import com.dwincomputer.kasir.auth.entity.AuditLog;
import com.dwincomputer.kasir.auth.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import jakarta.servlet.http.HttpServletRequest;
@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditRepo;
    private final HttpServletRequest request;
    public void log(String action, String detail) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        auditRepo.save(AuditLog.builder()
                .username(username)
                .action(action)
                .detail(detail)
                .timestamp(LocalDateTime.now())
                .ipAddress(getClientIp())
                .build());
    }
    private String getClientIp() {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-Forwarded-For");
            if (remoteAddr == null || remoteAddr.isEmpty()) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}