package com.learnit.payloads;

import lombok.Data;

@Data
public class ChangePassword {
	private String oldPassword;
	private String newPassword;
}
