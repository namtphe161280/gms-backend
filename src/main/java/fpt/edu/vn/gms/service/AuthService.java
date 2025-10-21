package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.*;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
    void logout(String token);
    void forgotPassword(ForgotPasswordDto request);
    void resetPassword(ResetPasswordDto request);
}
