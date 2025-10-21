package fpt.edu.vn.gms.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String phone;
    private String password;
}