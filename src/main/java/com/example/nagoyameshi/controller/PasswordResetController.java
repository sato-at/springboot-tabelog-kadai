package com.example.nagoyameshi.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.entity.VerificationToken.TokenType;
import com.example.nagoyameshi.service.EmailService;
import com.example.nagoyameshi.service.UserService;
import com.example.nagoyameshi.service.VerificationTokenService;

@Controller
@RequestMapping("/password")
public class PasswordResetController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private VerificationTokenService verificationTokenService;

	@GetMapping("/reset")
	public String showResetForm(Model model) {
		return "auth/reset-password";
	}

	@PostMapping("/reset")
	public String processResetRequest(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
		User user = userService.findByEmail(email);

		if (user != null) {
			// 既存のトークンを削除
			verificationTokenService.deleteByUser(user);

			String token = UUID.randomUUID().toString();
			VerificationToken verificationToken = new VerificationToken();
			verificationToken.setToken(token);
			verificationToken.setUser(user);
			verificationToken.setTokenType(TokenType.PASSWORD_RESET);
			verificationTokenService.save(verificationToken);

			String resetLink = "http://localhost:8080/password/reset/" + token;
			emailService.sendPasswordResetEmail(email, resetLink);

			redirectAttributes.addFlashAttribute("success", "パスワード再設定用のメールを送信しました");
		} else {
			redirectAttributes.addFlashAttribute("error", "メールアドレスが見つかりません");
		}

		return "redirect:/password/reset";
	}

	@GetMapping("/reset/{token}")
	public String showNewPasswordForm(@PathVariable String token, Model model) {
		VerificationToken verificationToken = verificationTokenService.findByToken(token);

		if (verificationToken != null &&
				verificationToken.getTokenType() == TokenType.PASSWORD_RESET) {
			model.addAttribute("token", token);
			return "auth/new-password";
		}
		return "redirect:/password/reset?error";
	}

	@PostMapping("/reset/{token}")
	public String processNewPassword(@PathVariable String token,
			@RequestParam("password") String password,
			RedirectAttributes redirectAttributes) {
		VerificationToken verificationToken = verificationTokenService.findByToken(token);

		if (verificationToken != null &&
				verificationToken.getTokenType() == TokenType.PASSWORD_RESET) {
			User user = verificationToken.getUser();
			user.setPassword(new BCryptPasswordEncoder().encode(password));
			userService.save(user);

			verificationTokenService.delete(verificationToken);

			redirectAttributes.addFlashAttribute("success", "パスワードを更新しました");
			return "redirect:/login";
		}

		return "redirect:/password/reset?error";
	}
}
