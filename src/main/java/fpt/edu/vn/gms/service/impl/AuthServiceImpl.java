package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.*;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.service.AuthService;
import fpt.edu.vn.gms.utils.JwtUtil;
import fpt.edu.vn.gms.utils.SmsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SmsService smsService;

    public AuthServiceImpl(AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           SmsService smsService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.smsService = smsService;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Account account = accountRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(account.getPhone());
        return new LoginResponseDto(token, account.getPhone(), account.getRole().getRoleName());
    }

    @Override
    public void logout(String token) {
        // Với JWT, logout thực hiện ở client (xóa token)
        // Nếu muốn lưu blacklist token, có thể thêm Redis
    }

    @Override
    public void forgotPassword(ForgotPasswordDto request) {
        Account acc = accountRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        acc.setOtpCode(otp);
        acc.setOtpExpiry(Instant.now().plusSeconds(300).toEpochMilli()); // 5 phút
        accountRepository.save(acc);

        smsService.sendOtp(request.getPhone(), otp);
    }

    @Override
    public void resetPassword(ResetPasswordDto request) {
        Account acc = accountRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("Phone not found"));

        if (acc.getOtpCode() == null || !acc.getOtpCode().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if (Instant.now().toEpochMilli() > acc.getOtpExpiry()) {
            throw new RuntimeException("OTP expired");
        }

        acc.setPassword(passwordEncoder.encode(request.getNewPassword()));
        acc.setOtpCode(null);
        acc.setOtpExpiry(null);
        accountRepository.save(acc);
    }
}
