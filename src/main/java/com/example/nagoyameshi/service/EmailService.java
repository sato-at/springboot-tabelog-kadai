package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.entity.VerificationToken.TokenType;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;
    private VerificationTokenRepository verificationTokenRepository;

	public void sendPasswordResetEmail(String to, String resetLink) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("【名古屋メシ】パスワード再設定のお知らせ");
		message.setText("以下のリンクからパスワードを再設定してください。\n\n" + resetLink +
				"\n\nこのリンクの有効期限は1時間です。");

		mailSender.send(message);
	}
	
	 public void deleteExistingTokens(User user, TokenType tokenType) {
	        List<VerificationToken> existingTokens = verificationTokenRepository.findByUserAndTokenType(user, tokenType);
	        verificationTokenRepository.deleteAll(existingTokens);
	    }

	    public VerificationToken save(VerificationToken verificationToken) {
	        return verificationTokenRepository.save(verificationToken);
	    }

	    public VerificationToken findByToken(String token) {
	        return verificationTokenRepository.findByToken(token);
	    }
}
