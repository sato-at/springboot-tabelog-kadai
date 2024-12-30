package com.example.nagoyameshi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.entity.VerificationToken.TokenType;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class VerificationTokenService {
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
		this.verificationTokenRepository = verificationTokenRepository;
	}

	@Transactional
	public void createVerificationToken(User user, String token) {
		VerificationToken verificationToken = new VerificationToken();

		verificationToken.setUser(user);
		verificationToken.setToken(token);

		verificationTokenRepository.save(verificationToken);
	}

	// トークンの文字列で検索した結果を返す
	public VerificationToken findVerificationTokenByToken(String token) {
		return verificationTokenRepository.findByToken(token);
	}

	
	// パスワード再設定用
	public void deleteExistingTokens(User user, TokenType tokenType) {
		List<VerificationToken> existingTokens = verificationTokenRepository.findByUserAndTokenType(user, tokenType);
		verificationTokenRepository.deleteAll(existingTokens);
	}

	@Transactional
	public VerificationToken save(VerificationToken verificationToken) {
		return verificationTokenRepository.save(verificationToken);
	}

	@Transactional
	public VerificationToken findByToken(String token) {
		return verificationTokenRepository.findByToken(token);
	}

	@Transactional
	public void delete(VerificationToken verificationToken) {
		verificationTokenRepository.delete(verificationToken);
	}

	@Transactional
	public void deleteByUser(User user) {
		verificationTokenRepository.deleteByUser(user);
	}
}